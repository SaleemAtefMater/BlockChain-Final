package com.anawajha.babble.logic.model

import com.anawajha.babble.shared.Constants
import org.json.JSONObject
import java.math.BigInteger
import java.security.MessageDigest


data class Block(
    var previousHash: String = "",
    var messages: JSONObject,
    var timestamp: Long = System.currentTimeMillis(),
    var nonce: Long = 0,
    var hash: String = ""
){
    fun encode(): JSONObject {
        var block= JSONObject()
        block.put("previousHash",this.previousHash)
        block.put("messages",this.messages)
        block.put("timestamp",this.timestamp)
        block.put("nonce",this.nonce)
        block.put("hash",this.hash)
        /* message.put(Constants.IMAGE_MESSAGE, this.imageMessage)*/
        return block
    }// encode

//    fun decode(obj:JSONObject):Block{
//        return Block(obj.getString(previousHash), obj.get("messages") as Message,obj.getLong("timestamp")
//            ,obj.getLong("nonce"),obj.getString("hash"))
//    }// decode

//    var previousHash: String = ""
//    var messages: MutableList<Message> = mutableListOf()
//    var timestamp: Long = System.currentTimeMillis()
//    var nonce: Long = 0
//    var hash: String = ""
//    constructor()
//    constructor(previousHash:String,messages:Message,timestamp: Long,nonce: Long,hash: String){
//        this.previousHash = previousHash
//        this.messages.add(messages)
//        this.timestamp =  timestamp
//        this.nonce = nonce
//        this.hash = hash
//
//    }
    init {
        hash = calculateHash()
    }

    fun calculateHash(): String {
        return "$previousHash$messages$timestamp$nonce".hash()
    }



}
fun String.hash(algorithm: String = "SHA-256"): String {
    val messageDigest = MessageDigest.getInstance(algorithm)
    messageDigest.update(this.toByteArray())
    return String.format("%064x", BigInteger(1, messageDigest.digest()))

}