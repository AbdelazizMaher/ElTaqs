package com.example.eltaqs.repo

import com.example.eltaqs.data.model.CurrentWeatherResponse
import com.example.eltaqs.data.model.FavoriteLocation
import com.example.eltaqs.data.model.ForecastResponse
import com.example.eltaqs.data.model.GeocodingResponse
import kotlinx.coroutines.flow.Flow


interface IWeatherRepository {
    suspend fun getCurrentWeather(
        lat: Double,
        lon: Double,
        units: String,
        lang: String): Flow<CurrentWeatherResponse>

    suspend fun getForecast(
        lat: Double,
        lon: Double,
        units: String,
        lang: String): Flow<ForecastResponse>

    suspend fun getCoordByCityName(
        cityName: String
    ): Flow<List<GeocodingResponse>>

    suspend fun getCityNameByCoord(
        latitude: Double,
        longitude: Double
    ): Flow<List<GeocodingResponse>>

    suspend fun getAllFavourites() : Flow<List<FavoriteLocation>>
    suspend fun insertFavourite(location: FavoriteLocation) : Long
    suspend fun deleteFavourite(location: FavoriteLocation) : Int


}