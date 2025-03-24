package com.example.eltaqs.data.model


import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.gms.maps.model.LatLng

@Entity(tableName = "favorite_locations")
data class FavoriteLocation(
    @PrimaryKey
    val latLng: LatLng,
    val locationName: String,
    val currentWeather: CurrentWeatherResponse,
    val forecastWeather: ForecastResponse
)