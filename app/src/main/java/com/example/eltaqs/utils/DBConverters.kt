package com.example.eltaqs.utils

import androidx.room.TypeConverter
import com.example.eltaqs.data.model.CurrentWeatherResponse
import com.example.eltaqs.data.model.ForecastResponse
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson

class DBConverters {
    @TypeConverter
    fun fromLatLng(latLng: LatLng): String {
        return "${latLng.latitude},${latLng.longitude}"
    }

    @TypeConverter
    fun toLatLng(value: String): LatLng {
        val parts = value.split(",")
        return LatLng(parts[0].toDouble(), parts[1].toDouble())
    }

    @TypeConverter
    fun fromCurrentWeather(currentWeather: CurrentWeatherResponse): String {
        return Gson().toJson(currentWeather)
    }

    @TypeConverter
    fun toCurrentWeather(value: String): CurrentWeatherResponse {
        return Gson().fromJson(value, CurrentWeatherResponse::class.java)
    }

    @TypeConverter
    fun fromForecastWeather(forecast: ForecastResponse): String {
        return Gson().toJson(forecast)
    }

    @TypeConverter
    fun toForecastWeather(value: String): ForecastResponse {
        return Gson().fromJson(value, ForecastResponse::class.java)
    }
}