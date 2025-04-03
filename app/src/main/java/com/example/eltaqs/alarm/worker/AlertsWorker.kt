package com.example.eltaqs.alarm.worker

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.eltaqs.R
import com.example.eltaqs.data.local.AppDataBase
import com.example.eltaqs.data.local.WeatherLocalDataSource
import com.example.eltaqs.data.remote.WeatherRemoteDataSource
import com.example.eltaqs.data.repo.WeatherRepository
import com.example.eltaqs.data.sharedpreference.SharedPrefDataSource
import com.example.eltaqs.utils.translateWeatherCondition
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

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
        val action = inputData.getString("action") ?: "start"
        Log.d("TAG", "Alarm ID: $alarmId, Action: $action")

        val alarm = repository.getAlarm(alarmId) ?: return Result.failure()
        repository.deleteAlarm(alarm)

        if (action == "start") {
            CoroutineScope(Dispatchers.IO).launch {
                val latlng = repository.getMapCoordinates()
                val unit = repository.getTemperatureUnit().apiUnit
                val language = repository.getLanguage().apiCode

                val weatherResponse = repository.getCurrentWeather(latlng.first, latlng.second, unit, language).first()
                val description = weatherResponse.weather.firstOrNull()?.description?.translateWeatherCondition()
                    ?: context.getString(R.string.no_description_available)

                showWeatherNotification(description)
            }
        } else if (action == "stop") {
            cancelWeatherNotification()
        }

        return Result.success()
    }

    @SuppressLint("MissingPermission")
    private fun showWeatherNotification(description: String) {
        val channelId = "weather_alert_channel"
        val notificationId = 1001

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Weather Alerts",
                NotificationManager.IMPORTANCE_HIGH
            )

            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.snow)
            .setContentTitle(context.getString(R.string.current_weather))
            .setContentText(description)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        NotificationManagerCompat.from(context).notify(notificationId, notificationBuilder.build())
    }

    private fun cancelWeatherNotification() {
        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.cancel(1001)
    }
}
