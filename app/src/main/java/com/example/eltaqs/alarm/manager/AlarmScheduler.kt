package com.example.eltaqs.alarm.manager

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.eltaqs.alarm.worker.AlertsWorker
import com.example.eltaqs.alarm.receiver.AlarmBroadcastReceiver
import com.example.eltaqs.data.model.Alarm
import com.example.eltaqs.utils.parseDateTimeToMillis
import java.util.concurrent.TimeUnit

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

        val alarmTime = parseDateTimeToMillis(alarm.date, alarm.startTime)
        val endTime = parseDateTimeToMillis(alarm.date, alarm.endTime)
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

    @SuppressLint("ScheduleExactAlarm")
    override fun scheduleSnoozeAlarm(alarm: Alarm, snoozeMinutes: Int) {
        cancelAlarm(alarm)

        val snoozeTime = System.currentTimeMillis() + (snoozeMinutes * 60 * 1000)

        val snoozeIntent = Intent(context, AlarmBroadcastReceiver::class.java).apply {
            putExtra("ALARM_ID", alarm.id)
            putExtra("ALARM_ACTION", "START")
            putExtra("isSnoozed", true)
        }

        val snoozePendingIntent = PendingIntent.getBroadcast(
            context,
            alarm.id,
            snoozeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            snoozeTime,
            snoozePendingIntent
        )

        Log.d("AlarmScheduler", "Snooze scheduled for $snoozeMinutes minutes")
    }


    override fun scheduleNotification(alarm : Alarm) {
        val alarmId = alarm.id
        val alarmTime = parseDateTimeToMillis(alarm.date, alarm.startTime)
        val endTime = parseDateTimeToMillis(alarm.date, alarm.endTime)
        val cancelTime = alarmTime + (endTime - alarmTime)

        val startInputData = workDataOf(
            "alarmId" to alarmId,
            "action" to "start"
        )

        val endInputData = workDataOf(
            "alarmId" to alarmId,
            "action" to "stop"
        )

        val startWorkRequest = OneTimeWorkRequestBuilder<AlertsWorker>()
            .setInitialDelay(alarmTime, TimeUnit.MILLISECONDS)
            .setInputData(startInputData)
            .build()

        val endWorkRequest = OneTimeWorkRequestBuilder<AlertsWorker>()
            .setInitialDelay(cancelTime, TimeUnit.MILLISECONDS)
            .setInputData(endInputData)
            .build()

        WorkManager.getInstance(context).enqueue(startWorkRequest)
        WorkManager.getInstance(context).enqueue(endWorkRequest)
    }
}