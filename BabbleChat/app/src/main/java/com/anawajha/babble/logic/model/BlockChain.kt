package com.anawajha.babble.logic.model

import android.util.Log
import com.anawajha.babble.logic.socket.SocketCreate
import com.anawajha.babble.shared.Constants
import com.github.nkzawa.socketio.client.IO
import com.github.nkzawa.socketio.client.Socket

class BlockChain {

    val chain: MutableList<Block> = mutableListOf()
    private val difficulty = 2
    private val validPrefix = "0".repeat(difficulty)
    private var mSocket :Socket? = IO.socket("http://${Constants.IP}:8080")

    fun addBlock(block: Block): Block {
        mSocket!!.connect()
        val minedBlock = if (isMined(block)) block else mineBlock(block)
        if (chain.size == 0){
            chain.add(minedBlock)
        }else{
            for (i in 1..chain.size){
                minedBlock.previousHash = chain[i-1].hash
            }

            chain.add(minedBlock)
        }
        mSocket!!.emit(
            "chain",
            chain
        )
        return minedBlock
    }

    private fun isMined(block: Block): Boolean {
        return block.hash.startsWith(validPrefix)
    }

    fun mineBlock(block: Block): Block {

        var minedBlock = block.copy()

        while (!isMined(minedBlock)) {
            minedBlock = minedBlock.copy(nonce = minedBlock.nonce + 1)
        }

        return minedBlock
    }


    fun isValid(): Boolean {
        when {
            chain.isEmpty() -> return true
            chain.size == 1 -> return chain[0].hash == chain[0].calculateHash()
            else -> {
                for (i in 1 until chain.size) {
                    val previousBlock = chain[i - 1]
                    val currentBlock = chain[i]

                    when {
                        currentBlock.hash != currentBlock.calculateHash() -> return false
                        currentBlock.previousHash != previousBlock.calculateHash() -> return false
                        !(isMined(previousBlock) && isMined(currentBlock)) -> return false
                    }
                }
                return true
            }
        }
    }

}