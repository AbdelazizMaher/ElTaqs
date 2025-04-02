package com.example.eltaqs.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.example.eltaqs.data.model.Alarm
import com.example.eltaqs.data.model.CurrentWeatherResponse
import com.example.eltaqs.data.model.FavouriteLocation
import com.example.eltaqs.data.model.ForecastResponse
import com.example.eltaqs.db.WeatherDAO
import com.google.android.gms.maps.model.LatLng
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import io.mockk.mockk
import junit.framework.TestCase.assertNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@MediumTest
class WeatherLocalDataSourceTest{
    private lateinit var localDataSource: WeatherLocalDataSource
    private lateinit var database: AppDataBase
    private lateinit var dao: WeatherDAO
    private lateinit var currentWeather : CurrentWeatherResponse
    private lateinit var forecastWeather : ForecastResponse

    @Before
    fun setup() {
        currentWeather = mockk(relaxed = true)
        forecastWeather = mockk(relaxed = true)

        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDataBase::class.java
        )
            .allowMainThreadQueries()
            .build()
        dao = database.getFavouritesDAO()
        localDataSource = WeatherLocalDataSource(dao)
    }

    @After
    fun tearDown() = database.close()

    @Test
    fun insertFavouriteAndGetFavouriteLocation() = runTest {
        val favouriteLocation = FavouriteLocation("Cairo", LatLng(10.0, 10.0), currentWeather, forecastWeather)

        localDataSource.insertFavourite(favouriteLocation)
        val result = localDataSource.getFavouriteByLocation("Cairo").first()

        assertNotNull(result)
        assertThat(result.locationName, `is`(favouriteLocation.locationName))
    }

    @Test
    fun deleteFavouriteAndGetAllFavourites() = runTest {
        val favouriteLocation = FavouriteLocation("Cairo", LatLng(10.0, 10.0), currentWeather, forecastWeather)

        localDataSource.insertFavourite(favouriteLocation)
        localDataSource.deleteFavourite(favouriteLocation)

        val result = localDataSource.getAllFavourites().first()
        assertThat(result.size, `is`(0))
    }

    @Test
    fun insertAlarmAndGetAlarm() = runTest {
        val alarm = Alarm(1, "10:00", "10:05", 1623456789000)

        localDataSource.insertAlarm(alarm)
        val result = localDataSource.getAlarm(1)

        assertNotNull(result as Alarm)
        assertThat(result.id, `is`(alarm.id))
    }

    @Test
    fun deleteAlarmAndGetAllAlarms() = runTest {
        val alarm = Alarm(1, "10:00", "10:05", 1623456789000)

        localDataSource.insertAlarm(alarm)
        localDataSource.deleteAlarm(alarm)

        val result = localDataSource.getAlarms().first()
        assertThat(result.size, `is`(0))
    }

}