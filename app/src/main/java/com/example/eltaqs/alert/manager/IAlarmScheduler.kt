package com.example.eltaqs.alert.manager

import com.example.eltaqs.data.model.Alarm

interface IAlarmScheduler {
    fun scheduleAlarm(alarm: Alarm)
    fun cancelAlarm(alarm: Alarm)
}