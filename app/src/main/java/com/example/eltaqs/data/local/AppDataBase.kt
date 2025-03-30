package com.example.eltaqs.data.local


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.eltaqs.Utils.DBConverters
import com.example.eltaqs.data.model.Alarm
import com.example.eltaqs.data.model.FavouriteLocation
import com.example.eltaqs.db.WeatherDAO


@Database(entities = [FavouriteLocation::class, Alarm::class], version = 1)
@TypeConverters(DBConverters::class)
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
