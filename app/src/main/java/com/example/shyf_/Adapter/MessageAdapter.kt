package com.example.shyf_.Adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.shyf_.R
import com.example.shyf_.model.Chat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import de.hdodenhof.circleimageview.CircleImageView

class MessageAdapter(private val activity: Activity, chatList: List<Chat>, imgUrl: String) :
    RecyclerView.Adapter<MessageAdapter.ViewHolder>() {
    private val chatList: List<Chat>
    private val imgUrl: String
    var firebaseUser: FirebaseUser? = null

    init {
        this.chatList = chatList
        this.imgUrl = imgUrl
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        if (viewType == MSG_TYPE_RIGHT) {
            val view: View =
                LayoutInflater.from(activity).inflate(R.layout.chat_right, parent, false)
            return ViewHolder(view)
        } else {
            val view: View =
                LayoutInflater.from(activity).inflate(R.layout.chat_left, parent, false)
            return ViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chat: Chat = chatList[position]
        holder.show_message.setText(chat.message)
        holder.txtdate.setText(chat.date)
        if (imgUrl == "default") {
            holder.profileImg.setImageResource(R.drawable.profile_sh)
        } else {
            Glide.with(activity).load(imgUrl).into(holder.profileImg)
        }
        if (position == chatList.size - 1) {
            if (chatList[position].seen) {
                holder.txt_seen.text = "Seen"
            } else {
                holder.txt_seen.text = "delivery"
            }
        } else {
            holder.txt_seen.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return chatList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var show_message: TextView
        var txt_seen: TextView
        var txtdate: TextView
        var profileImg: CircleImageView

        init {
            show_message = itemView.findViewById<TextView>(R.id.show_message)
            profileImg = itemView.findViewById<CircleImageView>(R.id.imgProf)
            txt_seen = itemView.findViewById<TextView>(R.id.txt_seen)
            txtdate = itemView.findViewById<TextView>(R.id.txtdate)
        }
    }

    override fun getItemViewType(position: Int): Int {
        firebaseUser = FirebaseAuth.getInstance().currentUser
        return if (chatList[position].sender.equals(firebaseUser!!.uid)) {
            MSG_TYPE_RIGHT
        } else {
            MSG_TYPE_LEFT
        }
    }

    companion object {
        const val MSG_TYPE_LEFT = 0
        const val MSG_TYPE_RIGHT = 1
    }
}