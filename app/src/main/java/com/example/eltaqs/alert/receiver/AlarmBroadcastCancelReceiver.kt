package com.example.eltaqs.alert.receiver

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.compose.ui.platform.LocalContext
import com.example.eltaqs.alert.manager.AlarmScheduler
import com.example.eltaqs.alert.service.MediaPlayerFacade
import com.example.eltaqs.data.local.AppDataBase
import com.example.eltaqs.data.local.WeatherLocalDataSource
import com.example.eltaqs.data.remote.WeatherRemoteDataSource
import com.example.eltaqs.data.repo.WeatherRepository
import com.example.eltaqs.data.sharedpreference.SharedPrefDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AlarmBroadcastCancelReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return

        val alarmId = intent.getIntExtra("ALARM_ID", -1)
        if (alarmId == -1) return

        val isDeleteAction = intent.action == "DELETE" || intent.getBooleanExtra("isDismiss", false)

        CoroutineScope(Dispatchers.IO).launch {
            val repository = WeatherRepository.getInstance(
                WeatherRemoteDataSource(RetrofitHelper.apiService),
                WeatherLocalDataSource(AppDataBase.getInstance(context).getFavouritesDAO()),
                SharedPrefDataSource.getInstance(context)
            )
            val alarm = repository.getAlarm(alarmId)

            alarm?.let {
                if (!isDeleteAction) {
                    AlarmScheduler(context).cancelAlarm(it)
                    repository.deleteAlarm(it)
                }

                MediaPlayerFacade.stopAudio()
                val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.cancel(alarmId)

            }
        }

    }
}