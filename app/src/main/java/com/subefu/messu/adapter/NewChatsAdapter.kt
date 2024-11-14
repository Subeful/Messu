package com.subefu.messu.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.subefu.messu.R
import com.subefu.messu.utils.ChatUtil.createChat
import com.subefu.messu.utils.UserModel


class NewChatsAdapter(val context: Context?, val list: ArrayList<UserModel>)
    : RecyclerView.Adapter<NewChatsAdapter.UserChatViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserChatViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.model_user, parent, false)
        return UserChatViewHolder(view)
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: UserChatViewHolder, position: Int) {
        val user = list[position]

        holder.name.text = user.username
        setAva(holder, position)

        if(user.statusOnline == "Online"){
            holder.status.setTextColor(context!!.resources.getColor(R.color.green))
            holder.status.text = user.statusOnline
        } else
            holder.status.text = "was online at " + user.statusOnline


        holder.itemView.setOnClickListener { view ->
            createChat(context!!, user.id)
        }
    }

    fun setAva(holder: UserChatViewHolder, position: Int){
        FirebaseDatabase.getInstance().getReference().child("Users").child(list[position].id)
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

    override fun getItemCount() = list.size

    class UserChatViewHolder(view: View): RecyclerView.ViewHolder(view){
        var name: TextView
        var status: TextView
        var ava: ImageView
        init {
            name = view.findViewById(R.id.model_user_name)
            status = view.findViewById(R.id.model_user_status)
            ava = view.findViewById(R.id.model_user_ava)
        }
    }
}