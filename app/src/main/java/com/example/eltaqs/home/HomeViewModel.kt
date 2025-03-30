package com.example.eltaqs.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.eltaqs.Utils.NetworkConnectivity
import com.example.eltaqs.data.model.CurrentWeatherResponse
import com.example.eltaqs.data.model.FavouriteLocation
import com.example.eltaqs.data.model.ForecastResponse
import com.example.eltaqs.data.model.Response
import com.example.eltaqs.data.repo.WeatherRepository
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.compose.autocomplete.models.geocoder.Location
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: WeatherRepository) : ViewModel() {
    private val mutableWeatherData = MutableStateFlow<Response<Pair<CurrentWeatherResponse, ForecastResponse>>>(Response.Loading)
    val weatherData = mutableWeatherData.asStateFlow()

    private val _locationState = MutableStateFlow(Location(0.0, 0.0))
    val locationState = _locationState.asStateFlow()


    init {
        onLocationChange()
        getLocation()
    }

    fun getWeatherAndForecast(lat: Double = repository.getMapCoordinates().first,
                              lon: Double = repository.getMapCoordinates().second,
                              units: String = repository.getTemperatureUnit().apiUnit,
                              lang: String = repository.getLanguage().apiCode
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                Log.d("HomeViewModel", "getWeatherAndForecast: $lat, $lon")
                if(lat == 0.0 || lon == 0.0) { return@launch }
                val currentWeather = repository.getCurrentWeather(lat, lon, units, lang).first()
                val forecast = repository.getForecast(lat, lon, units, lang).first()

                mutableWeatherData.value = Response.Loading
                mutableWeatherData.value = Response.Success(Pair(currentWeather, forecast))

                repository.insertFavourite(FavouriteLocation("CACHED_HOME", latLng = LatLng(lat, lon),currentWeather, forecast))

            } catch (e: Exception) {
                mutableWeatherData.value = Response.Error(e.message ?: "Something went wrong")
            }
        }
    }

    fun getWeatherAndForecastFromLocal(lat: Double = repository.getMapCoordinates().first,
                                       lon: Double = repository.getMapCoordinates().second,
                                       units: String = repository.getTemperatureUnit().apiUnit,
                                       lang: String = repository.getLanguage().apiCode
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.getFavouriteByLocation("CACHED_HOME").collect {
                    mutableWeatherData.value = Response.Success(Pair(it.currentWeather, it.forecastWeather))
                }
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

    fun onLocationChange() {
        viewModelScope.launch {
            repository.getLocationChange().collect { latLng ->
                _locationState.value = Location(latLng.first, latLng.second)
                Log.d("HomeViewModel", "onLocationChange: ${_locationState.value}")
            }
        }
    }

    fun getLocation() {
        viewModelScope.launch {
            val latLng = repository.getMapCoordinates()
            _locationState.value = Location(latLng.first, latLng.second)
        }
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