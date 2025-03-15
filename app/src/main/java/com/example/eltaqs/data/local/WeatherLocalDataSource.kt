package com.example.eltaqs.data.local

import com.example.eltaqs.data.local.IWeatherLocalDataSource
import com.example.eltaqs.data.model.FavoriteLocation
import com.example.eltaqs.db.WeatherDAO


class WeatherLocalDataSource(private val dao: WeatherDAO) : IWeatherLocalDataSource {
    override suspend fun insertWeatherLocation(location: FavoriteLocation) {
        dao.insertWeatherLocation(location)
    }

    override suspend fun getWeatherLocationById(id: Int): FavoriteLocation? {
        return dao.getWeatherLocationById(id)
    }

    override suspend fun deleteWeatherLocation(location: FavoriteLocation) {
        dao.deleteWeatherLocation(location)
    }
}