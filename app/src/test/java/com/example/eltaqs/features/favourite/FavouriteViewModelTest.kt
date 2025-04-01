package com.example.eltaqs.features.favourite

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.eltaqs.data.model.CurrentWeatherResponse
import com.example.eltaqs.data.model.FavouriteLocation
import com.example.eltaqs.data.model.ForecastResponse
import com.example.eltaqs.data.model.Response
import com.example.eltaqs.data.repo.WeatherRepository
import com.google.android.gms.maps.model.LatLng
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

    @OptIn(ExperimentalCoroutinesApi::class)
    @RunWith(AndroidJUnit4::class)
    class FavouriteViewModelTest {
        private lateinit var viewModel: FavouriteViewModel
        private lateinit var repository: WeatherRepository

        private lateinit var currentWeather: CurrentWeatherResponse
        private lateinit var forecastWeather: ForecastResponse


        @Before
        fun setup() {
            currentWeather = mockk(relaxed = true)
            forecastWeather = mockk(relaxed = true)

            repository = mockk()
            viewModel = FavouriteViewModel(repository)

            Dispatchers.setMain(StandardTestDispatcher())
        }

        @After
        fun tearDown() {
            Dispatchers.resetMain()
        }

        @Test
        fun insertFavAndGetAllFav() = runTest {
            val testData = listOf(
                FavouriteLocation(
                    "CACHED_HOME",
                    LatLng(50.0, 60.0),
                    currentWeather,
                    forecastWeather
                ),
                FavouriteLocation("Cairo", LatLng(10.0, 10.0), currentWeather, forecastWeather),
                FavouriteLocation("Alexandria", LatLng(20.0, 30.0), currentWeather, forecastWeather)
            )

            coEvery { repository.getAllFavourites() } returns flowOf(testData)

            viewModel.getFavourites()
            advanceUntilIdle()
            val result = viewModel.favourites.first { it !is Response.Loading }

            val expected = listOf(
                FavouriteLocation(
                    "Alexandria",
                    LatLng(20.0, 30.0),
                    currentWeather,
                    forecastWeather
                ),
                FavouriteLocation("Cairo", LatLng(10.0, 10.0), currentWeather, forecastWeather)

            )
            assertEquals(Response.Success(expected), result)

            coVerify { repository.getAllFavourites() }
        }

        @Test
        fun deleteFavAndGetAllFav() = runTest {
            val allLocations = FavouriteLocation("Alexandria", LatLng(20.0, 30.0), currentWeather, forecastWeather)

            coEvery { repository.deleteFavourite(allLocations) } returns 1
            coEvery { repository.getAllFavourites() } returns flowOf(emptyList())

            viewModel.removeFromFavourite(allLocations)
            advanceUntilIdle()

            coVerify {
                repository.deleteFavourite(allLocations)
            }
            coVerify {
                repository.getAllFavourites()
            }
        }
    }