package com.example.eltaqs.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.eltaqs.data.model.CurrentWeatherResponse
import com.example.eltaqs.data.model.ForecastResponse
import com.example.eltaqs.repo.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: WeatherRepository) : ViewModel() {
    private val mutableCurrentWeather = MutableLiveData<CurrentWeatherResponse>()
    val currentWeather: LiveData<CurrentWeatherResponse> = mutableCurrentWeather

    private val mutableForecast = MutableLiveData<ForecastResponse>()
    val forecast: LiveData<ForecastResponse> = mutableForecast

    fun getCurrentWeather(lat: Double, lon: Double, units: String, lang: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = repository.getCurrentWeather(lat, lon, units, lang)
                if (response != null) {
                    val data: CurrentWeatherResponse = response
                    mutableCurrentWeather.postValue(data)
                }else {
                    Log.d("TAG", "getCurrentWeather: null")
                }
            }catch (e: Exception) {
                    Log.d("TAG", "getCurrentWeather: ${e.message}")
            }
        }
    }

    fun getForecast(lat: Double, lon: Double, units: String, lang: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = repository.getForecast(lat, lon, units, lang)
                if (response != null) {
                    val data: ForecastResponse = response
                    mutableForecast.postValue(data)
                } else {

                }
            } catch (e: Exception) {

            }
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