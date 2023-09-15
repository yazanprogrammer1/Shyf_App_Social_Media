package com.example.shyf_.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.shyf_.ChatApp.MessageActivity
import com.example.shyf_.R
import com.example.shyf_.model.UserChats
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.util.Random

class MessageServices : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val user = UserChats()
        user.id = (message.data["userid"])
        user.userToken = (message.data["token"])
        val notificationId = Random().nextInt()
        val channel_id = "chat_message"
        val i = Intent(this, MessageActivity::class.java)
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        i.putExtra("user", user)
        val pendingIntent = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_IMMUTABLE)
        val builder = NotificationCompat.Builder(this, channel_id)
        builder.setContentTitle("New Message")
        builder.setContentText(message.data["message"])
        builder.setSmallIcon(R.mipmap.ic_launcher)
        builder.setContentIntent(pendingIntent)
        builder.setAutoCancel(true)
        builder.priority = NotificationCompat.PRIORITY_DEFAULT
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val namec: CharSequence = "chat message"
            val descrption = "Notifiction channel is chat app"
            val improtant = NotificationManager.IMPORTANCE_HIGH
            val notificationChannel = NotificationChannel(channel_id, namec, improtant)
            notificationChannel.description = descrption
            val notificationManager = getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }
        val notificationManagerCompat = NotificationManagerCompat.from(this)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        notificationManagerCompat.notify(notificationId, builder.build())
    }
}