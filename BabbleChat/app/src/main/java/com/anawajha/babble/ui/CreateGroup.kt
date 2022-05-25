package com.anawajha.babble.ui

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.anawajha.babble.R
import com.anawajha.babble.databinding.ActivityCreateGroupBinding
import com.anawajha.babble.databinding.DialogCreateGroupBinding
import com.anawajha.babble.logic.adapter.GroupUserAdapter
import com.anawajha.babble.logic.adapter.UserAdapter
import com.anawajha.babble.logic.adapter.UserAddedAdapter
import com.anawajha.babble.logic.model.User
import com.anawajha.babble.logic.socket.SocketCreate
import com.anawajha.babble.shared.Constants
import com.github.nkzawa.emitter.Emitter
import com.github.nkzawa.socketio.client.Socket
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.json.JSONArray
import org.json.JSONObject
import android.view.ViewGroup
import com.anawajha.babble.logic.model.Group


class CreateGroup : AppCompatActivity() {
    private lateinit var binding: ActivityCreateGroupBinding
    private var users = mutableListOf<User>()
    private val currentUser = Firebase.auth.currentUser
    lateinit var app: SocketCreate
    private var mSocket: Socket? = null
    var groupName = "saleem"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateGroupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val group_id = intent.getStringExtra("group_id12")
        app = application as SocketCreate
        mSocket = app.getSocket()
        mSocket!!.connect()

        mSocket!!.on(Socket.EVENT_CONNECT_ERROR) {
            runOnUiThread {
                Log.e("EVENT_CONNECT_ERROR", "EVENT_CONNECT_ERROR: ")
            }
        };
        mSocket!!.on(Socket.EVENT_CONNECT_TIMEOUT, Emitter.Listener {
            runOnUiThread {
                Log.e("EVENT_CONNECT_TIMEOUT", "EVENT_CONNECT_TIMEOUT: ")
            }
        })

        mSocket!!.on(
            Socket.EVENT_CONNECT
        ) { Log.e("onConnect", "Socket Connected!") };
        mSocket!!.on(Socket.EVENT_DISCONNECT, Emitter.Listener {
            runOnUiThread {
                Log.e("onDisconnect", "Socket onDisconnect!")

            }
        })


        mSocket!!.emit("user-view")
        //users.clear()
        mSocket!!.on("new-users", Emitter.Listener { args ->
            users.clear()

            val a = args[0] as JSONArray
            runOnUiThread {
                for (i in 0 until a.length()) {
                    val u = User.decode(a[i] as JSONObject)
                    currentUser?.let {
                        if (u.id != currentUser!!.uid) {
                            users.add(u)
                        }
                        users = users.distinctBy {
                            it.id
                        } as MutableList<User>

                    }
                }
                val adapter = GroupUserAdapter(this, users,group_id!!)
                binding.rvUsers.layoutManager = LinearLayoutManager(this)
                binding.rvUsers.adapter = adapter

            }
        })
    }// onCreate
}// CreateGroup class