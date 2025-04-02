package com.example.eltaqs

import android.annotation.SuppressLint
import android.app.Application
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.annotation.RequiresApi
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient

class WeatherApp : Application() {
    @SuppressLint("InlinedApi")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()
        Places.initializeWithNewPlacesApiEnabled(this, BuildConfig.GOOGLE_MAP_API_KEY)
        val intentFilterLangReceiver = IntentFilter(Intent.ACTION_LOCALE_CHANGED)
        registerReceiver(
            LanguageChangeReceiver(), intentFilterLangReceiver,
            RECEIVER_EXPORTED
        )
    }
}