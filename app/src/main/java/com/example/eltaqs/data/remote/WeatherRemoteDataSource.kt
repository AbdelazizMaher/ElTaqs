package com.example.eltaqs.data.remote

import com.example.eltaqs.data.model.CurrentWeatherResponse
import com.example.eltaqs.data.model.ForecastResponse
import com.example.eltaqs.data.model.GeocodingResponse


class WeatherRemoteDataSource(private val service: WeatherApiService) : IWeatherRemoteDataSource {
    override suspend fun getCurrentWeather(
        lat: Double,
        lon: Double,
        units: String,
        lang: String
    ): CurrentWeatherResponse? {
        return service.getCurrentWeather(lat, lon, units, lang).body()
    }

    override suspend fun getForecast(
        lat: Double,
        lon: Double,
        units: String,
        lang: String
    ): ForecastResponse? {
        return service.getForecast(lat, lon, units, lang).body()
    }

    override suspend fun getGeocode(address: String): GeocodingResponse? {
        return service.getGeocode(address).body()
    }

}