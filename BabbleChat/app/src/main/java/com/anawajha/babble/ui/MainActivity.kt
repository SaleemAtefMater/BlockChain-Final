package com.anawajha.babble.ui

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.anawajha.babble.R
import com.anawajha.babble.databinding.ActivityMainBinding
import com.anawajha.babble.databinding.DialogCreateGroupBinding
import com.anawajha.babble.logic.adapter.GroupAdapter
import com.anawajha.babble.logic.adapter.UserAdapter
import com.anawajha.babble.logic.model.*
import com.anawajha.babble.logic.socket.SocketCreate
import com.anawajha.babble.shared.Constants
import com.github.nkzawa.emitter.Emitter
import com.github.nkzawa.socketio.client.Socket
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import org.json.JSONArray
import org.json.JSONObject
import java.util.*


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val user = Firebase.auth.currentUser
    var users = mutableListOf<User>()
    var groups = mutableListOf<Group>()
    var membergroups = mutableListOf<User>()
    var pbt: Long = 0
    var groupName = "saleem"
    lateinit var app: SocketCreate
    private var mSocket: Socket? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        app = application as SocketCreate
        mSocket = app.getSocket()

        mSocket!!.connect()
        val blockChain = BlockChain()

//        val block = Block("aa1", Message("saleem","sss","saleemmmm"),0,0,"sss")
//        val block1 = Block("aa", Message("saleem","sss","saleemmmm"),0,0,"ssssss")
//        blockChain.addBlock(block)
//        blockChain.addBlock(block1)
//        blockChain.chain.forEach { Log.e("aaa","it.hash ${it.hash} it.previousHash ${it.previousHash} ${it.messages.forEach { ms ->
//            Log.e("aaa","ms ${ms}")
//        }}") }
//        blockChain.chain.forEach { Log.e("aaa",it.hash) }

        Picasso.get().load(user?.photoUrl).placeholder(R.drawable.ic_user)
            .into(binding.imgGoToProfile)

        binding.btnNewGroup.setOnClickListener {

            val dialogBinding = DialogCreateGroupBinding.inflate(layoutInflater)
            val dialog = Dialog(this)
            dialog.setContentView(dialogBinding.root)
            dialog.setCancelable(false)

            dialogBinding.tvCancel.setOnClickListener {
                dialog.dismiss()
            }

            dialogBinding.tvCreate.setOnClickListener {
                groupName = dialogBinding.edGroupName.text.toString()
                if (groupName.isNotEmpty()) {
                    dialogBinding.tfGroupName.error = null
                    //  emit   Constants.addedUsers

                    var userss =
                        User(
                            user!!.uid,
                            user.displayName!!,
                            user.email!!,
                            user.photoUrl.toString(),
                            status = true,
                            typing = false
                        )

                    var group = Group(UUID.randomUUID().toString(), groupName, userss)
                    mSocket!!.emit("create-group", group.encode())
                    Constants.addedUsers.clear()
                    dialog.dismiss()
                    startActivity(Intent(this, GroupChat::class.java))
                } else {
                    dialogBinding.tfGroupName.error = "Group name should'nt be empty"
                }
            }

            dialog.show()
        }

        binding.imgGoToProfile.setOnClickListener {
            startActivity(Intent(this, Profile::class.java))
        }


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
        /* View All User*/
        mSocket!!.on("new-users", Emitter.Listener { args ->
            users.clear()

            val a = args[0] as JSONArray
            runOnUiThread {
                for (i in 0 until a.length()) {
                    val u = User.decode(a[i] as JSONObject)
                    user?.let {
                        if (u.id != user!!.uid) {
                            users.add(u)
                        }
                        users = users.distinctBy {
                            it.id
                        } as MutableList<User>

                    }
                }
                val adapter = UserAdapter(this, users)
                adapter.notifyItemInserted(0)
                binding.rvAllUsers.layoutManager = LinearLayoutManager(this)
                binding.rvAllUsers.adapter = adapter

            }
        })
        /* view offline */
        mSocket!!.on("disconnect_user_emit", Emitter.Listener { sss ->
            val user_dis = sss[0] as JSONObject
            runOnUiThread {
                val u = User.decode(user_dis)
                user?.let {
                    if (u.id != user.uid) {
                        users.add(u)
                    }
                }
            }

        })
        mSocket!!.emit("user-view")
        mSocket!!.emit("group-view")
        mSocket!!.emit("group-members_view")



        /* View Group User*/
        mSocket!!.on("group-members_emit", Emitter.Listener { args ->
            membergroups.clear()
            val a = args[0] as JSONArray
            runOnUiThread {
                for (i in 0 until a.length()) {
                    val u = User.decode(a[i] as JSONObject)
                    membergroups.add(u)
                }
                membergroups.forEach {
                    Log.d("mmmmm","member ${it.name}")
                }
                Log.d("mmmmm","membergroups.size ${membergroups.size}")


            }
        })

        /* View All Group*/
        mSocket!!.on("group-name", Emitter.Listener { args ->
            groups.clear()
            val a = args[0] as JSONArray
            runOnUiThread {
                for (i in 0 until a.length()) {
                    val u = Group.decode(a[i] as JSONObject)
                        groups.add(u)

                    groups = groups.distinctBy {
                        it.id
                    } as MutableList<Group>


                }
                val adapter = GroupAdapter(this, groups)
                binding.rvGroup.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
                binding.rvGroup.adapter = adapter


            }
        })

    }// onCreate



    override fun onBackPressed() {
        pbt = System.currentTimeMillis()
        if (pbt + 2000 > System.currentTimeMillis()) {
            user?.let {
                var userss =
                    User(
                        user!!.uid,
                        user.displayName!!,
                        user.email!!,
                        user.photoUrl.toString(),
                        status = false,
                        typing = false
                    )
                mSocket!!.emit("disconnect_user", userss.encode())
                Log.d("gff", "${userss.encode()}")
            }
            super.onBackPressed()
        }
    }
}// MainActivity class