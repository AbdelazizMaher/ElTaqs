package com.example.eltaqs.data.db


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.eltaqs.data.model.FavoriteLocation
import com.example.eltaqs.db.WeatherDAO


@Database(entities = [FavoriteLocation::class], version = 1)
abstract class AppDataBase : RoomDatabase() {
    abstract fun getFavouritesDAO(): WeatherDAO

    companion object {
        @Volatile
        private var instance: AppDataBase? = null
        fun getInstance(context: Context): AppDataBase {

            return instance ?: synchronized(this) {
                val tempInstance =  Room.databaseBuilder(
                    context.applicationContext,
                    AppDataBase::class.java, "weather_database"
                ).build()
                instance = tempInstance
                tempInstance
            }
        }
    }
}
