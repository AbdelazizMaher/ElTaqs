package com.example.eltaqs.data.repo

import com.example.eltaqs.utils.settings.enums.Language
import com.example.eltaqs.utils.settings.enums.LocationSource
import com.example.eltaqs.utils.settings.enums.SpeedUnit
import com.example.eltaqs.utils.settings.enums.TemperatureUnit
import com.example.eltaqs.data.model.Alarm
import com.example.eltaqs.data.model.CurrentWeatherResponse
import com.example.eltaqs.data.model.FavouriteLocation
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
    ): Flow<GeocodingResponse>

    suspend fun getCityNameByCoord(
        latitude: Double,
        longitude: Double
    ): Flow<GeocodingResponse>

    suspend fun getAllFavourites() : Flow<List<FavouriteLocation>>
    suspend fun getFavouriteByLocation(locationName: String) : Flow<FavouriteLocation>
    suspend fun updateFavourite(location: FavouriteLocation) : Int
    suspend fun insertFavourite(location: FavouriteLocation) : Long
    suspend fun deleteFavourite(location: FavouriteLocation) : Int

    suspend fun getAlarms(): Flow<List<Alarm>>
    suspend fun getAlarm(alarmId: Int): Alarm?
    suspend fun insertAlarm(alarm: Alarm) : Long
    suspend fun deleteAlarm(alarm: Alarm) : Int

    fun setLocationSource(source: LocationSource)
    fun getLocationSource(): LocationSource

    fun getLocationChange(): Flow<Pair<Double, Double>>
    fun getLocationSourceFlow():  Flow<LocationSource>

    fun setTemperatureUnit(unit: TemperatureUnit)
    fun getTemperatureUnit(): TemperatureUnit

    fun setWindSpeedUnit(unit: SpeedUnit)
    fun getWindSpeedUnit(): SpeedUnit

    fun setLanguage(language: Language)
    fun getLanguage(): Language

    fun setMapCoordinates(lat: Double, lon: Double)
    fun getMapCoordinates(): Pair<Double, Double>
}