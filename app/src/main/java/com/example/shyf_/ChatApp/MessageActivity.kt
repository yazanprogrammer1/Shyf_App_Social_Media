package com.example.shyf_.ChatApp

import android.content.Intent
import android.graphics.Color
import android.icu.util.Calendar
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.shyf_.Adapter.MessageAdapter
import com.example.shyf_.R
import com.example.shyf_.apis.ApiClient
import com.example.shyf_.apis.ApiService
import com.example.shyf_.databinding.ActivityMessageBinding
import com.example.shyf_.model.Chat
import com.example.shyf_.model.UserChats
import com.example.shyf_.notification.Cons
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DateFormat
import java.text.SimpleDateFormat

class MessageActivity : AppCompatActivity() {

    lateinit var buiding: ActivityMessageBinding
    var reference: DatabaseReference? = null
    var messageAdapter: MessageAdapter? = null
    var list: ArrayList<Chat>? = null
    var firebaseUser: FirebaseUser? = null
    var isValidable = false
    lateinit var user: UserChats
    var eventListener: ValueEventListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        buiding = ActivityMessageBinding.inflate(layoutInflater)
        setContentView(buiding.root)
        //.... code
        firebaseUser = FirebaseAuth.getInstance().currentUser
        buiding.recyclerView.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(
            applicationContext
        )
        linearLayoutManager.stackFromEnd = true
        buiding.recyclerView.layoutManager = linearLayoutManager
        user = intent.getSerializableExtra("user") as UserChats
        reference = FirebaseDatabase.getInstance().getReference("users").child(user.id!!)
        reference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userState: UserChats? = snapshot.getValue(UserChats::class.java)
                buiding.username.setText(userState!!.nameUser)
                if (userState.ImgUrl.equals("default")) {
                    buiding.profileImg.setImageResource(R.drawable.profile_sh)
                } else {
                    Glide.with(applicationContext).load(userState.ImgUrl)
                        .into(buiding.profileImg)
                }
                if (userState.status.equals("online")) {
                    buiding.imgOff.borderColor = Color.GREEN
                    buiding.txtState.text = "online"
                    isValidable = true
                } else {
                    buiding.imgOff.borderColor = Color.RED
                    buiding.txtState.text = "offline"
                    isValidable = false
                }
                readMesage(firebaseUser!!.uid, user.id!!, userState.ImgUrl!!)
                seenMessage(user.id!!)
            }

            override fun onCancelled(error: DatabaseError) {}
        })

        buiding.imageSend.setOnClickListener(View.OnClickListener {
            val msg: String = buiding.etMessage.getText().toString().trim()
            val shared = getSharedPreferences("user_data", AppCompatActivity.MODE_PRIVATE)
            var userToken = shared.getString("userToken", "").toString()

            sendMessage(firebaseUser!!.uid, user!!.id!!, msg)
            if (!isValidable) {
                try {
                    val token = JSONArray()
                    token.put(user!!.userToken)
                    val data = JSONObject()
                    data.put("userid", firebaseUser!!.uid)
                    data.put("token", userToken)
                    data.put("message", msg)
                    val body = JSONObject()
                    body.put(Cons.remoteData, data)
                    body.put(Cons.registeration_id, token)
                    Toast.makeText(this@MessageActivity, "welcome", Toast.LENGTH_SHORT).show()
                    sendNotification(body.toString())
                } catch (E: Exception) {
                    E.printStackTrace()
                }
            }
            buiding.etMessage.setText("")
        })

        buiding.imageBack.setOnClickListener(View.OnClickListener {
            startActivity(Intent(applicationContext, ChatMainActivity::class.java))
            finish()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        })

    }

    private fun seenMessage(userid: String) {
        reference = FirebaseDatabase.getInstance().getReference("chats")
        eventListener = reference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (dataSnapshots in snapshot.children) {
                    val chat = dataSnapshots.getValue(Chat::class.java)
                    if (chat!!.reciever.equals(firebaseUser!!.uid) && chat.sender
                            .equals(userid)
                    ) {
                        val hashMap = HashMap<String, Any>()
                        hashMap["seen"] = true
                        reference!!.child(dataSnapshots.key!!).updateChildren(hashMap)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun sendMessage(sender: String, reciver: String, msg: String) {
        val df: DateFormat = SimpleDateFormat("EEE, d MMM yyyy, HH:mm")
        val date = df.format(Calendar.getInstance().time)
        val reference1 = FirebaseDatabase.getInstance().reference
        val hash = HashMap<String, Any>()
        hash["sender"] = sender
        hash["reciever"] = reciver
        hash["message"] = msg
        hash["seen"] = false
        hash["date"] = date
        hash["timestamp"] = ServerValue.TIMESTAMP  // هنا نستخدم قيمة الوقت من الخادم
        // الحصول على وقت الساعة الحالي باستخدام Calendar
        val currentTime = Calendar.getInstance()
        val hour = currentTime.get(Calendar.HOUR_OF_DAY)
        val minute = currentTime.get(Calendar.MINUTE)
        // تكوين سلسلة الوقت
        val timeString = String.format("%02d:%02d", hour, minute)
        hash["lastTimeMessage"] = timeString
        reference1.child("chats").push().setValue(hash).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this@MessageActivity, "sueecffuul", Toast.LENGTH_SHORT).show()
                updateSortUserSender()
                updateSortUserReciver()
            }
        }
    }

    private fun updateSortUserSender() {
        val reference = FirebaseDatabase.getInstance().getReference("users").child(user.id!!)
        val hashMap = HashMap<String, Any>()
        hashMap["timestamp"] = ServerValue.TIMESTAMP
        reference.updateChildren(hashMap)
    }

    private fun updateSortUserReciver() {
        val reference =
            FirebaseDatabase.getInstance().getReference("users").child(firebaseUser!!.uid)
        val hashMap = HashMap<String, Any>()
        hashMap["timestamp"] = ServerValue.TIMESTAMP
        reference.updateChildren(hashMap)
    }

    private fun readMesage(myId: String, userId: String, imageUrl: String) {
        list = ArrayList()
        val refer = FirebaseDatabase.getInstance().getReference("chats")
        refer.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                list!!.clear()
                for (dataSnapshot in snapshot.children) {
                    val chat = dataSnapshot.getValue(Chat::class.java)
                    if (chat!!.reciever.equals(myId) && chat.sender.equals(userId)
                        || chat.reciever.equals(userId) && chat.sender.equals(myId)
                    ) {
                        list!!.add(chat!!)
                        messageAdapter = MessageAdapter(this@MessageActivity, list!!, imageUrl)
                        buiding.recyclerView.adapter = messageAdapter
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun state(state: String) {
        val reference = FirebaseDatabase.getInstance().getReference("users").child(
            firebaseUser!!.uid
        )
        val hashMap = HashMap<String, Any>()
        hashMap["status"] = state
        reference.updateChildren(hashMap)
    }


    private fun sendNotification(message: String) {
        ApiClient.getClient().create(ApiService::class.java).sendMessage(
            Cons.getHeader()!!, message
        ).enqueue(object : Callback<String?> {
            override fun onResponse(call: Call<String?>, response: Response<String?>) {
                if (response.isSuccessful) {
                    try {
                        if (response.body() != null) {
                            val responseObj = JSONObject(response.body())
                            val jsonArray = responseObj.getJSONArray("results")
                            if (responseObj.getInt("failure") == 1) {
                                val error = jsonArray[0] as JSONObject
                                Toast.makeText(
                                    this@MessageActivity,
                                    error.getString("error") + "",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } catch (e: java.lang.Exception) {
                        Toast.makeText(this@MessageActivity, e.message, Toast.LENGTH_SHORT).show()
                    }
                    Toast.makeText(
                        this@MessageActivity,
                        "succeefully sent notification",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(this@MessageActivity, "error", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<String?>, t: Throwable) {
                Toast.makeText(this@MessageActivity, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }


    override fun onResume() {
        super.onResume()
        state("online")
    }

    override fun onPause() {
        super.onPause()
        reference!!.removeEventListener(eventListener!!)
        state("offline")
    }
}