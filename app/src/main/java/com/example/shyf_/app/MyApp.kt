package com.example.forground_service.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

class MyApp : Application() {

    companion object {
        val CHANNEL_ID = "Foreground Service Example"
    }

    override fun onCreate() {
        super.onCreate()
        createNotifi()
    }

    private fun createNotifi() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID, "Example Service Channel ",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager!!.createNotificationChannel(serviceChannel)
        }
    }


}