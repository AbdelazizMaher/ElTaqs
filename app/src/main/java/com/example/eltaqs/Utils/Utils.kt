package com.example.eltaqs.Utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.widget.ImageView
import androidx.compose.animation.core.Easing
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.ui.unit.LayoutDirection
import androidx.core.app.NotificationCompat
import com.airbnb.lottie.LottieAnimationView
import com.example.eltaqs.MainActivity
import com.example.eltaqs.R
import com.example.eltaqs.alert.receiver.AlarmBroadcastReceiver
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

fun Easing.transform(from: Float, to: Float, value: Float): Float {
    return transform(((value - from) * (1f / (to - from))).coerceIn(0f, 1f))
}

operator fun PaddingValues.times(value: Float): PaddingValues = PaddingValues(
    top = calculateTopPadding() * value,
    bottom = calculateBottomPadding() * value,
    start = calculateStartPadding(LayoutDirection.Ltr) * value,
    end = calculateEndPadding(LayoutDirection.Ltr) * value
)

fun restartActivity(context: Context) {
    val intent = (context as? Activity)?.intent
    intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(intent)
    (context as? Activity)?.finish()
}

fun createNotification(
    context: Context,
    alarmId: Int,
    weatherDescription: String,
) {
    val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            "ALARM_CHANNEL",
            "Alarm Notifications",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Channel for alarm notifications"
        }
        notificationManager.createNotificationChannel(channel)
    }

    val deleteIntent = Intent(context, AlarmBroadcastReceiver::class.java).apply {
        putExtra("ALARM_ID", alarmId)
        putExtra("ALARM_ACTION", "STOP")
        action = "DELETE"
    }
    val deletePendingIntent = PendingIntent.getBroadcast(
        context,
        alarmId,
        deleteIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val cancelIntent = Intent(context, AlarmBroadcastReceiver::class.java).apply {
        putExtra("ALARM_ID", alarmId)
        putExtra("ALARM_ACTION", "STOP")
    }
    val cancelPendingIntent = PendingIntent.getBroadcast(
        context,
        alarmId,
        cancelIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val openIntent = Intent(context, AlarmBroadcastReceiver::class.java).apply {
        putExtra("ALARM_ID", alarmId)
        putExtra("ALARM_ACTION", "OPEN")
    }
    val tempAlarmId = alarmId + 10
    val openPendingIntent = PendingIntent.getBroadcast(
        context,
        tempAlarmId,
        openIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val notification = NotificationCompat.Builder(context, "ALARM_CHANNEL")
        .setContentTitle("Alarm: Weather awaits!")
        .setContentText("Current weather: $weatherDescription")
        .setSmallIcon(R.drawable.snow)
        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM))
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        .setAutoCancel(true)
        .addAction(
            R.drawable.snow,
            "Cancel",
            cancelPendingIntent
        )
        .addAction(
            R.drawable.hail,
            "Open",
            openPendingIntent
        )
        .setDeleteIntent(deletePendingIntent)
        .build()

    notificationManager.notify(alarmId, notification)
}

fun String.isValidTimeFormat(): Boolean {
    return this.matches(Regex("^[0-9].*"))
}

fun parseTimeToMillis(timeString: String): Long {
    val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault()).apply {
        timeZone = TimeZone.getDefault()
    }

    val date = sdf.parse(timeString) ?: return 0L

    val calendar = Calendar.getInstance()
    calendar.time = date

    val now = Calendar.getInstance()
    calendar.set(Calendar.YEAR, now.get(Calendar.YEAR))
    calendar.set(Calendar.MONTH, now.get(Calendar.MONTH))
    calendar.set(Calendar.DAY_OF_MONTH, now.get(Calendar.DAY_OF_MONTH))

    return calendar.timeInMillis
}

@SuppressLint("NewApi")
fun isFutureDateTime(selectedDate: Long?, selectedTime: String): Boolean {
    if (selectedDate == null) return false

    val formatter = DateTimeFormatter.ofPattern("hh:mm a")
    val selectedLocalTime = LocalTime.parse(selectedTime, formatter)

    val currentDateTime = LocalDateTime.now()
    val selectedDateTime = Instant.ofEpochMilli(selectedDate).atZone(ZoneId.systemDefault()).toLocalDate().atTime(selectedLocalTime)

    return selectedDateTime.isAfter(currentDateTime)
}

fun Long.startOfDayMillis(): Long {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = this
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    return calendar.timeInMillis
}


fun isEndTimeValid(startTime: String, endTime: String): Boolean {
    val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault()).apply {
        timeZone = TimeZone.getDefault()
    }
    val start = sdf.parse(startTime)
    val end = sdf.parse(endTime)

    return if (start != null && end != null) {
        end.after(start)
    } else {
        false
    }
}

fun String.translateWeatherCondition(): String {
    val map = mapOf(
        "clear sky" to mapOf("ar" to "سماء صافية"),
        "few clouds" to mapOf("ar" to "سحب قليلة"),
        "scattered clouds" to mapOf("ar" to "سحب متناثرة"),
        "broken clouds" to mapOf("ar" to "سحب متقطعة"),
        "shower rain" to mapOf("ar" to "مطر غزير"),
        "rain" to mapOf("ar" to "مطر"),
        "thunderstorm" to mapOf("ar" to "عاصفة رعدية"),
        "snow" to mapOf("ar" to "ثلج"),
        "mist" to mapOf("ar" to "ضباب"),
        "light rain" to mapOf("ar" to "مطر خفيف"),
        "moderate rain" to mapOf("ar" to "مطر معتدل"),
        "heavy intensity rain" to mapOf("ar" to "مطر غزير"),
        "very heavy rain" to mapOf("ar" to "مطر شديد جدًا"),
        "extreme rain" to mapOf("ar" to "مطر شديد"),
        "freezing rain" to mapOf("ar" to "مطر متجمد"),
        "light snow" to mapOf("ar" to "ثلج خفيف"),
        "heavy snow" to mapOf("ar" to "ثلج كثيف"),
        "sleet" to mapOf("ar" to "مطر ثلجي"),
        "shower sleet" to mapOf("ar" to "زخات مطر ثلجي"),
        "light rain and snow" to mapOf("ar" to "مطر خفيف وثلج"),
        "rain and snow" to mapOf("ar" to "مطر وثلج"),
        "light shower snow" to mapOf("ar" to "زخات ثلج خفيفة"),
        "heavy shower snow" to mapOf("ar" to "زخات ثلج كثيفة"),
        "fog" to mapOf("ar" to "ضباب كثيف"),
        "haze" to mapOf("ar" to "ضباب خفيف"),
        "dust" to mapOf("ar" to "غبار"),
        "sand" to mapOf("ar" to "رمال"),
        "volcanic ash" to mapOf("ar" to "رماد بركاني"),
        "squalls" to mapOf("ar" to "عواصف"),
        "tornado" to mapOf("ar" to "إعصار")
    )

    val language = Locale.getDefault().language
    return map[this]?.get(language) ?: this
}

fun String.getWeatherNotification(): String {
    val notifications = mapOf(
        "01d" to mapOf(
            "en" to "Clear sky during the day! Enjoy the sunshine. ☀️",
            "ar" to "سماء صافية خلال النهار! استمتع بأشعة الشمس. ☀️"
        ),
        "01n" to mapOf(
            "en" to "Clear night sky! Perfect for stargazing. 🌙",
            "ar" to "سماء صافية في الليل! مثالية لمشاهدة النجوم. 🌙"
        ),
        "02d" to mapOf(
            "en" to "A few clouds in the sky, but still a nice day! ⛅",
            "ar" to "بعض السحب في السماء، لكن الجو لا يزال جميلاً! ⛅"
        ),
        "02n" to mapOf(
            "en" to "Partly cloudy night! Enjoy the cool breeze. 🌌",
            "ar" to "ليلة غائمة جزئيًا! استمتع بالنسيم البارد. 🌌"
        ),
        "03d" to mapOf(
            "en" to "Scattered clouds today. ☁️",
            "ar" to "غيوم متفرقة اليوم. ☁️"
        ),
        "03n" to mapOf(
            "en" to "Scattered clouds at night. 🌥️",
            "ar" to "غيوم متفرقة في الليل. 🌥️"
        ),
        "04d" to mapOf(
            "en" to "Broken clouds covering the sky. 🌥️",
            "ar" to "غيوم متقطعة تغطي السماء. 🌥️"
        ),
        "04n" to mapOf(
            "en" to "Broken clouds tonight. Might feel chilly! 🌙",
            "ar" to "غيوم متقطعة الليلة. قد يكون الجو باردًا! 🌙"
        ),
        "09d" to mapOf(
            "en" to "Shower rain expected. Carry an umbrella! 🌧️",
            "ar" to "متوقع هطول أمطار غزيرة. احمل مظلة! 🌧️"
        ),
        "09n" to mapOf(
            "en" to "Shower rain at night. Stay warm! 🌧️",
            "ar" to "أمطار غزيرة في الليل. ابقَ دافئًا! 🌧️"
        )
    )

    val language = Locale.getDefault().language
    return notifications[this]?.get(language) ?: "Weather update not available."
}

fun getWeatherIcon(iconId: String): Int {
    return when (iconId) {
        "01d" -> R.raw.clearsunnyy
        "01n" -> R.raw.clearnight
        "02d" -> R.raw.sunnywithclouds
        "02n" -> R.raw.cloudynight
        "03d", "03n" -> R.raw.cloudywithwind
        "04d", "04n" -> R.raw.cloudswhitandgray
        "09d", "09n" -> R.raw.basicrain
        "10d" -> R.raw.sunnyrain
        "10n" -> R.raw.nightrain
        "11d" -> R.raw.sunnythunder
        "11n" -> R.raw.thunder
        "13d" -> R.raw.sunnysnow
        "13n" -> R.raw.nightsnow
        else -> R.raw.windyclouds
    }
}


@SuppressLint("ResourceType")
fun getWeatherIconForItems(iconId: String): Int {
    return when (iconId) {
        "01d" -> R.drawable.clearsunny
        "01n" -> R.drawable.clearnigth
        "02d" -> R.drawable.ddsunny
        "02n" -> R.drawable.darkcloudynightpng
        "03d", "03n" -> R.drawable.cloudyempty
        "04d", "04n" -> R.drawable.cloudywithwind
        "09d" -> R.drawable.ddrainy
        "09n" -> R.drawable.simplwnightrainy
        "10d" -> R.drawable.heavyddrainy
        "10n" -> R.drawable.rainnight
        "11d" -> R.drawable.thunderstorm
        "11n" -> R.drawable.nightthunder
        "13d" -> R.drawable.ddsnow
        "13n" -> R.drawable.nnsnow
        else -> R.drawable.cloudyempty
    }
}
