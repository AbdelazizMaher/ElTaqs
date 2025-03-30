package com.example.eltaqs.data.local

import com.example.eltaqs.data.model.Alarm
import com.example.eltaqs.data.model.FavouriteLocation
import kotlinx.coroutines.flow.Flow


interface IWeatherLocalDataSource {
    fun getAllFavourites() : Flow<List<FavouriteLocation>>
    suspend fun getFavouriteByLocation(locationName: String) : Flow<FavouriteLocation>
    suspend fun updateFavourite(location: FavouriteLocation) : Int
    suspend fun insertFavourite(location: FavouriteLocation) : Long
    suspend fun deleteFavourite(location: FavouriteLocation) : Int

    fun getAlarms(): Flow<List<Alarm>>
    suspend fun getAlarm(alarmId: Int): Alarm?
    suspend fun insertAlarm(alarm: Alarm) : Long
    suspend fun deleteAlarm(alarm: Alarm) : Int
}