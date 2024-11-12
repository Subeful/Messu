package com.subefu.messu.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.subefu.messu.R
import com.subefu.messu.utils.ChatUtil.createChat
import com.subefu.messu.utils.UserModel


class NewChatsAdapter(val context: Context?, val list: ArrayList<UserModel>)
    : RecyclerView.Adapter<NewChatsAdapter.UserChatViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserChatViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.model_user, parent, false)
        return UserChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserChatViewHolder, position: Int) {
        holder.name.text = list[position].username

        val user = list.get(position);

        holder.itemView.setOnClickListener { view ->
            createChat(context!!, user.id)
        }
    }

    override fun getItemCount() = list.size

    class UserChatViewHolder(view: View): RecyclerView.ViewHolder(view){
        lateinit var name: TextView
        init {
            name = view.findViewById(R.id.model_user_name)
        }
    }
}