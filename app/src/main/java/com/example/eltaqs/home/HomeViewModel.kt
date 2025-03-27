package com.example.eltaqs.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.eltaqs.data.model.CurrentWeatherResponse
import com.example.eltaqs.data.model.ForecastResponse
import com.example.eltaqs.data.model.Response
import com.example.eltaqs.repo.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: WeatherRepository) : ViewModel() {
    private val mutableWeatherData = MutableStateFlow<Response<Pair<CurrentWeatherResponse, ForecastResponse>>>(Response.Loading)
    val weatherData = mutableWeatherData.asStateFlow()

    fun getWeatherAndForecast(lat: Double,
                              lon: Double,
                              units: String = repository.getTemperatureUnit().apiUnit,
                              lang: String = repository.getLanguage().apiCode
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                mutableWeatherData.value = Response.Loading
                val currentWeather = repository.getCurrentWeather(lat, lon, units, lang).first()
                val forecast = repository.getForecast(lat, lon, units, lang).first()

                mutableWeatherData.value = Response.Success(Pair(currentWeather, forecast))

            } catch (e: Exception) {
                mutableWeatherData.value = Response.Error(e.message ?: "Something went wrong")
            }
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

class HomeViewModelFactory(private val repository: WeatherRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}