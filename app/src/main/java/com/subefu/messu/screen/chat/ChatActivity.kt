package com.subefu.messu.screen.chat

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.subefu.messu.adapter.MessageAdapter
import com.subefu.messu.databinding.ActivityChatBinding
import com.subefu.messu.utils.MessageModel
import java.text.SimpleDateFormat
import java.util.Date


class ChatActivity : AppCompatActivity() {

    lateinit var binding: ActivityChatBinding
    lateinit var chatId: String
    lateinit var mine_id: String
    lateinit var firebaseDatabase: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val decorView = window.decorView
        decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)

        init()
        loadMessage()
        lookMessage()

        binding.ibSend.setOnClickListener {
            val et_mes = binding.etChat
            if(et_mes.text.toString().trim().isEmpty()) return@setOnClickListener
            sendMessage(et_mes.text.toString())
            et_mes.setText("")
        }
    }

    fun init(){
        chatId = intent.getStringExtra("chat_id").toString()
        mine_id = FirebaseAuth.getInstance().currentUser!!.uid
        firebaseDatabase = FirebaseDatabase.getInstance().getReference()
    }

    @SuppressLint("SimpleDateFormat")
    fun sendMessage(text: String){
        if(text.isEmpty()) return

        val time = SimpleDateFormat("HH:mm").format(Date())
        val message = mapOf("uid" to mine_id, "text" to text, "time" to time, "isLook" to "no")

        firebaseDatabase.child("Chats").child(chatId).child("message")
            .push().setValue(message)

    }

    fun lookMessage(){
        firebaseDatabase.child("Chats").child(chatId).child("message")
            .addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot.childrenCount.toInt() == 0) return
                for (snapshot in dataSnapshot.children) {
                    val isLook = snapshot.child("isLook").value.toString()
                    val userId = snapshot.child("uid").value.toString()
                    if(userId == mine_id) continue

                    if ("no" == isLook) {
                        snapshot.ref.child("isLook").setValue("yes")
                    }
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    fun loadMessage(){
        if(chatId.isEmpty()) return

        firebaseDatabase.child("Chats").child(chatId).child("message")
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(!snapshot.exists()) return
                    if(snapshot.childrenCount.equals(0)) return

                    val listMessage = ArrayList<MessageModel>()

                    for(message in snapshot.children){
                        val mid = message.key.toString()
                        val uid = message.child("uid").value.toString()
                        val text = message.child("text").value.toString()
                        val timeSend = message.child("time").value.toString()
                        val isLook = message.child("isLook").value.toString()

                        if(!text.equals("null"))
                            listMessage.add(MessageModel(mid, uid, text, timeSend, isLook))
                    }


                    val rv = binding.rvChatMine
                    rv.adapter = MessageAdapter(applicationContext, listMessage)

                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

}