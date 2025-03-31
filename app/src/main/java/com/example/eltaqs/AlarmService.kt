package com.example.eltaqs

import android.app.*
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat

class AlarmService : Service() {

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action
        if (ACTION_CLOSE_SERVICE == action) {
            stopForeground(true)
            stopSelf()
            return START_NOT_STICKY
        }

        val weatherDescription = intent?.getStringExtra("weatherDescription") ?: "No description available"
        val alarmId = intent?.getIntExtra("alarmId", -1) ?: -1

        startForeground(alarmId, createNotification(weatherDescription, alarmId))

        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun createNotification(description: String, alarmId: Int): Notification {
        val openIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val openPendingIntent = PendingIntent.getActivity(
            this, alarmId + 10, openIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val closeIntent = Intent(this, AlarmService::class.java).apply {
            action = ACTION_CLOSE_SERVICE
        }
        val closePendingIntent = PendingIntent.getService(
            this, alarmId, closeIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Weather Alert #$alarmId")
            .setContentText(description)
            .setSmallIcon(R.drawable.hail)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setAutoCancel(true)
            .addAction(0, "Open", openPendingIntent)
            .addAction(0, "Close", closePendingIntent)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Weather Alerts",
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(serviceChannel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopForeground(true)
        stopSelf()
    }

    companion object {
        private const val CHANNEL_ID = "WeatherAlertChannel"
        private const val ACTION_CLOSE_SERVICE = "com.example.eltaqs.ACTION_CLOSE_SERVICE"
    }
}
