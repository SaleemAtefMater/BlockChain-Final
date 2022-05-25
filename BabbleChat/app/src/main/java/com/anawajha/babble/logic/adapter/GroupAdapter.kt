package com.anawajha.babble.logic.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.anawajha.babble.R
import com.anawajha.babble.databinding.PeopleGroupItemBinding
import com.anawajha.babble.databinding.UserItemBinding
import com.anawajha.babble.logic.model.Group
import com.anawajha.babble.logic.model.User
import com.anawajha.babble.logic.socket.SocketCreate
import com.anawajha.babble.shared.Constants
import com.anawajha.babble.ui.Chat
import com.anawajha.babble.ui.GroupChat
import com.github.nkzawa.socketio.client.IO
import com.github.nkzawa.socketio.client.Socket
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso


data class GroupAdapter(var activity: Activity, var group:MutableList<Group>):RecyclerView.Adapter<GroupAdapter.GroupViewHolder>() {

    class GroupViewHolder(binding: PeopleGroupItemBinding):RecyclerView.ViewHolder(binding.root) {
        var groupName = binding.tvUserName
        var image = binding.imgAddedUser
        var cart = binding.cvGroupChat


    }// GroupViewHolder class

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val binding = PeopleGroupItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return GroupAdapter.GroupViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        Picasso.get().load(group[position].id/**/).placeholder(R.drawable.ic_group).into(holder.image)
        holder.groupName.text = group[position].groupName

        holder.cart.setOnClickListener {
            val i = Intent(activity,GroupChat::class.java)
            i.putExtra("group_id",group[position].id)
            activity.startActivity(i)
        }
    }

    override fun getItemCount(): Int {
        return group.size
    }

}