package com.subefu.messu.adapter

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.subefu.messu.R
import com.subefu.messu.utils.MessageModel

class MessageAdapter(val context: Context, val listMessage: List<MessageModel>)
    :RecyclerView.Adapter<MessageAdapter.MessageViewHolder>(){


    val config = context.getSharedPreferences("configChats", 0)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = LayoutInflater.from(context).inflate(viewType, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val viewType = getItemViewType(position)
        val message = listMessage[position]

        holder.text.text = message.text
        holder.text.textSize = config.getInt("textSize", 16).toString().toFloat()
        holder.text.setTextColor(Color.parseColor(config.getString("textColor", "#ffffff")))

        holder.timeSend.text = message.timeSend
        holder.timeSend.setTextColor(Color.parseColor(config.getString("textColor", "#ffffff")))

        when(viewType){
            R.layout.message_mine_model -> holder.messahe_box
                .setCardBackgroundColor(Color.parseColor(config.getString("textColor_m", "#ADEBFF")))
            R.layout.message_your_model -> holder.messahe_box
            .setCardBackgroundColor(Color.parseColor(config.getString("textColor_y", "#BCBDC0")))
        }

        holder.check.setBackgroundResource(
            if(listMessage[position].isLook == "no") R.drawable.vc_chek_1
            else R.drawable.vc_chek_2
        )
    }

    override fun getItemViewType(position: Int): Int {
        if(listMessage[position].uid == FirebaseAuth.getInstance().currentUser!!.uid)
            return R.layout.message_mine_model
        else
            return R.layout.message_your_model
    }

    override fun getItemCount() = listMessage.size

    class MessageViewHolder(view: View): RecyclerView.ViewHolder(view){
        var text: TextView
        var timeSend: TextView
        var check: ImageView
        var messahe_box: CardView
        init{
            text = view.findViewById(R.id.message_text)
            timeSend = view.findViewById(R.id.message_time)
            check = view.findViewById(R.id.message_check)
            messahe_box = view.findViewById(R.id.message_box)
        }
    }
}