package com.example.vchat.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.vchat.R
import com.example.vchat.repository.VChatRepository
import com.example.vchat.util.PrefHelper
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class PushNotificationService @Inject constructor(private val repository: VChatRepository) :FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        println("NEW TOKEN RECEIVED $token")
        GlobalScope.launch {
            repository.updateDeviceToken("userId")
        }
//        update to the server
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        message.notification?.let {
            Log.d("PushNotificationService", "Notification Body: ${it.body}")
            showNotification(it.title.toString(), it.body.toString())
        }

    }

    private fun showNotification(title: String, body: String) {
        val channelId = "default_channel_id"
        val channelName = "Default Channel"

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setContentTitle(title)
            .setSmallIcon(R.drawable.msg)
            .setContentText(body)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        val notificationId = System.currentTimeMillis().toInt()
        notificationManager.notify(notificationId, notificationBuilder.build())
    }

}