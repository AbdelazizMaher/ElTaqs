package com.example.eltaqs.data.repo

import com.example.eltaqs.Utils.settings.enums.Language
import com.example.eltaqs.Utils.settings.enums.LocationSource
import com.example.eltaqs.Utils.settings.enums.SpeedUnit
import com.example.eltaqs.Utils.settings.enums.TemperatureUnit
import com.example.eltaqs.data.model.CurrentWeatherResponse
import com.example.eltaqs.data.model.ForecastResponse
import com.example.eltaqs.data.local.WeatherLocalDataSource
import com.example.eltaqs.data.model.FavouriteLocation
import com.example.eltaqs.data.model.GeocodingResponse
import com.example.eltaqs.data.remote.WeatherRemoteDataSource
import com.example.eltaqs.data.sharedpreference.SharedPrefDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf


class WeatherRepository private constructor(
    private val remoteDataSource: WeatherRemoteDataSource,
    private val localDataSource: WeatherLocalDataSource,
    private val sharedPrefDataSource: SharedPrefDataSource
) : IWeatherRepository {

    companion object {
        @Volatile
        private var instance: WeatherRepository? = null
        fun getInstance(
            remoteDataSource: WeatherRemoteDataSource,
            localDataSource: WeatherLocalDataSource,
            sharedPrefDataSource: SharedPrefDataSource
        ): WeatherRepository {
            return instance ?: synchronized(this) {
                val tempInstance = WeatherRepository(remoteDataSource, localDataSource, sharedPrefDataSource)
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

    override suspend fun getCoordByCityName(cityName: String): Flow<GeocodingResponse> {
        return flowOf(remoteDataSource.getCoordByCityName(cityName))
    }

    override suspend fun getCityNameByCoord(
        latitude: Double,
        longitude: Double
    ): Flow<GeocodingResponse> {
        return flowOf(remoteDataSource.getCityNameByCoord(latitude, longitude))
    }

    override suspend fun getAllFavourites(): Flow<List<FavouriteLocation>> {
        return localDataSource.getAllFavourites()
    }

    override suspend fun getFavouriteByLocation(locationName: String): Flow<FavouriteLocation> {
        return localDataSource.getFavouriteByLocation(locationName)
    }

    override suspend fun updateFavourite(location: FavouriteLocation): Int {
        return localDataSource.updateFavourite(location)
    }

    override suspend fun insertFavourite(location: FavouriteLocation): Long {
        return localDataSource.insertFavourite(location)
    }

    override suspend fun deleteFavourite(location: FavouriteLocation): Int {
        return localDataSource.deleteFavourite(location)
    }

    override fun setLocationSource(source: LocationSource) {
        sharedPrefDataSource.setLocationSource(source)
    }

    override fun getLocationSource(): LocationSource {
        return sharedPrefDataSource.getLocationSource()
    }

    override fun getLocationChange(): Flow<Pair<Double, Double>> {
        return sharedPrefDataSource.getLocationChange()
    }

    override fun getLocationSourceFlow():  Flow<LocationSource> {
        return sharedPrefDataSource.getLocationSourceFlow()
    }

    override fun setTemperatureUnit(unit: TemperatureUnit) {
        sharedPrefDataSource.setTemperatureUnit(unit)
    }

    override fun getTemperatureUnit(): TemperatureUnit {
        return sharedPrefDataSource.getTemperatureUnit()
    }

    override fun setWindSpeedUnit(unit: SpeedUnit) {
        sharedPrefDataSource.setWindSpeedUnit(unit)
    }

    override fun getWindSpeedUnit(): SpeedUnit {
        return sharedPrefDataSource.getWindSpeedUnit()
    }

    override fun setLanguage(language: Language) {
        sharedPrefDataSource.setLanguage(language)
    }

    override fun getLanguage(): Language {
        return sharedPrefDataSource.getLanguage()
    }

    override fun setMapCoordinates(lat: Double, lon: Double) {
        sharedPrefDataSource.setMapCoordinates(lat, lon)
    }

    override fun getMapCoordinates(): Pair<Double, Double> {
        return sharedPrefDataSource.getMapCoordinates()
    }
}