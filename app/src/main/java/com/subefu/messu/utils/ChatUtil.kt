package com.subefu.messu.utils

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import java.util.Arrays


object ChatUtil {
    fun createChat(user: UserModel) {
        val uid: String = FirebaseAuth.getInstance().currentUser?.getUid() ?: "null"

        if(uid == "null") return

        val chatInfo = hashMapOf("user1" to uid, "user2" to user.id)

        val chatId: String = generateChatId(uid, user.id)
        FirebaseDatabase.getInstance().reference.child("Chats").child(chatId)
            .setValue(chatInfo)

        addChatIdToUser(uid, chatId)
        addChatIdToUser(user.id, chatId)
    }

    private fun generateChatId(userId1: String, userId2: String): String {
        val sumUser1User2 = userId1 + userId2
        val charArray = sumUser1User2.toCharArray()
        Arrays.sort(charArray)

        return String(charArray)
    }

    private fun addChatIdToUser(uid: String, chatId: String) {
        FirebaseDatabase.getInstance().reference.child("Users").child(uid)
            .child("chats").get()
            .addOnCompleteListener { task: Task<DataSnapshot> ->
                if (task.isSuccessful) {
                    val chats = task.result.value.toString()
                    val chatsUpd: String = addIdToStr(chats, chatId)

                    FirebaseDatabase.getInstance().reference.child("Users").child(uid)
                        .child("chats").setValue(chatsUpd)
                }
            }
    }

    private fun addIdToStr(str: String, chatId: String): String {
        var str = str
        str += if ((str.isEmpty())) chatId else (",$chatId")

        return str
    }
}