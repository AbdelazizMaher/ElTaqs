package com.example.eltaqs.alarm.manager

import android.content.Context
import com.example.eltaqs.data.model.Alarm

interface IAlarmScheduler {
    fun scheduleAlarm(alarm: Alarm)
    fun cancelAlarm(alarm: Alarm)
    fun scheduleSnoozeAlarm(alarm: Alarm, snoozeMinutes: Int)

    fun scheduleNotification(alarm : Alarm)
}