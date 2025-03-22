package com.example.eltaqs.data.sharedpreference

import android.content.Context

class SharedPreference private constructor(context: Context) : ISharedPreference  {
    private val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)


    companion object {
        @Volatile
        private var instance: SharedPreference? = null

        fun getInstance(context: Context): SharedPreference {
            return instance ?: synchronized(this) {
                instance ?: SharedPreference(context.applicationContext).also { instance = it }
            }
        }
    }

    override fun setLocationSource(source: LocationSource) {
        prefs.edit()
            .putString("location_source", source.name)
            .apply()
    }

    override fun getLocationSource(): LocationSource {
        val value = prefs.getString("location_source", LocationSource.GPS.name)
        return LocationSource.valueOf(value ?: LocationSource.GPS.name)
    }

    override fun setMapCoordinates(lat: Double, lon: Double) {
        prefs.edit()
            .putFloat("map_lat", lat.toFloat())
            .putFloat("map_lon", lon.toFloat())
            .apply()
    }

    override fun getMapCoordinates(): Pair<Double, Double>? {
        if (!prefs.contains("map_lat") || !prefs.contains("map_lon")) return null
        val lat = prefs.getFloat("map_lat", 0f).toDouble()
        val lon = prefs.getFloat("map_lon", 0f).toDouble()
        return Pair(lat, lon)
    }

    override fun clearPreferences() {
        prefs.edit().clear().apply()
    }
}

enum class LocationSource {
    GPS,
    MAP
}