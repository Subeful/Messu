package com.subefu.messu.screen.chat

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
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
    }

    @SuppressLint("SimpleDateFormat")
    fun sendMessage(text: String){
        if(text.isEmpty()) return

        val time = SimpleDateFormat("HH:mm").format(Date())
        val message = mapOf("uid" to mine_id, "text" to text, "time" to time)

        FirebaseDatabase.getInstance().getReference().child("Chats").child(chatId).child("message")
            .push().setValue(message)

    }

    fun loadMessage(){
        if(chatId.isEmpty()) return

        FirebaseDatabase.getInstance().reference.child("Chats").child(chatId).child("message")
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

                        if(!text.equals("null"))
                            listMessage.add(MessageModel(mid, uid, text, timeSend))
                    }


                    val rv = binding.rvChatMine
                    rv.adapter = MessageAdapter(applicationContext, listMessage)

                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

}