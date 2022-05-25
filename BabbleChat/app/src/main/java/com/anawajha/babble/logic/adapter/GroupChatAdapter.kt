package com.anawajha.babble.logic.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.anawajha.babble.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.anawajha.babble.databinding.ChatGroupItemBinding
import com.anawajha.babble.logic.model.GroupMessage


class GroupChatAdapter(var activity: Activity, var messages:ArrayList<GroupMessage>):RecyclerView.Adapter<GroupChatAdapter.GroupChatViewHolder>() {
    class GroupChatViewHolder(binding:ChatGroupItemBinding):RecyclerView.ViewHolder(binding.root){
        var imageMessage = binding.imgMessage
        var message = binding.tvMessage
        var container = binding.loMessageContainer
        var senderName = binding.tvSenderName
        var parentContainer = binding.parentLayout
    }// MessageViewHolder

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupChatViewHolder {
        val binding = ChatGroupItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return GroupChatViewHolder(binding)
    }// onCreateViewHolder

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: GroupChatViewHolder, position: Int) {
        val userId:String? = Firebase.auth.currentUser?.uid
        holder.message.text = messages[position].message!!
        userId?.let {
            if (messages[position].source_id.equals(userId)){
                holder.container.setBackgroundResource(R.drawable.sender_item)
                holder.message.setTextColor(Color.parseColor("#FFBB86FC"))
                holder.parentContainer.gravity = Gravity.END
                holder.senderName.visibility = View.GONE


//                if (messages[position].message != null){
                    holder.message.visibility = View.VISIBLE
                    holder.imageMessage.visibility = View.GONE
//
//                }else if(messages[position].imageMessage != null){
//                    holder.imageMessage.setImageBitmap(ImageOperations.getImage(messages[position].imageMessage!!))
//                    holder.message.visibility = View.GONE
//                    holder.imageMessage.visibility = View.VISIBLE
//                }

            }else if (messages[position].message!!.contains("join")){
                holder.container.setBackgroundResource(R.color.white)
                holder.message.setTextColor(Color.parseColor("#000000"))
                holder.parentContainer.gravity = Gravity.CENTER
                holder.senderName.visibility = View.GONE
            }else if (messages[position].message!!.contains("leave")){
                holder.container.setBackgroundResource(R.color.white)
                holder.message.setTextColor(Color.parseColor("#000000"))
                holder.parentContainer.gravity = Gravity.CENTER
                holder.senderName.visibility = View.GONE
            }
            else{
                holder.container.setBackgroundResource(R.drawable.recevier_item)
                holder.message.setTextColor(Color.parseColor("#FFFFFF"))
                holder.parentContainer.gravity = Gravity.START
                holder.senderName.text = messages[position].source_name
                holder.senderName.visibility = View.VISIBLE

            }

        }
    }// onBindViewHolder

    override fun getItemCount(): Int {
       return messages.size
    }// getItemCount
}// MessageAdapter