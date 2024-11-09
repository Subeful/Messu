package com.subefu.messu.screen.bot_menu.chats

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.subefu.messu.adapter.ChatsAdapter
import com.subefu.messu.databinding.FragmentChatsBinding
import com.subefu.messu.utils.ChatModel
import java.util.ArrayList


class ChatsFragment : Fragment() {

    lateinit var binding: FragmentChatsBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentChatsBinding.inflate(inflater, container, false)

        setChats()

        return binding.root
    }

    fun setChats(){
        val listUsers = ArrayList<ChatModel>()

        val uid = FirebaseAuth.getInstance().currentUser!!.uid

        FirebaseDatabase.getInstance().getReference().addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val chatsStr = snapshot.child("Users").child(uid).child("chats").value.toString()
                if(chatsStr.isEmpty()) return

                val chatsIds: List<String> = chatsStr.split(",");

                for (chatId in chatsIds){
                    val chatSnapshot = snapshot.child("Chats").child(chatId);
                    val userId1 = chatSnapshot.child("user1").value.toString()
                    val userId2 = chatSnapshot.child("user2").value.toString()

                    val chatUserId = if(uid.equals(userId1)) userId2 else userId1;
                    val chatName = snapshot.child("Users").child(chatUserId).child("username").value.toString()

                    val chat = ChatModel(chatId, chatName, userId1, userId2);
                    listUsers.add(chat);
                }

                val rv = binding.rvChats
                rv.setAdapter(ChatsAdapter(context, listUsers));
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(getContext(), "Failed to get user chats", Toast.LENGTH_SHORT).show();
            }
        });
    }




}