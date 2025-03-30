package com.example.eltaqs.alert.manager

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.example.eltaqs.Utils.parseTimeToMillis
import com.example.eltaqs.alert.receiver.AlarmBroadcastReceiver
import com.example.eltaqs.data.model.Alarm

class AlarmScheduler(val context: Context) : IAlarmScheduler {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    @SuppressLint("ScheduleExactAlarm")
    override fun scheduleAlarm(alarm: Alarm) {
        val alarmIntent = Intent(context, AlarmBroadcastReceiver::class.java).apply {
            putExtra("ALARM_ID", alarm.id)
            putExtra("ALARM_ACTION", "START")
        }

        val alarmPendingIntent = PendingIntent.getBroadcast(
            context,
            alarm.id,
            alarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val cancelIntent = Intent(context, AlarmBroadcastReceiver::class.java).apply {
            putExtra("ALARM_ID", alarm.id)
            putExtra("ALARM_ACTION", "STOP")
        }

        val cancelPendingIntent = PendingIntent.getBroadcast(
            context,
            -alarm.id,
            cancelIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmTime = parseTimeToMillis(alarm.startTime)
        val endTime = parseTimeToMillis(alarm.endTime)
        val cancelTime = alarmTime + (endTime - alarmTime)

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            alarmTime,
            alarmPendingIntent
        )
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            cancelTime,
            cancelPendingIntent
        )
    }

    override fun cancelAlarm(alarm: Alarm) {
        val alarmIntent = Intent(context, AlarmBroadcastReceiver::class.java)
        val cancelIntent = Intent(context, AlarmBroadcastReceiver::class.java)

        val alarmPendingIntent = PendingIntent.getBroadcast(
            context,
            alarm.id,
            alarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val cancelPendingIntent = PendingIntent.getBroadcast(
            context,
            -alarm.id,
            cancelIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.cancel(alarmPendingIntent)
        alarmManager.cancel(cancelPendingIntent)

    }

}