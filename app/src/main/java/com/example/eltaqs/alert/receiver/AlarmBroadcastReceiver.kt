package com.example.eltaqs.alert.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.eltaqs.Utils.createNotification
import com.example.eltaqs.alert.service.MediaPlayerFacade
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

        val repository = WeatherRepository.getInstance(
            WeatherRemoteDataSource(RetrofitHelper.apiService),
            WeatherLocalDataSource(AppDataBase.getInstance(context).getFavouritesDAO()),
            SharedPrefDataSource.getInstance(context)
        )

        CoroutineScope(Dispatchers.IO).launch {
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

    }
}