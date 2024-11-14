package com.subefu.messu.adapter

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.subefu.messu.R
import com.subefu.messu.adapter.NewChatsAdapter.UserChatViewHolder
import com.subefu.messu.screen.chat.ChatActivity
import com.subefu.messu.utils.ChatModel
import com.subefu.messu.utils.ChatUtil.generateChatId


class ChatsAdapter(val context: Context?, val listChats: ArrayList<ChatModel>)
    :RecyclerView.Adapter<ChatsAdapter.ChatsViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatsViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.model_chat, parent, false)
        return ChatsViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatsViewHolder, position: Int) {
        holder.chatName.text = listChats[position].chatName
        setLastMessageAndTime(holder, position)
        setAva(holder, position)
        setNoLookCount(holder, position)
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
    fun setAva(holder: ChatsViewHolder, position: Int){
        FirebaseDatabase.getInstance().getReference().child("Users").child(listChats[position].id_2)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    when(snapshot.child("profileImage").value.toString()){
                        "1" -> holder.ava.setBackgroundResource(R.drawable.png_animal_1)
                        "2" -> holder.ava.setBackgroundResource(R.drawable.png_animal_2)
                        "3" -> holder.ava.setBackgroundResource(R.drawable.png_animal_3)
                        "4" -> holder.ava.setBackgroundResource(R.drawable.png_animal_4)
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
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
    fun setLastMessageAndTime(holder: ChatsViewHolder, position: Int){
        FirebaseDatabase.getInstance().getReference().child("Chats").child(listChats[position].id).child("message")
            .orderByKey().limitToLast(1).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (snapshot in dataSnapshot.children) {
                        val message = snapshot.child("text").value.toString()
                        val time = snapshot.child("time").value.toString()
                        holder.chatMessage.setText(message)
                        holder.chatTime.setText(time)
                        Log.d("LastMessage", message)
                    }
                }
                override fun onCancelled(databaseError: DatabaseError) {
                    Log.w("FirebaseError", "loadPost:onCancelled", databaseError.toException())
                }
            })
    }

    fun setNoLookCount(holder: ChatsViewHolder, position: Int) {
        FirebaseDatabase.getInstance().getReference().child("Chats").child(listChats[position].id).child("message")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    var count = 0
                    for (snapshot in dataSnapshot.children) {
                        if(snapshot.child("isLook").value.toString() == "no" &&
                            snapshot.child("uid").value.toString() != FirebaseAuth.getInstance().currentUser!!.uid)
                            count++
                    }
                    holder.chatCount.setText(count.toString())
                }
                override fun onCancelled(databaseError: DatabaseError) {
                    Log.w("FirebaseError", "loadPost:onCancelled", databaseError.toException())
                }
            })
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
        var chatMessage: TextView
        var chatTime: TextView
        var chatCount: TextView
        var ava: ImageView
        init {
            chatName = view.findViewById(R.id.model_chat_name)
            chatMessage = view.findViewById(R.id.model_chat_message)
            chatTime = view.findViewById(R.id.model_chat_message_time)
            chatCount = view.findViewById(R.id.model_chat_message_count)
            ava = view.findViewById(R.id.model_chat_ava)
        }
    }
}