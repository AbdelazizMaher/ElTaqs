package com.example.eltaqs.data.local

import com.example.eltaqs.data.model.FavoriteLocation


interface IWeatherLocalDataSource {
    suspend fun insertWeatherLocation(location: FavoriteLocation)
    suspend fun getWeatherLocationById(id: Int): FavoriteLocation?
    suspend fun deleteWeatherLocation(location: FavoriteLocation)
}