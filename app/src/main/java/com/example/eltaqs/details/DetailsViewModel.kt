package com.example.eltaqs.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.eltaqs.data.model.ForecastResponse
import com.example.eltaqs.repo.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DetailsViewModel(private val repository: WeatherRepository) : ViewModel() {
    private val mutableForecast = MutableLiveData<ForecastResponse>()
    val forecast: LiveData<ForecastResponse> = mutableForecast

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

class DetailsViewModelFactory(private val repository: WeatherRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetailsViewModel::class.java)) {
            return DetailsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}