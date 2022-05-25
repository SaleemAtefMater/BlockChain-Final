package com.anawajha.babble.logic.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.anawajha.babble.R
import com.anawajha.babble.databinding.UserItemBinding
import com.anawajha.babble.logic.model.GroupMessage
import com.anawajha.babble.logic.model.User
import com.anawajha.babble.logic.socket.SocketCreate
import com.anawajha.babble.shared.Constants
import com.anawajha.babble.ui.GroupChat
import com.github.nkzawa.socketio.client.IO
import com.github.nkzawa.socketio.client.Socket
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import java.util.*

class GroupUserAdapter(var activity: Activity, var users:MutableList<User>,var group_id:String):RecyclerView.Adapter<GroupUserAdapter.UserViewHolder>() {
    lateinit var app: SocketCreate
    private var mSocket: Socket? = null
    private val user = Firebase.auth.currentUser

    class UserViewHolder(binding:UserItemBinding):RecyclerView.ViewHolder(binding.root) {
        var image = binding.imgUser
        var userName = binding.tvUserName
        var lastMessage  = binding.tvLastMessage
        var status = binding.tvStatus
        var messageTime = binding.tvTime
        var card = binding.cvUserChat
    }// UserViewHolder class

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = UserItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return UserViewHolder(binding)
    }// onCreateViewHolder

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        Picasso.get().load(users[position].image).placeholder(R.drawable.ic_user).into(holder.image)
        holder.userName.text = users[position].name
        holder.card.setOnClickListener {
            mSocket = IO.socket("http://${Constants.IP}:8080")
            mSocket!!.connect()
            user?.let {
                var userss =
                    User(
                        users[position].id,
                        users[position].name,
                        users[position].email,
                        users[position].image,
                        status = true,
                        typing = false
                    )
                mSocket!!.emit("user-join_group", userss.encode())
                val i = Intent(activity,GroupChat::class.java)
                i.putExtra("group_id111",group_id)
                i.putExtra("username",users[position].name)
                activity.startActivity(i)
            }
            val message = GroupMessage(
                " ",
                " ",
                "${users[position].name} join",
                group_id
            )
            mSocket!!.emit(
                Constants.GROUP_MESSAGES,
                message.encode()
            )
        }
    }// onBindViewHolder

    override fun getItemCount(): Int {
        return users.size
    }// getItemCount


}// UserAdapter class