package com.example.eltaqs.data.local

import com.example.eltaqs.data.model.FavouriteLocation
import com.example.eltaqs.db.WeatherDAO
import kotlinx.coroutines.flow.Flow


class WeatherLocalDataSource(private val dao: WeatherDAO) : IWeatherLocalDataSource {
    override suspend fun getAllFavourites(): Flow<List<FavouriteLocation>> {
        return dao.getAllFavoriteLocations()
    }

    override suspend fun insertFavourite(location: FavouriteLocation): Long {
        return dao.insertFavoriteLocation(location)
    }

    override suspend fun deleteFavourite(location: FavouriteLocation): Int {
        return dao.deleteFavoriteLocation(location)
    }
}