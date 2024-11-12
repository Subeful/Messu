package com.subefu.messu.screen.bot_menu.chats

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.LinearLayout.VISIBLE
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.subefu.messu.adapter.ChatsAdapter
import com.subefu.messu.databinding.FragmentChatsBinding
import com.subefu.messu.screen.bot_menu.new_chat.NewChatFragment
import com.subefu.messu.utils.ChatModel
import com.subefu.messu.utils.SetFragment


class ChatsFragment : Fragment() {

    lateinit var binding: FragmentChatsBinding
    lateinit var someEventListener: SetFragment

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentChatsBinding.inflate(inflater, container, false)

        setChats()
        binding.tvClickToCreateChat2.setOnClickListener {
            if(binding.tvClickToCreateChat2.visibility == VISIBLE)
                someEventListener.setOtherFragment(NewChatFragment())
        }

        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        someEventListener = context as SetFragment
    }

    fun setChats(){
        val listUsers = ArrayList<ChatModel>()

        val uid = FirebaseAuth.getInstance().currentUser!!.uid

        FirebaseDatabase.getInstance().getReference().addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val chatsStr = snapshot.child("Users").child(uid).child("chats")
                if(chatsStr.childrenCount.toInt() == 0) {
                    binding.ltNoChatBanner.visibility = LinearLayout.VISIBLE
                    binding.rvChats.visibility = RecyclerView.GONE
                    return
                }

                for (chat in chatsStr.children){
                    val chatID = chat.child("chat_id").value.toString()
                    val userId1 = snapshot.child("Chats").child(chatID).child("user1").value.toString()
                    val userId2 = snapshot.child("Chats").child(chatID).child("user2").value.toString()

                    val chatUserId = if(uid.equals(userId1)) userId2 else userId1;
                    val chatName = snapshot.child("Users").child(chatUserId).child("username").value.toString()

                    val chat = ChatModel(chatID, chatName, userId1, userId2);
                    listUsers.add(chat);
                }

                val rv = binding.rvChats
                rv.setAdapter(ChatsAdapter(context, listUsers));
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(getContext(), "Failed to get user chats", Toast.LENGTH_SHORT).show();
            }
        })
    }
}