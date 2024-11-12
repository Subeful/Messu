package com.subefu.messu.utils

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.subefu.messu.R
import java.util.Arrays


object ChatUtil {

    fun createChat(context: Context?, userId: String){
        val uid: String = FirebaseAuth.getInstance().currentUser?.getUid() ?: "null"
        if(uid == "null") return

        val chatInfo = mapOf("user1" to uid, "user2" to userId)
        val chatId: String = generateChatId(uid, userId)

        FirebaseDatabase.getInstance().getReference().child("Chats").child(chatId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Toast.makeText(context, "Chat already exist", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Chat created", Toast.LENGTH_SHORT).show()

                        FirebaseDatabase.getInstance().reference.child("Chats").child(chatId)
                            .setValue(chatInfo)

                        addChatIdToUser(uid, chatId)
                        addChatIdToUser(userId, chatId)
                    }
                }
                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(context, "error database", Toast.LENGTH_SHORT).show()
                    Log.e("Firebase", "Ошибка при чтении данных: " + databaseError.message)
                }
            })
    }


    fun generateChatId(userId1: String, userId2: String): String {
        val charArray = (userId1 + userId2).toCharArray()
        Arrays.sort(charArray)

        return String(charArray)
    }

    private fun addChatIdToUser(uid: String, chatId: String) {
        FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("chats")
            .child(chatId).setValue(mapOf("chat_id" to chatId))

    }

}