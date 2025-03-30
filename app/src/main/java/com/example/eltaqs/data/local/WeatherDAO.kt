package com.example.eltaqs.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.eltaqs.data.model.Alarm
import com.example.eltaqs.data.model.FavouriteLocation
import kotlinx.coroutines.flow.Flow


@Dao
interface WeatherDAO {
    @Query("SELECT * FROM favorite_locations")
    fun getAllFavoriteLocations(): Flow<List<FavouriteLocation>>

    @Query("SELECT * FROM favorite_locations WHERE locationName = :locationName")
    fun getFavoriteLocationByLocation(locationName: String): Flow<FavouriteLocation>

    @Update
    suspend fun updateFavoriteLocation(location: FavouriteLocation) : Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteLocation(location: FavouriteLocation) : Long

    @Delete
    suspend fun deleteFavoriteLocation(location: FavouriteLocation) : Int

    @Query("SELECT * FROM alarms")
    fun getAlarms(): Flow<List<Alarm>>

    @Query("SELECT * FROM alarms WHERE id = :alarmId")
    suspend fun getAlarm(alarmId: Int): Alarm?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlarm(alarm: Alarm) : Long

    @Delete
    suspend fun deleteAlarm(alarm: Alarm) : Int
}