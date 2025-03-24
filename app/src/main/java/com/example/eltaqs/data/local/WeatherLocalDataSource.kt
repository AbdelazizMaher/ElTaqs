package com.example.eltaqs.data.local

import com.example.eltaqs.data.model.FavoriteLocation
import com.example.eltaqs.db.WeatherDAO
import kotlinx.coroutines.flow.Flow


class WeatherLocalDataSource(private val dao: WeatherDAO) : IWeatherLocalDataSource {
    override suspend fun getAllFavourites(): Flow<List<FavoriteLocation>> {
        return dao.getAllFavoriteLocations()
    }

    override suspend fun insertFavourite(location: FavoriteLocation): Long {
        return dao.insertFavoriteLocation(location)
    }

    override suspend fun deleteFavourite(location: FavoriteLocation): Int {
        return dao.deleteFavoriteLocation(location)
    }
}