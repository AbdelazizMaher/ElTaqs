package com.example.eltaqs.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.example.eltaqs.data.model.Alarm
import com.example.eltaqs.data.model.CurrentWeatherResponse
import com.example.eltaqs.data.model.FavouriteLocation
import com.example.eltaqs.data.model.ForecastResponse
import com.example.eltaqs.db.WeatherDAO
import com.google.android.gms.maps.model.LatLng
import io.mockk.mockk
import junit.framework.TestCase.assertNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
class WeatherDAOTest {
    private lateinit var dao : WeatherDAO
    private lateinit var database : AppDataBase
    private lateinit var currentWeather : CurrentWeatherResponse
    private lateinit var forecastWeather : ForecastResponse

    @Before
    fun setup() {
        currentWeather = mockk(relaxed = true)
        forecastWeather = mockk(relaxed = true)
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDataBase::class.java
        ).build()
        dao = database.getFavouritesDAO()
    }

    @After
    fun tearDown() = database.close()

    @Test
    fun insertAlarmAndGetAlarm() = runTest {
        val alarm = Alarm(1, "10:00", "10:05")

        dao.insertAlarm(alarm)
        val result = dao.getAlarm(1)

        assertNotNull(result as Alarm)
        assertThat(result.id, `is`(alarm.id))
        assertThat(result.startTime, `is`(alarm.startTime))
        assertThat(result.endTime, `is`(alarm.endTime))
    }

    @Test
    fun deleteAlarmAndGetAllAlarms() = runTest {
        val alarm = Alarm(1, "10:00", "10:05")

        dao.insertAlarm(alarm)
        dao.deleteAlarm(alarm)

        val result = dao.getAlarms().first()
        assertThat(result.size, `is`(0))
    }

    @Test
    fun insertFavouriteAndGetFavouriteLocation() = runTest {
        val favouriteLocation = FavouriteLocation("Cairo", LatLng(10.0, 10.0), currentWeather, forecastWeather)

        dao.insertFavoriteLocation(favouriteLocation)
        val result = dao.getFavoriteLocationByLocation("Cairo").first()

        assertNotNull(result)
        assertThat(result.locationName, `is`(favouriteLocation.locationName))
        assertThat(result.latLng.latitude, `is`(favouriteLocation.latLng.latitude))
        assertThat(result.latLng.longitude, `is`(favouriteLocation.latLng.longitude))
    }

    @Test
    fun deleteFavouriteAndGetAllFavourites() = runTest {
        val favouriteLocation = FavouriteLocation("Cairo", LatLng(10.0, 10.0), currentWeather, forecastWeather)

        dao.insertFavoriteLocation(favouriteLocation)
        dao.deleteFavoriteLocation(favouriteLocation)

        val result = dao.getAllFavoriteLocations().first()
        assertThat(result.size, `is`(0))
    }
}