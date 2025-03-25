package com.example.eltaqs.data.remote

import com.example.eltaqs.data.model.CurrentWeatherResponse
import com.example.eltaqs.data.model.ForecastResponse
import com.example.eltaqs.data.model.GeocodingResponse


interface IWeatherRemoteDataSource {
    suspend fun  getCurrentWeather(
        lat: Double,
        lon: Double,
        units: String,
        lang: String
    ): CurrentWeatherResponse

    suspend fun getForecast(
        lat: Double,
        lon: Double,
        units: String,
        lang: String
    ): ForecastResponse

    suspend fun getCoordByCityName(
        cityName: String
    ): GeocodingResponse

    suspend fun getCityNameByCoord(
        latitude: Double,
        longitude: Double
    ): GeocodingResponse

}