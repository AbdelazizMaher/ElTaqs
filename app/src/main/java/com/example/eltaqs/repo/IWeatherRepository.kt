package com.example.eltaqs.repo

import com.example.eltaqs.data.model.CurrentWeatherResponse
import com.example.eltaqs.data.model.ForecastResponse
import com.example.eltaqs.data.model.GeocodingResponse


interface IWeatherRepository {
    suspend fun getCurrentWeather(
        lat: Double,
        lon: Double,
        units: String,
        lang: String): CurrentWeatherResponse?

    suspend fun getForecast(
        lat: Double,
        lon: Double,
        units: String,
        lang: String): ForecastResponse?

    suspend fun getCoordByCityName(
        cityName: String
    ): List<GeocodingResponse>?

    suspend fun getCityNameByCoord(
        latitude: Double,
        longitude: Double
    ): List<GeocodingResponse>?

}