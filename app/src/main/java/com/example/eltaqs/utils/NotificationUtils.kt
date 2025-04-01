package com.example.eltaqs.utils

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.eltaqs.MainActivity
import com.example.eltaqs.R

fun createNotification(context : Context) : NotificationCompat.Builder {
    val intent = Intent(context, MainActivity::class.java)

    val channelId = "my_channel_id"

    val pendingIntent = PendingIntent.getActivity(
        context,
        0,
        intent,
        PendingIntent.FLAG_IMMUTABLE
    )



    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
        val name = "My Channel"
        val descriptionText = "My Channel Description"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = android.app.NotificationChannel(channelId, name, importance).apply {
            description = descriptionText
        }
        val notificationManager = context.getSystemService(NotificationManager::class.java) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
    
    val builder = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentTitle("My Foreground Service")
        .setContentText("Download in progress...")
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setAutoCancel(true)
        .setContentIntent(pendingIntent)
    return builder
}