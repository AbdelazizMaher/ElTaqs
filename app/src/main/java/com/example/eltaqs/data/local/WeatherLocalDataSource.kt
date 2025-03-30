package com.example.eltaqs.data.local

import com.example.eltaqs.data.model.Alarm
import com.example.eltaqs.data.model.FavouriteLocation
import com.example.eltaqs.db.WeatherDAO
import kotlinx.coroutines.flow.Flow


class WeatherLocalDataSource(private val dao: WeatherDAO) : IWeatherLocalDataSource {
    override fun getAllFavourites(): Flow<List<FavouriteLocation>> {
        return dao.getAllFavoriteLocations()
    }

    override suspend fun getFavouriteByLocation(locationName: String): Flow<FavouriteLocation> {
        return dao.getFavoriteLocationByLocation(locationName)
    }

    override suspend fun updateFavourite(location: FavouriteLocation): Int {
        return dao.updateFavoriteLocation(location)
    }

    override suspend fun insertFavourite(location: FavouriteLocation): Long {
        return dao.insertFavoriteLocation(location)
    }

    override suspend fun deleteFavourite(location: FavouriteLocation): Int {
        return dao.deleteFavoriteLocation(location)
    }

    override fun getAlarms(): Flow<List<Alarm>> {
        return dao.getAlarms()
    }

    override suspend fun getAlarm(alarmId: Int): Alarm? {
        return dao.getAlarm(alarmId)
    }

    override suspend fun insertAlarm(alarm: Alarm) : Long {
        return dao.insertAlarm(alarm)
    }

    override suspend fun deleteAlarm(alarm: Alarm) : Int {
        return dao.deleteAlarm(alarm)
    }
}