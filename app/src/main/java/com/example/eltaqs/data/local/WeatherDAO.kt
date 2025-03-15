package com.example.eltaqs.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.eltaqs.data.model.FavoriteLocation


@Dao
interface WeatherDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeatherLocation(location: FavoriteLocation)

    @Query("SELECT * FROM favorite_locations WHERE id = :id")
    suspend fun getWeatherLocationById(id: Int): FavoriteLocation?

    @Delete
    suspend fun deleteWeatherLocation(location: FavoriteLocation)
}