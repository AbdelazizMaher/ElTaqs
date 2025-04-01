package com.example.eltaqs.features.fivedaysdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.eltaqs.data.model.ForecastResponse
import com.example.eltaqs.data.model.Response
import com.example.eltaqs.data.repo.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class DetailsViewModel(private val repository: WeatherRepository) : ViewModel() {
    private val mutableForecast = MutableStateFlow<Response<ForecastResponse>>(Response.Loading)
    val forecast = mutableForecast.asStateFlow()

    fun getForecast(lat: Double,
                    lon: Double,
                    units: String = repository.getTemperatureUnit().apiUnit,
                    lang: String = repository.getLanguage().apiCode
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getForecast(lat, lon, units, lang).catch {
            }.collect {
                mutableForecast.value = Response.Success(it)
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