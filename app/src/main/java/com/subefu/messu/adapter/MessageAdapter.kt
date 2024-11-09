package com.subefu.messu.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.subefu.messu.R
import com.subefu.messu.utils.MessageModel

class MessageAdapter(val context: Context, val listMessage: List<MessageModel>)
    :RecyclerView.Adapter<MessageAdapter.MessageViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = LayoutInflater.from(context).inflate(viewType, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = listMessage[position]

        holder.text.text = message.text
        holder.timeSend.text = message.timeSend
    }

    override fun getItemViewType(position: Int): Int {
        if(listMessage[position].uid == FirebaseAuth.getInstance().currentUser!!.uid)
            return R.layout.message_mine_model
        else
            return R.layout.message_your_model
    }

    override fun getItemCount() = listMessage.size

    class MessageViewHolder(view: View): RecyclerView.ViewHolder(view){
        lateinit var text: TextView
        lateinit var timeSend: TextView
        init{
            text = view.findViewById(R.id.message_text)
            timeSend = view.findViewById(R.id.message_time)
        }
    }
}