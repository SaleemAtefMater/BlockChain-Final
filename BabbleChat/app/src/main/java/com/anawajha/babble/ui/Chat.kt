package com.anawajha.babble.ui

import android.annotation.SuppressLint
import android.app.ActionBar.DISPLAY_SHOW_CUSTOM
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.format.DateUtils
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.anawajha.babble.R
import com.anawajha.babble.databinding.ActivityChatBinding
import com.anawajha.babble.logic.adapter.ChatAdapter
import com.anawajha.babble.logic.model.Block
import com.anawajha.babble.logic.model.BlockChain
import com.anawajha.babble.logic.model.Message
import com.anawajha.babble.logic.model.User
import com.anawajha.babble.logic.socket.ImageOperations
import com.anawajha.babble.logic.socket.SocketCreate
import com.anawajha.babble.shared.Constants
import com.github.dhaval2404.imagepicker.ImagePicker
import com.github.nkzawa.emitter.Emitter
import com.github.nkzawa.socketio.client.IO
import com.github.nkzawa.socketio.client.Socket
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.log


class Chat : AppCompatActivity() {
    private lateinit var binding:ActivityChatBinding
    lateinit var app: SocketCreate
    private var mSocket: Socket? = null
    private var user = Firebase.auth.currentUser
    private var userId:String? = null
    private var destUser:String? = null
    lateinit var adapter:ChatAdapter
    var message_array = ArrayList<Message>()
    var chain: MutableList<Block> = mutableListOf()
    lateinit var blockChain: BlockChain
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        app = application as SocketCreate
        mSocket = app.getSocket()
        blockChain = BlockChain()
        chain = mutableListOf()
        this.setSupportActionBar(binding.toolbar)
        binding.btnBack.setOnClickListener {
            finish()
        }

        mSocket!!.connect()

        destUser = intent.getStringExtra(Constants.DESTINATION_ID)
        Picasso.get().load(intent.getStringExtra("image")).placeholder(R.drawable.ic_user).into(binding.imgSender)
        binding.tvSenderName.text = intent.getStringExtra("name")

        user.let {
            userId = it!!.uid
        }

          adapter = ChatAdapter(this,message_array,userId)

        isTyping()

        val newMessages = Emitter.Listener {
            var messages = it[0] as JSONArray
            runOnUiThread {
                message_array.clear()
                for(i in 0 until messages.length()){
                    val m = messages[i] as JSONObject
                          var ss =   m.getJSONObject("messages")
                    val message = Message(ss.getString(Constants.SOURCE_ID),ss.getString(Constants.DESTINATION_ID),ss.getString(Constants.MESSAGE))


                    val block = Block(m.getString("previousHash"),
                        message.encode(),m.getLong("timestamp"),m.getLong("nonce"),m.getString("hash"))
                   val saleem =  Message(block.messages.getString(Constants.SOURCE_ID),block.messages.getString(Constants.DESTINATION_ID),block.messages.getString(Constants.MESSAGE))
                    if(((saleem.destination_id == userId && saleem.source_id ==  destUser) || (saleem.destination_id == destUser && saleem.source_id == userId) )) {
                        message_array.add(saleem)
                    }
                    binding.rvMessage.adapter = adapter
                    binding.rvMessage.layoutManager = LinearLayoutManager(this)

                }

            }
        }
        newMessages




        mSocket!!.on("user-isTyping_emit", Emitter.Listener { sss ->
            val user_typing = sss[0] as JSONObject
            runOnUiThread {
                val u = User.decode(user_typing)
                user?.let {
                    if (u.id != user!!.uid) {
                        binding.isTyping.text = "isTyping..."
                    }else{
                        binding.isTyping.text = " "
                    }
                }
            }

        })


        binding.btnCaptureImage.setOnClickListener {
            ImagePicker.with(this)
                .cameraOnly()
                .compress(1024)
                .start()
        }

        binding.btnChooseImage.setOnClickListener {
            ImagePicker.with(this)
                .galleryOnly()
                .compress(1024)
                .start()
        }


        mSocket!!.on(Socket.EVENT_CONNECT_ERROR) {
            runOnUiThread {
                Log.e("EVENT_CONNECT_ERROR", "EVENT_CONNECT_ERROR: ",)
            }
        }

        mSocket!!.on(Socket.EVENT_CONNECT_TIMEOUT,  Emitter.Listener {
            runOnUiThread {
                Log.e("EVENT_CONNECT_TIMEOUT", "EVENT_CONNECT_TIMEOUT: ", )

            }
        })


        mSocket!!.on(
            Socket.EVENT_CONNECT
        ) { Log.e("onConnect", "Socket Connected!") }
        mSocket!!.on(Socket.EVENT_DISCONNECT, Emitter.Listener {
            runOnUiThread {
                Log.e("onDisconnect", "Socket onDisconnect!")

            }
        })

        mSocket!!.emit("user-view")
        mSocket!!.on("newMessage", newMessages)
        mSocket!!.connect()

        blockChain.chain.add(Block("", JSONObject(),0,0,""))
        binding.btnSend.setOnClickListener {
            val message = Message(userId!!, destUser!!, binding.edMessage.text.toString())
            val block = blockChain.addBlock(Block("",message.encode(),System.currentTimeMillis(),0,""))
            mSocket!!.emit(
                Constants.MESSAGE,
                block.encode()
            )
            Log.e("aa","size ${chain.size}")
            newMessages
            //  adapter.notifyItemInserted(message_array.size)
            binding.edMessage.text.clear()
        }

    }// onCreate

    private fun isTyping(){

        binding.edMessage.doOnTextChanged { text, start, before, count ->
            if (text!!.isNotEmpty()){
                user?.let {
                    var userss =
                        User(
                            user!!.uid,
                            user!!.displayName!!,
                            user!!.email!!,
                            user!!.photoUrl.toString(),
                            status = true,
                            typing = true
                        )
                    Log.d("ffff","userid ${userss.id}  typing ${userss.typing}")
                    mSocket!!.emit("user-isTyping", userss.encode())

                }
            }else{
                binding.isTyping.text = " "
            }
        }
    }

    private fun sendTextMessage() {
        binding.edMessage.doOnTextChanged { text, start, before, count ->
            if (text!!.isNotEmpty()){
               if (userId != null && destUser != null){
                   binding.edMessage.isEnabled = true
                   mSocket!!.emit(Constants.MESSAGE, Message(userId!!,destUser!!,binding.edMessage.text.toString()))
               }
            }else{
                binding.edMessage.isEnabled = false
            }
        }
    }// sendMessage

//    private fun sendImageMessage(image:ByteArray){
//        if (userId != null && destUser != null){
//            binding.edMessage.isEnabled = true
//            mSocket!!.emit(Constants.MESSAGE, Message(userId!!,destUser!!,null ,image))
//        }
//    }// sendImageMessage


//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        if (item.itemId == android.R.id.home){
//            finish()
//            return true
//        }
//        return super.onOptionsItemSelected(item)
//    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Activity.RESULT_OK){

            val bitmap:Bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver,data?.data)

    //        sendImageMessage(ImageOperations.getBitmapAsByteArray(bitmap))

        }else if(requestCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this,"Something went error",Toast.LENGTH_SHORT).show()
        }
    }// onActivityResult
}// class