package com.example.eltaqs.repo

import com.example.eltaqs.data.model.CurrentWeatherResponse
import com.example.eltaqs.data.model.ForecastResponse


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
}