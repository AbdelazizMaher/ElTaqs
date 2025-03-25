package com.example.eltaqs.data.local

import com.example.eltaqs.data.model.FavouriteLocation
import kotlinx.coroutines.flow.Flow


interface IWeatherLocalDataSource {
    suspend fun getAllFavourites() : Flow<List<FavouriteLocation>>
    suspend fun insertFavourite(location: FavouriteLocation) : Long
    suspend fun deleteFavourite(location: FavouriteLocation) : Int
}