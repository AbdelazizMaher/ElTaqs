package com.example.eltaqs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.eltaqs.data.repo.WeatherRepository
import com.example.eltaqs.utils.settings.enums.Language
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(private val repository: WeatherRepository) : ViewModel()  {
    private val mutableLanguage = MutableStateFlow(repository.getLanguage())
    val language = mutableLanguage.asStateFlow()

    private val mutableLocationSource = MutableStateFlow(repository.getLocationSource())
    val locationSource = mutableLocationSource.asStateFlow()

    private val mutableLocation = MutableStateFlow(repository.getMapCoordinates())
    val location = mutableLocationSource.asStateFlow()

    fun setLanguage(newLanguage: Language) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.setLanguage(newLanguage)
            mutableLanguage.value = newLanguage
        }
    }

    fun setMapCoordinates(lat: Double, lon: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.setMapCoordinates(lat, lon)
            mutableLocation.value = Pair(lat, lon)
        }
    }
}

class MainViewModelFactory(private val repository: WeatherRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}