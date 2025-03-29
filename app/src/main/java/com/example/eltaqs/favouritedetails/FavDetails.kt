package com.example.eltaqs.favouritedetails

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.eltaqs.Utils.NetworkConnectivity
import com.example.eltaqs.data.local.AppDataBase
import com.example.eltaqs.data.local.WeatherLocalDataSource
import com.example.eltaqs.data.model.FavouriteLocation
import com.example.eltaqs.data.model.Response
import com.example.eltaqs.data.remote.WeatherRemoteDataSource
import com.example.eltaqs.data.repo.WeatherRepository
import com.example.eltaqs.data.sharedpreference.SharedPrefDataSource
import com.example.eltaqs.home.CurrentWeatherSection
import com.example.eltaqs.home.HourlyForecastRow
import com.example.eltaqs.home.TodayForecastRow
import com.example.eltaqs.home.WeatherStatsRow

@Composable
fun FavDetails(location: FavouriteLocation, onNavigateToDetails: (lat: Double, lon: Double, location: String)-> Unit) {
    val viewModel: FavDetailsViewModel = viewModel(
        factory = FavDetailsViewModelFactory(
            WeatherRepository.getInstance(
                WeatherRemoteDataSource(RetrofitHelper.apiService),
                WeatherLocalDataSource(AppDataBase.getInstance(LocalContext.current).getFavouritesDAO()),
                SharedPrefDataSource.getInstance(LocalContext.current)
            )
        )
    )

    val context = LocalContext.current

    val uiState = viewModel.weatherData.collectAsStateWithLifecycle()
    val windSpeedSymbol = viewModel.getWindSpeedUnitSymbol()
    val tempSymbol = viewModel.getTemperatureUnitSymbol()

    val isInternetAvailable = NetworkConnectivity.isInternetAvailable

    LaunchedEffect(isInternetAvailable) {
        if(isInternetAvailable.value) {
            viewModel.getWeatherAndForecast(location)
        }else {
            viewModel.getItemFromDatabase(location)
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        when (val state = uiState.value) {
            is Response.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentSize()
                ) {
                    CircularProgressIndicator()
                }
            }

            is Response.Success -> {
                val current = state.data.first
                val forecast = state.data.second

                CurrentWeatherSection(current, tempSymbol)
                WeatherStatsRow(current, tempSymbol, windSpeedSymbol)
                TodayForecastRow(location.latLng.latitude, location.latLng.longitude, location.locationName, onNavigateToDetails)
                HourlyForecastRow(forecast, tempSymbol)
            }

            is Response.Error -> {
                Text(
                    text = "Something went wrong: ${state.message}",
                    color = Color.Red,
                    fontSize = 16.sp,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}