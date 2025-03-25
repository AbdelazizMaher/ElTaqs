package com.example.eltaqs.data.model


import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.gms.maps.model.LatLng

@Entity(tableName = "favorite_locations")
data class FavouriteLocation(
    @PrimaryKey
    val locationName: String,
    val latLng: LatLng,
    val currentWeather: CurrentWeatherResponse,
    val forecastWeather: ForecastResponse
)