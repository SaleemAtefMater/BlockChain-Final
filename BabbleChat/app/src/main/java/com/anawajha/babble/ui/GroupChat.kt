package com.anawajha.babble.ui

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.LinearLayoutManager
import com.anawajha.babble.R
import com.anawajha.babble.databinding.ActivityCreateGroupBinding
import com.anawajha.babble.databinding.ActivityGroupChatBinding
import com.anawajha.babble.logic.adapter.ChatAdapter
import com.anawajha.babble.logic.adapter.GroupChatAdapter
import com.anawajha.babble.logic.model.*
import com.anawajha.babble.logic.socket.SocketCreate
import com.anawajha.babble.shared.Constants
import com.github.nkzawa.emitter.Emitter
import com.github.nkzawa.socketio.client.Socket
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
class GroupChat : AppCompatActivity() {
    private lateinit var binding: ActivityGroupChatBinding
    private var user = Firebase.auth.currentUser
    private var userId: String? = null
    private var userName: String? = null
    val groupName = ""
    var message_array = ArrayList<GroupMessage>()
    lateinit var app: SocketCreate
    private var mSocket: Socket? = null
    lateinit var group_id:String
    lateinit var username:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroupChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        group_id = intent.getStringExtra("group_id").toString()
        username = intent.getStringExtra("username").toString()
        app = application as SocketCreate
        mSocket = app.getSocket()

        binding.btnBack.setOnClickListener {
            finish()
        }

        user?.let {
            userId = it.uid
            userName = it.displayName
        }

        mSocket!!.on(Socket.EVENT_CONNECT_ERROR) {
            runOnUiThread {
                Log.e("EVENT_CONNECT_ERROR", "EVENT_CONNECT_ERROR: ")
            }
        }

        mSocket!!.on(Socket.EVENT_CONNECT_TIMEOUT, Emitter.Listener {
            runOnUiThread {
                Log.e("EVENT_CONNECT_TIMEOUT", "EVENT_CONNECT_TIMEOUT: ")

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
        mSocket!!.on("newGroupMessage", newMessages)
        mSocket!!.connect()


        binding.btnSend.setOnClickListener {
            //sendTextMessage()
            val message = GroupMessage(
                userId!!,
                userName!!,
                binding.edMessage.text.toString(),
                UUID.randomUUID().toString()
            )
            mSocket!!.emit(
                Constants.GROUP_MESSAGES,
                message.encode()
            )
            message_array.add(message)
            binding.edMessage.text.clear()
        }

        binding.btnMore.setOnClickListener {
            val popupMenu: PopupMenu = PopupMenu(this,binding.btnMore)
            popupMenu.menuInflater.inflate(R.menu.group_main_menu,popupMenu.menu)
            popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
                when(item.itemId) {
                    R.id.mi_add_new_member -> addNewMember()
                    R.id.mi_exit -> exitGroup()
                }
                true
            })
            popupMenu.show()
        }

    }





    fun addNewMember() {
        val i = Intent(this,CreateGroup::class.java)
        i.putExtra("group_id12",group_id)
        startActivity(i)
    }

    fun exitGroup() {
        userId?.let {
            val message = GroupMessage(
                " ",
                " ",
                "${user!!.displayName} leave",
                group_id
            )
            mSocket!!.emit(
                Constants.GROUP_MESSAGES,
                message.encode()
            )
            mSocket!!.emit("leave_group",userId)
            finish()
        }
    }

    val newMessages = Emitter.Listener {
        var messages = it[0] as JSONArray
        runOnUiThread {
            message_array.clear()
            for (i in 0 until messages.length()) {
                val m = messages[i] as JSONObject
                val message = GroupMessage(
                    m.getString(Constants.SOURCE_ID),
                    m.getString(Constants.SOURCE_NAME),
                    m.getString(Constants.MESSAGE),
                    m.getString("groupId")
                )
//                if (group_id == message.groupId){
                message_array.add(message)
//                }
                Log.d("gggg","group_id ${group_id}")
                Log.d("gggg","message.groupId ${message.groupId}")
                val adapter = GroupChatAdapter(this, message_array)
                binding.rvMessage.adapter = adapter
                binding.rvMessage.layoutManager = LinearLayoutManager(this)
            }
        }
    }
}