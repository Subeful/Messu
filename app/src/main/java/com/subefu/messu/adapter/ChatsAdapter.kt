package com.subefu.messu.adapter

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.subefu.messu.R
import com.subefu.messu.screen.chat.ChatActivity
import com.subefu.messu.utils.ChatModel
import com.subefu.messu.utils.ChatUtil
import com.subefu.messu.utils.ChatUtil.generateChatId


class ChatsAdapter(val context: Context?, val listChats: ArrayList<ChatModel>)
    :RecyclerView.Adapter<ChatsAdapter.ChatsViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatsViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.model_chat, parent, false)
        return ChatsViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatsViewHolder, position: Int) {
        holder.chatName.text = listChats[position].chatName

        holder.itemView.setOnClickListener {
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra("chat_id", listChats[position].id)
            context?.startActivity(intent)
        }
        holder.itemView.setOnLongClickListener {
            delete(position)
            true
        }
    }
    fun delete(position: Int){
        val alert = AlertDialog.Builder(context!!)
            .setTitle("Delete this chat?")
            .setPositiveButton("Yes", object : DialogInterface.OnClickListener{
                override fun onClick(dialog: DialogInterface?, which: Int) {
                   deleteChat(listChats[position].id_2)
                }
            }).setNegativeButton("No", object : DialogInterface.OnClickListener{
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    return
                }
            })
        alert.create().show()
    }
    fun deleteChat(fid: String){
        val uid = FirebaseAuth.getInstance().currentUser!!.uid.toString()
        val supposedChatId: String = generateChatId(fid, uid)

        FirebaseDatabase.getInstance().reference.child("Chats").child(supposedChatId).removeValue()

        FirebaseDatabase.getInstance().reference.child("Users").child(uid).child("chats").child(supposedChatId).removeValue()
        FirebaseDatabase.getInstance().reference.child("Users").child(fid).child("chats").child(supposedChatId).removeValue()
    }

    override fun getItemCount() = listChats.size

    class ChatsViewHolder(view: View): RecyclerView.ViewHolder(view){
        var chatName: TextView
        init {
            chatName = view.findViewById(R.id.model_chat_name)
        }
    }
}