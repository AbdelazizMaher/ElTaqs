package com.example.eltaqs.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.eltaqs.MainActivity
import com.example.eltaqs.R
import com.example.eltaqs.alarm.receiver.AlarmBroadcastReceiver

fun createNotification(
    context: Context,
    alarmId: Int,
    weatherDescription: String,
) {
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            "ALARM_CHANNEL",
            "Alarm Notifications",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Channel for alarm notifications"
        }
        notificationManager.createNotificationChannel(channel)
    }

    val deleteIntent = Intent(context, AlarmBroadcastReceiver::class.java).apply {
        putExtra("ALARM_ID", alarmId)
        putExtra("ALARM_ACTION", "STOP")
        action = "DELETE"
    }
    val deletePendingIntent = PendingIntent.getBroadcast(
        context,
        alarmId,
        deleteIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val cancelIntent = Intent(context, AlarmBroadcastReceiver::class.java).apply {
        putExtra("ALARM_ID", alarmId)
        putExtra("ALARM_ACTION", "STOP")
    }
    val cancelPendingIntent = PendingIntent.getBroadcast(
        context,
        alarmId,
        cancelIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val openIntent = Intent(context, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
    val openPendingIntent = PendingIntent.getActivity(
        context,
        alarmId + 10, 
        openIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val snoozeIntent = Intent(context, AlarmBroadcastReceiver::class.java).apply {
        putExtra("ALARM_ID", alarmId)
        putExtra("ALARM_ACTION", "SNOOZE")
    }
    val snoozePendingIntent = PendingIntent.getBroadcast(
        context,
        alarmId + 20, 
        snoozeIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val notification = NotificationCompat.Builder(context, "ALARM_CHANNEL")
        .setContentTitle(context.getString(R.string.alarm_weather_awaits))
        .setContentText(context.getString(R.string.current_weather, weatherDescription))
        .setSmallIcon(R.drawable.snow)
        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM))
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        .setAutoCancel(true)
        .addAction(
            R.drawable.thunderstorm,
            context.getString(R.string.snooze_1_min),
            snoozePendingIntent
        )
        .addAction(
            R.drawable.snow,
            context.getString(R.string.cancel),
            cancelPendingIntent
        )
        .addAction(
            R.drawable.hail,
            context.getString(R.string.open),
            openPendingIntent
        )
        .setDeleteIntent(deletePendingIntent)
        .build()

    notificationManager.notify(alarmId, notification)
}