package com.example.eltaqs.repo

import com.example.eltaqs.data.model.CurrentWeatherResponse
import com.example.eltaqs.data.model.ForecastResponse
import com.example.eltaqs.data.local.WeatherLocalDataSource
import com.example.eltaqs.data.model.GeocodingResponse
import com.example.eltaqs.data.remote.WeatherRemoteDataSource


class WeatherRepository private constructor(
    private val remoteDataSource: WeatherRemoteDataSource,
    private val localDataSource: WeatherLocalDataSource
) : IWeatherRepository {

    companion object {
        @Volatile
        private var instance: WeatherRepository? = null
        fun getInstance(
            remoteDataSource: WeatherRemoteDataSource,
            localDataSource: WeatherLocalDataSource
        ): WeatherRepository {
            return instance ?: synchronized(this) {
                val tempInstance = WeatherRepository(remoteDataSource, localDataSource)
                instance = tempInstance
                tempInstance
            }
        }
    }

    override suspend fun getCurrentWeather(
        lat: Double,
        lon: Double,
        units: String,
        lang: String
    ): CurrentWeatherResponse? {
        return remoteDataSource.getCurrentWeather(lat, lon, units, lang)
    }

    override suspend fun getForecast(
        lat: Double,
        lon: Double,
        units: String,
        lang: String
    ): ForecastResponse? {
        return remoteDataSource.getForecast(lat, lon, units, lang)
    }

    override suspend fun getCoordByCityName(cityName: String): List<GeocodingResponse>? {
        return remoteDataSource.getCoordByCityName(cityName)
    }

    override suspend fun getCityNameByCoord(
        latitude: Double,
        longitude: Double
    ): List<GeocodingResponse>? {
        return remoteDataSource.getCityNameByCoord(latitude, longitude)
    }
}