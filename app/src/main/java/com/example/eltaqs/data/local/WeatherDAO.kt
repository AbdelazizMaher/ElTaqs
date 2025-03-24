package com.example.eltaqs.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.eltaqs.data.model.FavoriteLocation
import kotlinx.coroutines.flow.Flow


@Dao
interface WeatherDAO {
    @Query("SELECT * FROM favorite_locations")
    fun getAllFavoriteLocations(): Flow<List<FavoriteLocation>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteLocation(location: FavoriteLocation) : Long

    @Delete
    suspend fun deleteFavoriteLocation(location: FavoriteLocation) : Int
}