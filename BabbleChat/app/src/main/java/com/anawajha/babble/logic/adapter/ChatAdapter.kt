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
import com.anawajha.babble.databinding.ChatItemBinding
import com.anawajha.babble.logic.model.Message



class ChatAdapter(var activity: Activity, var messages:ArrayList<Message>,var userId:String?):RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {
    class ChatViewHolder(binding:ChatItemBinding):RecyclerView.ViewHolder(binding.root){
        var imageMessage = binding.imgMessage
        var message = binding.tvMessage
        var container = binding.loMessageContainer
        var parentContainer = binding.parentLayout
    }// MessageViewHolder

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val binding = ChatItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ChatViewHolder(binding)
    }// onCreateViewHolder

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.message.text = messages[position].message!!
        userId?.let {
            if (messages[position].source_id.equals(userId)){
                holder.container.setBackgroundResource(R.drawable.sender_item)
                holder.message.setTextColor(Color.parseColor("#FFBB86FC"))
                holder.parentContainer.gravity = Gravity.END

        //        if (messages[position].message ) {
                    holder.message.text = messages[position].message!!
                    holder.message.visibility = View.VISIBLE
                    holder.imageMessage.visibility = View.GONE

   //             }
//                else if(messages[position].imageMessage != null){
//                    holder.imageMessage.setImageBitmap(ImageOperations.getImage(messages[position].imageMessage!!))
//                    holder.message.visibility = View.GONE
//                    holder.imageMessage.visibility = View.VISIBLE
//                }

            }else{
                holder.container.setBackgroundResource(R.drawable.recevier_item)
                holder.message.setTextColor(Color.parseColor("#FFFFFF"))
                holder.parentContainer.gravity = Gravity.START
            }
        }
    }// onBindViewHolder

    override fun getItemCount(): Int {
       return messages.size
    }// getItemCount
}// MessageAdapter