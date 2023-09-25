package com.example.shyf_.inite

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity

open class GetUserData(var activity: Activity) {
    fun getId(): Int {
        val shared = activity.getSharedPreferences(
            "user_data", AppCompatActivity.MODE_PRIVATE
        )
        return shared.getInt("id", 0)
    }

    fun getToken(): String {
        val shared = activity.getSharedPreferences(
            "user_data", AppCompatActivity.MODE_PRIVATE
        )
        return shared.getString("userToken", "").toString()
    }


}