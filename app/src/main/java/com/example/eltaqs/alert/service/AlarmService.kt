package com.example.eltaqs.alert.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.eltaqs.R

class AlarmService : Service() {

    private var mediaPlayer : MediaPlayer? = null
    private lateinit var audioManager : AudioManager

    companion object{
        const val CHANNEL_ID = "CHANNEL_ID_ALARM"
        const val NOTIFICATION_ID = 123
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action){
            "Start" -> {
                startAlarm()
            }
            "Stop" -> {
                stopAlarm()
            }
        }
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        startAlarm()
    }

    private fun startAlarm(){
        val resource = resources.openRawResourceFd(R.raw.alert)
        mediaPlayer = MediaPlayer().apply {
            setAudioStreamType(AudioManager.STREAM_ALARM)
            setOnPreparedListener{
                setVolume(1f,1f)
                start()
            }
            setDataSource(resource.fileDescriptor,
                resource.startOffset,
                resource.length)
            resource.close()
            prepareAsync()
            isLooping = true
        }
        val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM)
        audioManager.setStreamVolume(AudioManager.STREAM_ALARM, maxVolume, 0)
        startForeground(NOTIFICATION_ID,
            createNotification()
        )
    }

    private fun stopAlarm(){
        mediaPlayer?.let {
            if(it.isPlaying){
                it.stop()
                it.release()
            }
        }
        mediaPlayer = null
        audioManager.abandonAudioFocus(null)
        stopForeground(true)
        stopSelf()
    }

    private fun createNotification(): Notification {
        createNotificationChannel()

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Alarm is playing")
            .setContentText("Tap to stop the alarm")
            .setSmallIcon(R.drawable.checked)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Alarm Channel",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Channel for alarm notifications"
            }

            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                .createNotificationChannel(channel)
        }
    }
}