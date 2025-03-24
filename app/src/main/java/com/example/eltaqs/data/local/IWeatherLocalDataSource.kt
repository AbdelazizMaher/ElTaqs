package com.example.eltaqs.data.local

import com.example.eltaqs.data.model.FavoriteLocation
import kotlinx.coroutines.flow.Flow


interface IWeatherLocalDataSource {
    suspend fun getAllFavourites() : Flow<List<FavoriteLocation>>
    suspend fun insertFavourite(location: FavoriteLocation) : Long
    suspend fun deleteFavourite(location: FavoriteLocation) : Int
}