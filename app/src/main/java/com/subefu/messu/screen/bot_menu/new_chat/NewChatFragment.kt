package com.subefu.messu.screen.bot_menu.new_chat

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.subefu.messu.adapter.NewChatsAdapter
import com.subefu.messu.databinding.FragmentNewChatBinding
import com.subefu.messu.utils.UserModel
import java.util.ArrayList

class NewChatFragment : Fragment() {

    lateinit var binding: FragmentNewChatBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentNewChatBinding.inflate(inflater, container, false)

        setChats()

        return binding.root
    }

    fun setChats(){

        val listUsers = ArrayList<UserModel>()

        FirebaseDatabase.getInstance().getReference().child("Users").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.childrenCount.toInt() == 0) return
                    val uid = FirebaseAuth.getInstance().currentUser!!.uid

                    for (user in snapshot.children) {
                        val id = user.child("id").value.toString()
                        val name = user.child("username").value.toString()
                        val profileImage = user.child("profileImage").value.toString()
                        val status = user.child("status").value.toString()
                        if(id == null || uid == null) return
                        if (!id.equals(uid))
                            listUsers.add(UserModel(id, name , profileImage, status))

                    }
                    val rv = binding.rvNewChat
                    rv.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
                    rv.adapter = NewChatsAdapter(context, listUsers)
                }
                override fun onCancelled(error: DatabaseError) {
                    Log.d( "FirebaseError", "Error fetching data: ${error.message}")
                }
            })


    }

}