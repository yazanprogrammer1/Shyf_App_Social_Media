package com.example.shyf_.inite

import android.content.ContentValues
import android.util.Log
import com.example.shyf_.model.UserChat
import com.example.shyf_.notification.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class GetIfUserOnline {

    fun fetchUserDetails(): Boolean {
        var isOnline: Boolean = false
        var user: UserChat?
        val database = FirebaseFirestore.getInstance()
        database.collection(Constants.KEY_COLLECTION_USER)
            .document(FirebaseAuth.getInstance().uid!!).get().addOnSuccessListener { document ->
                user = document.toObject(UserChat::class.java)
                if (user!!.online_status!!) {
                    isOnline = true
                } else if (!user!!.online_status!!) {
                    isOnline = false
                }
            }.addOnFailureListener { exception ->
                isOnline = false
                Log.e(ContentValues.TAG, "Error Getting the User Details", exception)
            }
        return isOnline
    }
}