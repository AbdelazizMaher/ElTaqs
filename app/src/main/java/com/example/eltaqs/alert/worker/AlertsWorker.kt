package com.example.eltaqs.alert.worker

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.eltaqs.alert.service.AlarmService
import com.example.eltaqs.data.local.AppDataBase
import com.example.eltaqs.data.local.WeatherLocalDataSource
import com.example.eltaqs.data.remote.WeatherRemoteDataSource
import com.example.eltaqs.data.repo.WeatherRepository
import com.example.eltaqs.data.sharedpreference.SharedPrefDataSource
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class AlertsWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    @SuppressLint("NewApi")
    override suspend fun doWork(): Result {
        val repository = WeatherRepository.getInstance(
            WeatherRemoteDataSource(RetrofitHelper.apiService),
            WeatherLocalDataSource(AppDataBase.getInstance(context).getFavouritesDAO()),
            SharedPrefDataSource.getInstance(context)
        )

        val alarmId = inputData.getInt("alarmId", -1)
        val endDelay = inputData.getLong("endDelay", 0L)

        val weatherDescription = runBlocking {
            val latlng = repository.getMapCoordinates()
            val unit = repository.getTemperatureUnit().apiUnit
            val language = repository.getLanguage().apiCode

            val weatherResponse = repository.getCurrentWeather(latlng.first, latlng.second, unit, language).first()
            weatherResponse.weather.firstOrNull()?.description ?: "No description available"
        }

        val serviceIntent = Intent(context, AlarmService::class.java).apply {
            putExtra("weatherDescription", weatherDescription)
            putExtra("alarmId", alarmId)
        }
        context.startForegroundService(serviceIntent)

        Handler(Looper.getMainLooper()).postDelayed({
            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.cancel(alarmId)
            context.stopService(serviceIntent)
        }, endDelay)

        return Result.success()
    }
}
