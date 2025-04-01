package com.example.eltaqs.features.map

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.eltaqs.utils.settings.enums.LocationSource
import com.example.eltaqs.data.model.FavouriteLocation
import com.example.eltaqs.data.model.GeocodingResponse
import com.example.eltaqs.data.model.Response
import com.example.eltaqs.data.repo.WeatherRepository
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MapViewModel(private val repository: WeatherRepository) : ViewModel() {

    private val mutableLocationByCity = MutableStateFlow<Response<GeocodingResponse>>(Response.Loading)
    val locationByCity = mutableLocationByCity.asStateFlow()

    private val mutableCityByLocation = MutableStateFlow<Response<GeocodingResponse>>(Response.Loading)
    val cityByLocation = mutableCityByLocation.asStateFlow()

    private val mutableMessage = MutableSharedFlow<String>()
    val message = mutableMessage.asSharedFlow()

    fun getLocationByCityName(cityName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try{
                repository.getCoordByCityName(cityName).catch{
                    mutableLocationByCity.value = Response.Error(it.message.toString())
                }.collect{
                    mutableLocationByCity.value = Response.Success(it)
                    Log.d("TAG", "getLocationByCityName1: ${it.first.name}")
                }
            }catch (e: Exception){
                mutableMessage.emit(e.message.toString())
            }
        }
    }

    fun getCityNameByLocation(latLng: LatLng) {
        viewModelScope.launch(Dispatchers.IO) {
            try{
                repository.getCityNameByCoord(latLng.latitude, latLng.longitude).catch{
                    mutableCityByLocation.value = Response.Error(it.message.toString())
                }.collect {
                    mutableCityByLocation.value = Response.Success(it)
                    Log.d("TAG", "getCityNameByLocation: ${it.first.name}")
                }
            }catch (e: Exception){
                mutableMessage.emit(e.message.toString())
            }
        }
    }
    fun saveLocation(city: String, latLng: LatLng) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val currentWeatherResponse = repository.getCurrentWeather(latLng.latitude, latLng.longitude, "metric", "en").first()
                val weatherResponse = repository.getForecast(latLng.latitude, latLng.longitude, "metric", "en").first()
                repository.insertFavourite(FavouriteLocation(city, latLng, currentWeatherResponse, weatherResponse))
            }catch (e: Exception){
                mutableMessage.emit(e.message.toString())
            }
        }
    }

    fun setHomeLocation(position: LatLng) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.setMapCoordinates(position.latitude, position.longitude)
        }
    }

    fun setLocationSource(source: LocationSource) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.setLocationSource(source)
        }
    }
}

class MapViewModelFactory(private val repository: WeatherRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MapViewModel::class.java)) {
            return MapViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}