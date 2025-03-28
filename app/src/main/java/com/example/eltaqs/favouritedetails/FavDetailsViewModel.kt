package com.example.eltaqs.favouritedetails

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.eltaqs.data.model.CurrentWeatherResponse
import com.example.eltaqs.data.model.FavouriteLocation
import com.example.eltaqs.data.model.ForecastResponse
import com.example.eltaqs.data.model.Response
import com.example.eltaqs.data.repo.WeatherRepository
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class FavDetailsViewModel(private val repository: WeatherRepository) : ViewModel() {
    private val mutableWeatherData = MutableStateFlow<Response<Pair<CurrentWeatherResponse, ForecastResponse>>>(Response.Loading)
    val weatherData = mutableWeatherData.asStateFlow()

    fun getWeatherAndForecast(location: FavouriteLocation,
                              units: String = repository.getTemperatureUnit().apiUnit,
                              lang: String = repository.getLanguage().apiCode
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val currentWeather = repository.getCurrentWeather(location.latLng.latitude, location.latLng.longitude, units, lang).first()
                val forecast = repository.getForecast(location.latLng.latitude, location.latLng.longitude, units, lang).first()

                mutableWeatherData.value = Response.Success(Pair(currentWeather, forecast))
                updateFavourite(FavouriteLocation(location.locationName, location.latLng, currentWeather, forecast))

            } catch (e: Exception) {
                mutableWeatherData.value = Response.Error(e.message ?: "Something went wrong")
            }
        }
    }

    fun getItemFromDatabase(location: FavouriteLocation) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getFavouriteByLocation(location.locationName).collect {
                mutableWeatherData.value = Response.Success(Pair(it.currentWeather, it.forecastWeather))
            }
        }
    }

    fun updateFavourite(location: FavouriteLocation) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertFavourite(location)
        }
    }



    fun getWindSpeedUnitSymbol(): String {
        val speedUnit = repository.getWindSpeedUnit()
        val language = repository.getLanguage()
        return speedUnit.getDisplayName(language)
    }

    fun getTemperatureUnitSymbol(): String {
        val tempUnit = repository.getTemperatureUnit()
        val language = repository.getLanguage()
        return tempUnit.getSymbol(language)
    }
}

class FavDetailsViewModelFactory(private val repository: WeatherRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FavDetailsViewModel::class.java)) {
            return FavDetailsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}