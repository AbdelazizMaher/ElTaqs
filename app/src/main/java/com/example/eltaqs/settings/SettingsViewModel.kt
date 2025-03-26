package com.example.eltaqs.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.eltaqs.data.model.Response
import com.example.eltaqs.data.sharedpreference.Language
import com.example.eltaqs.data.sharedpreference.TemperatureUnit
import com.example.eltaqs.data.sharedpreference.WindSpeedUnit
import com.example.eltaqs.repo.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(private val repository: WeatherRepository) : ViewModel() {
    private val mutableTemperatureUnit = MutableStateFlow<Response<TemperatureUnit>>(Response.Loading)
    val temperatureUnit = mutableTemperatureUnit.asStateFlow()

    private val mutableWindSpeedUnit = MutableStateFlow<Response<WindSpeedUnit>>(Response.Loading)
    val windSpeedUnit = mutableWindSpeedUnit.asStateFlow()

    private val mutablelanguage = MutableStateFlow<Response<Language>>(Response.Loading)
    val language = mutablelanguage.asStateFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                mutableTemperatureUnit.value = Response.Success(repository.getTemperatureUnit())
                mutableWindSpeedUnit.value = Response.Success(repository.getWindSpeedUnit())
                mutablelanguage.value = Response.Success(repository.getLanguage())
            } catch (e: Exception) {
                mutableTemperatureUnit.value = Response.Error("Failed to load temperature unit")
                mutableWindSpeedUnit.value = Response.Error("Failed to load wind speed unit")
                mutablelanguage.value = Response.Error("Failed to load language")
            }
        }
    }

    fun setTemperatureUnit(unit: TemperatureUnit) {
        viewModelScope.launch(Dispatchers.IO)  {
            repository.setTemperatureUnit(unit)
            mutableTemperatureUnit.value = Response.Success(unit)
        }
    }

    fun setWindSpeedUnit(unit: WindSpeedUnit) {
        viewModelScope.launch {
            repository.setWindSpeedUnit(unit)
            mutableWindSpeedUnit.value = Response.Success(unit)
        }
    }

    fun setLanguage(language: Language) {
        viewModelScope.launch {
            repository.setLanguage(language)
            mutablelanguage.value = Response.Success(language)
        }
    }
}

class SettingsViewModelFactory(private val repository: WeatherRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            return SettingsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}