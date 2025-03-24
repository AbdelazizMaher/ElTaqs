package com.example.eltaqs.repo

import com.example.eltaqs.data.model.CurrentWeatherResponse
import com.example.eltaqs.data.model.ForecastResponse
import com.example.eltaqs.data.local.WeatherLocalDataSource
import com.example.eltaqs.data.model.FavoriteLocation
import com.example.eltaqs.data.model.GeocodingResponse
import com.example.eltaqs.data.remote.WeatherRemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf


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
    ): Flow<CurrentWeatherResponse> {
        return flowOf(remoteDataSource.getCurrentWeather(lat, lon, units, lang))
    }

    override suspend fun getForecast(
        lat: Double,
        lon: Double,
        units: String,
        lang: String
    ): Flow<ForecastResponse> {
        return flowOf(remoteDataSource.getForecast(lat, lon, units, lang))
    }

    override suspend fun getCoordByCityName(cityName: String): Flow<List<GeocodingResponse>> {
        return flowOf(remoteDataSource.getCoordByCityName(cityName))
    }

    override suspend fun getCityNameByCoord(
        latitude: Double,
        longitude: Double
    ): Flow<List<GeocodingResponse>> {
        return flowOf(remoteDataSource.getCityNameByCoord(latitude, longitude))
    }

    override suspend fun getAllFavourites(): Flow<List<FavoriteLocation>> {
        return localDataSource.getAllFavourites()
    }

    override suspend fun insertFavourite(location: FavoriteLocation): Long {
        return localDataSource.insertFavourite(location)
    }

    override suspend fun deleteFavourite(location: FavoriteLocation): Int {
        return localDataSource.deleteFavourite(location)
    }
}