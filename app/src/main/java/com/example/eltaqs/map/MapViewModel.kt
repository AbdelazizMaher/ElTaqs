package com.example.eltaqs.map

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.eltaqs.data.model.GeocodingResponse
import com.example.eltaqs.data.model.Response
import com.example.eltaqs.home.HomeViewModel
import com.example.eltaqs.repo.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class MapViewModel(private val repository: WeatherRepository) : ViewModel() {

    private val mutableLocation = MutableStateFlow<Response<GeocodingResponse>>(Response.Loading)
    val location = mutableLocation.asStateFlow()

    private val mutableMessage = MutableSharedFlow<String>()
    val message = mutableMessage.asSharedFlow()

    fun getLocationByCityName(cityName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try{
                repository.getCoordByCityName(cityName).catch{
                    mutableMessage.emit(it.message.toString())
                }.collect{
                    mutableLocation.value = Response.Success(it[0])
                }
            }catch (e: Exception){
                mutableMessage.emit(e.message.toString())
            }
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