package com.subefu.messu.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.subefu.messu.R
import com.subefu.messu.screen.chat.ChatActivity
import com.subefu.messu.utils.ChatModel
import com.subefu.messu.utils.MessageModel

class ChatsAdapter(val context: Context?, val listChats: ArrayList<ChatModel>)
    :RecyclerView.Adapter<ChatsAdapter.ChatsViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatsViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.model_user, parent, false)
        return ChatsViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatsViewHolder, position: Int) {
        holder.chatName.text = listChats[position].chatName

        holder.itemView.setOnClickListener {
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra("chat_id", listChats[position].id)
            context?.startActivity(intent)
        }
    }

    override fun getItemCount() = listChats.size

    class ChatsViewHolder(view: View): RecyclerView.ViewHolder(view){
        lateinit var chatName: TextView
        init {
            chatName = view.findViewById(R.id.model_user_name)
        }
    }
}