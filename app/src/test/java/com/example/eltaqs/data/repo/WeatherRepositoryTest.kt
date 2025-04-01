package com.example.eltaqs.data.repo

import androidx.compose.runtime.referentialEqualityPolicy
import com.example.eltaqs.data.local.WeatherLocalDataSource
import com.example.eltaqs.data.model.Alarm
import com.example.eltaqs.data.model.CurrentWeatherResponse
import com.example.eltaqs.data.model.FavouriteLocation
import com.example.eltaqs.data.model.ForecastResponse
import com.example.eltaqs.data.remote.WeatherRemoteDataSource
import com.example.eltaqs.data.sharedpreference.SharedPrefDataSource
import com.google.android.gms.maps.model.LatLng
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Test
import kotlinx.coroutines.test.runTest


class WeatherRepositoryTest {
    private lateinit var localDataSource: WeatherLocalDataSource
    private lateinit var remoteDataSource: WeatherRemoteDataSource
    private lateinit var sharedPrefDataSource: SharedPrefDataSource
    private lateinit var weatherRepository: WeatherRepository

    @Before
    fun setup() {
        localDataSource = mockk(relaxed = true)
        remoteDataSource = mockk(relaxed = true)
        sharedPrefDataSource = mockk(relaxed = true)

        weatherRepository = WeatherRepository(remoteDataSource, localDataSource, sharedPrefDataSource)

    }

    @Test
    fun insertAlarmAndGetAlarm() = runTest {
        val alarm = Alarm(1, "10:00", "10:05")

        coEvery { localDataSource.insertAlarm(any()) } returns 1

        val result = weatherRepository.insertAlarm(alarm)
        assertEquals(1, result)
        coVerify { localDataSource.insertAlarm(alarm) }
    }

    @Test
    fun getWeatherAndInsertFavourite() = runTest {

        coEvery { remoteDataSource.getCurrentWeather(any(), any(), any(), any()) } returns mockk(relaxed = true)
        coEvery { remoteDataSource.getForecast(any(), any(), any(), any()) } returns mockk(relaxed = true)

        val location = FavouriteLocation("Cairo",
            LatLng(10.0, 10.0),
            weatherRepository.getCurrentWeather(10.0, 10.0, "metric", "en").first(),
            weatherRepository.getForecast(10.0, 10.0, "metric", "en").first())

        coEvery { localDataSource.insertFavourite(location) } returns 1L

        val result = weatherRepository.insertFavourite(location)

        assertEquals(1L, result)
        coVerify { localDataSource.insertFavourite(location) }

    }
}