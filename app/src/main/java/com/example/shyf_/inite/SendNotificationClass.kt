package com.example.shyf_.inite

import android.app.Activity
import android.widget.Toast
import com.example.shyf_.apis.ApiClient
import com.example.shyf_.apis.ApiService
import com.example.shyf_.model.UserChat
import com.example.shyf_.notification.Cons
import com.example.shyf_.notification.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SendNotificationClass(var activity: Activity) {

    private lateinit var senderDetails: UserChat
    private lateinit var receiverDetails: UserChat
    private lateinit var chatRoomId: String
    private lateinit var userId: String
    private lateinit var senderId: String
    private lateinit var receiverId: String

    fun sendMessage(sender_id: String,receiver_id: String, token: String, message: String,nameUserSender:String) {
        fetchSenderDetail(sender_id)
        fetchReceiverDetail(receiver_id)
        if(!receiverDetails.online_status!!) {
            try {
                val tokens: JSONArray = JSONArray()
                tokens.put(receiverDetails.userToken)
                val data: JSONObject = JSONObject()
                data.put(Constants.KEY_USER_ID,userId)
                data.put(Constants.KEY_CHAT_ROOM_ID,chatRoomId)
                data.put(Constants.KEY_NAME,senderDetails.name)
                data.put(Constants.KEY_FCM_TOKEN,senderDetails.userToken)
                data.put(Constants.KEY_MESSAGE,"تم بدأ متابعتك من قبل $nameUserSender")
                val body: JSONObject = JSONObject()
                body.put(Constants.REMOTE_MSG_DATA,data)
                body.put(Constants.REMOTE_MSG_REGISTRATION_IDS,tokens)
                sendNotification(body.toString())
            } catch (e: Exception) {
                Toast.makeText(activity, "error send message", Toast.LENGTH_SHORT).show()
            }
        }
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
                                    activity,
                                    error.getString("error") + "",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } catch (e: java.lang.Exception) {
                        Toast.makeText(activity, e.message, Toast.LENGTH_SHORT).show()
                    }
                    Toast.makeText(
                        activity,
                        "succeefully sent notification",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(activity, "error", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<String?>, t: Throwable) {
                Toast.makeText(activity, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun fetchSenderDetail(sender_id:String) {
        var database = FirebaseFirestore.getInstance()
        database.collection(Constants.KEY_COLLECTION_USER).document(sender_id).addSnapshotListener { value, error ->
            val sender = value?.toObject(UserChat::class.java)
            senderDetails = sender!!
        }
    }

    private fun fetchReceiverDetail(receiver_id:String) {
        var database = FirebaseFirestore.getInstance()
        database.collection(Constants.KEY_COLLECTION_USER).document(receiver_id).addSnapshotListener { value, error ->
            val receiver = value?.toObject(UserChat::class.java)
            receiverDetails = receiver!!
        }
    }

}