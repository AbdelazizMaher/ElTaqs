package com.example.eltaqs.alarm.receiver

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.eltaqs.MainActivity
import com.example.eltaqs.utils.createNotification
import com.example.eltaqs.utils.MediaPlayerFacade
import com.example.eltaqs.alarm.manager.AlarmScheduler
import com.example.eltaqs.data.local.AppDataBase
import com.example.eltaqs.data.local.WeatherLocalDataSource
import com.example.eltaqs.data.remote.WeatherRemoteDataSource
import com.example.eltaqs.data.repo.WeatherRepository
import com.example.eltaqs.data.sharedpreference.SharedPrefDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AlarmBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return

        val alarmId = intent.getIntExtra("ALARM_ID", -1)
        if (alarmId == -1) return

        val action = intent.getStringExtra("ALARM_ACTION") ?: "START"

        val repository = WeatherRepository.getInstance(
            WeatherRemoteDataSource(RetrofitHelper.apiService),
            WeatherLocalDataSource(AppDataBase.getInstance(context).getFavouritesDAO()),
            SharedPrefDataSource.getInstance(context)
        )

        CoroutineScope(Dispatchers.IO).launch {
            when (action) {
                "START" -> handleAlarmStart(context, alarmId, repository)
                "STOP" -> handleAlarmStop(context, intent, alarmId, repository)
                "OPEN" -> {
                    handleAlarmStop(context, intent, alarmId, repository)
                    val mainIntent = Intent(context, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    context.startActivity(mainIntent)
                }
            }
        }
    }

    private suspend fun handleAlarmStart(context: Context, alarmId: Int, repository: WeatherRepository) {
        val latlng = repository.getMapCoordinates()
        val unit = repository.getTemperatureUnit().apiUnit
        val language = repository.getLanguage().apiCode

        val weatherFlow = repository.getCurrentWeather(latlng.first, latlng.second, unit, language)
        var weatherDescription = "Weather description not available"

        weatherFlow.collect { weatherResponse ->
            weatherResponse.weather.let {
                weatherDescription = it[0].description
            }
            return@collect
        }

        createNotification(context, alarmId, weatherDescription)
        MediaPlayerFacade.playAudio(context)
    }

    private suspend fun handleAlarmStop(
        context: Context,
        intent: Intent,
        alarmId: Int,
        repository: WeatherRepository
    ) {
        val alarm = repository.getAlarm(alarmId)
        val isDeleteAction = intent.action == "DELETE" || intent.getBooleanExtra("isDismiss", false)

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