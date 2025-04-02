package com.example.eltaqs

import android.app.Application
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient

class WeatherApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Places.initializeWithNewPlacesApiEnabled(this, BuildConfig.GOOGLE_MAP_API_KEY)
    }
}