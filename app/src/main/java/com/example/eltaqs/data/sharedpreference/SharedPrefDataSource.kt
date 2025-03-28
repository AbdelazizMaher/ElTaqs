package com.example.eltaqs.data.sharedpreference

import android.content.Context
import android.content.SharedPreferences
import com.example.eltaqs.Utils.settings.enums.Language
import com.example.eltaqs.Utils.settings.enums.LocationSource
import com.example.eltaqs.Utils.settings.enums.SpeedUnit
import com.example.eltaqs.Utils.settings.enums.TemperatureUnit
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow

class SharedPrefDataSource private constructor(context: Context) : ISharedPreference {
    private val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    companion object {
        @Volatile
        private var instance: SharedPrefDataSource? = null

        fun getInstance(context: Context): SharedPrefDataSource {
            return instance ?: synchronized(this) {
                instance ?: SharedPrefDataSource(context.applicationContext).also { instance = it }
            }
        }
    }

    override fun getLocationChange(): Flow<Pair<Double, Double>> = callbackFlow {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == "map_lat" || key == "map_lon") {
                trySend(getMapCoordinates())
            }
        }
        prefs.registerOnSharedPreferenceChangeListener(listener)
        awaitClose { prefs.unregisterOnSharedPreferenceChangeListener(listener) }
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

    override fun getMapCoordinates(): Pair<Double, Double> {
        val lat = prefs.getFloat("map_lat", 0f).toDouble()
        val lon = prefs.getFloat("map_lon", 0f).toDouble()
        return Pair(lat, lon)
    }

    override fun setTemperatureUnit(unit: TemperatureUnit) {
        prefs.edit()
            .putString("temperature_unit", unit.name)
            .apply()
    }

    override fun getTemperatureUnit(): TemperatureUnit {
        val value = prefs.getString("temperature_unit", TemperatureUnit.CELSIUS.name)
        return TemperatureUnit.valueOf(value ?: TemperatureUnit.CELSIUS.name)
    }

    override fun setWindSpeedUnit(unit: SpeedUnit) {
        prefs.edit()
            .putString("wind_speed_unit", unit.name)
            .apply()
    }

    override fun getWindSpeedUnit(): SpeedUnit {
        val value = prefs.getString("wind_speed_unit", SpeedUnit.METER_PER_SECOND.name)
        return SpeedUnit.valueOf(value ?: SpeedUnit.METER_PER_SECOND.name)
    }

    override fun setLanguage(language: Language) {
        prefs.edit()
            .putString("app_language", language.name)
            .apply()
    }

    override fun getLanguage(): Language {
        val value = prefs.getString("app_language", Language.ENGLISH.name)
        return Language.valueOf(value ?: Language.ENGLISH.name)
    }

    override fun clearPreferences() {
        prefs.edit().clear().apply()
    }
}


