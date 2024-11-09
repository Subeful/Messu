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
import com.subefu.messu.adapter.UserChatsAdapter
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

        FirebaseDatabase.getInstance().getReference().child("Users")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (user in snapshot.children) {
                        val name = user.child("username").getValue().toString()
                        val id = user.child("id").getValue().toString()
                        if (!id.equals(FirebaseAuth.getInstance().currentUser!!.uid)){
                            listUsers.add(UserModel(id, name , ""))
                        }
                    }
                    val rv = binding.rvNewChat
                    rv.addItemDecoration(
                        DividerItemDecoration(context,
                            DividerItemDecoration.VERTICAL)
                    )
                    rv.adapter = UserChatsAdapter(context, listUsers)
                }
                override fun onCancelled(error: DatabaseError) {
                    Log.d( "FirebaseError", "Error fetching data: ${error.message}")
                }
            })


    }

}