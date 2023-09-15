package com.example.shyf_.Adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.shyf_.ChatApp.MessageActivity
import com.example.shyf_.R
import com.example.shyf_.model.Chat
import com.example.shyf_.model.UserChats
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import de.hdodenhof.circleimageview.CircleImageView


class userAdapter(private val mContext: Activity, var userList: ArrayList<UserChats>, ischat: Boolean) :
    RecyclerView.Adapter<userAdapter.ViewHolder>() {
    private var mUsers: ArrayList<UserChats>
    private val ischat: Boolean
    var lastMessage: String? = null
    var lastTime: String? = null

    init {
        mUsers = userList
        this.ischat = ischat
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(mContext).inflate(R.layout.user_search_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user: UserChats = mUsers[position]
        val firebaseUser = FirebaseAuth.getInstance().currentUser

        holder.contactName.setText(user.nameUser)
        if (user.ImgUrl.equals("default")) {
            holder.profileImg.setImageResource(R.mipmap.ic_launcher)
        } else {
            Glide.with(mContext).load(user.ImgUrl).into(holder.profileImg)
        }

        holder.itemView.setOnClickListener {
            val i = Intent(mContext, MessageActivity::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            i.putExtra("user", user)
            mContext.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            mContext.startActivity(i)
        }
        if (ischat) {
            if (user.status.equals("online")) {
                holder.img_on.visibility = View.VISIBLE
                holder.img_off.visibility = View.GONE
            } else if (user.status.equals("offline")) {
                holder.img_on.visibility = View.GONE
                holder.img_off.visibility = View.VISIBLE
            }
            lastMessage(
                user.id!!, holder.contactStatus, holder.lastTimeMessage
            )

        } else {
            holder.img_on.visibility = View.GONE
            holder.img_off.visibility = View.GONE
            holder.contactStatus.setText(user.status)
            holder.unSeenMessageImage.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return mUsers.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var contactName: TextView
        var lastTimeMessage: TextView
        var contactStatus: TextView
        var profileImg: CircleImageView
        var img_on: CircleImageView
        var img_off: CircleImageView
        var unSeenMessageImage: TextView

        init {
            contactName = itemView.findViewById<TextView>(R.id.ContactName)
            lastTimeMessage = itemView.findViewById<TextView>(R.id.timeMessage)
            contactStatus = itemView.findViewById<TextView>(R.id.ContactStatus)
            profileImg = itemView.findViewById<CircleImageView>(R.id.imgProfile)
            img_on = itemView.findViewById<CircleImageView>(R.id.img_on)
            img_off = itemView.findViewById<CircleImageView>(R.id.img_off)
            unSeenMessageImage = itemView.findViewById<TextView>(R.id.unSeenMessage)
        }
    }

    private fun lastMessage(
        userid: String, message: TextView, lastTimeMessage: TextView
    ) {
        lastMessage = ""
        lastTime = ""
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val reference = FirebaseDatabase.getInstance().getReference("chats")
        reference.addValueEventListener(object : ValueEventListener {
            @SuppressLint("SetTextI18n")
            override fun onDataChange(snapshot: DataSnapshot) {
                for (dataSnapshot in snapshot.children) {
                    val chat: Chat? = dataSnapshot.getValue(Chat::class.java)
                    if (firebaseUser != null) {
                        if (chat!!.sender.equals(firebaseUser.uid) && chat.reciever.equals(userid) || chat.sender.equals(
                                userid
                            ) && chat.reciever.equals(firebaseUser.uid)
                        ) {
                            lastMessage = chat.message
                            lastTime = chat.lastTimeMessage
                        }
                    }
                }
                when (lastMessage) {
                    "" -> message.text = "."
                    else -> message.text = lastMessage
                }
                lastTimeMessage.text = lastTime
                lastMessage = ""
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun searchList(userArrayList: ArrayList<UserChats>) {
        mUsers = userArrayList
        notifyDataSetChanged()
    }
}