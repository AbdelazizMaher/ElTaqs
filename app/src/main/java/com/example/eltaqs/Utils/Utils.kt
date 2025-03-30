package com.example.eltaqs.Utils

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.compose.animation.core.Easing
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.ui.unit.LayoutDirection
import androidx.core.app.NotificationCompat
import com.example.eltaqs.MainActivity
import com.example.eltaqs.R
import com.example.eltaqs.alert.receiver.AlarmBroadcastCancelReceiver
import com.example.eltaqs.alert.service.MediaPlayerFacade
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

fun Easing.transform(from: Float, to: Float, value: Float): Float {
    return transform(((value - from) * (1f / (to - from))).coerceIn(0f, 1f))
}

operator fun PaddingValues.times(value: Float): PaddingValues = PaddingValues(
    top = calculateTopPadding() * value,
    bottom = calculateBottomPadding() * value,
    start = calculateStartPadding(LayoutDirection.Ltr) * value,
    end = calculateEndPadding(LayoutDirection.Ltr) * value
)

fun restartActivity(context: Context) {
    val intent = (context as? Activity)?.intent
    intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(intent)
    (context as? Activity)?.finish()
}

fun createNotification(
    context: Context,
    alarmId: Int,
    weatherDescription: String,
) {
    val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

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

    val deleteIntent = Intent(context, AlarmBroadcastCancelReceiver::class.java).apply {
        putExtra("ALARM_ID", alarmId)
        action = "DELETE"
    }
    val deletePendingIntent = PendingIntent.getBroadcast(
        context,
        alarmId,
        deleteIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val cancelIntent = Intent(context, AlarmBroadcastCancelReceiver::class.java).apply {
        putExtra("ALARM_ID", alarmId)
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
        alarmId,
        openIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val notification = NotificationCompat.Builder(context, "ALARM_CHANNEL")
        .setContentTitle("Alarm: Favourite weather awaits!")
        .setContentText("Current weather: $weatherDescription")
        .setSmallIcon(R.drawable.hail)
        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM))
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        .setAutoCancel(true)
        .addAction(
            R.drawable.hail,
            "Cancel",
            cancelPendingIntent
        )
        .addAction(
            R.drawable.hail,
            "Open",
            openPendingIntent
        )
        .setDeleteIntent(deletePendingIntent)
        .build()

    notificationManager.notify(alarmId, notification)
}

fun String.isValidTimeFormat(): Boolean {
    return this.matches(Regex("^[0-9].*"))
}

fun parseTimeToMillis(timeString: String): Long {
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    val date = sdf.parse(timeString) ?: return 0L

    val calendar = Calendar.getInstance()
    calendar.time = date

    // Get the current date and set the extracted time
    val now = Calendar.getInstance()
    calendar.set(Calendar.YEAR, now.get(Calendar.YEAR))
    calendar.set(Calendar.MONTH, now.get(Calendar.MONTH))
    calendar.set(Calendar.DAY_OF_MONTH, now.get(Calendar.DAY_OF_MONTH))

    return calendar.timeInMillis
}