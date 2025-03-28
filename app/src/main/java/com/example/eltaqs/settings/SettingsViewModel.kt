package com.example.eltaqs.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.eltaqs.Utils.settings.enums.Language
import com.example.eltaqs.Utils.settings.enums.LocationSource
import com.example.eltaqs.Utils.settings.enums.SpeedUnit
import com.example.eltaqs.Utils.settings.enums.TemperatureUnit
import com.example.eltaqs.data.repo.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(private val repository: WeatherRepository) : ViewModel() {
    private val mutableTemperatureUnit = MutableStateFlow(TemperatureUnit.CELSIUS)
    val temperatureUnit = mutableTemperatureUnit.asStateFlow()

    private val mutableLocationSource = MutableStateFlow(LocationSource.GPS)
    val locationSource = mutableLocationSource.asStateFlow()

    private val mutableWindSpeedUnit = MutableStateFlow(SpeedUnit.METER_PER_SECOND)
    val windSpeedUnit = mutableWindSpeedUnit.asStateFlow()

    private val mutableLanguage = MutableStateFlow(Language.ENGLISH)
    val language = mutableLanguage.asStateFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch(Dispatchers.IO) {
            mutableTemperatureUnit.value = repository.getTemperatureUnit()
            mutableWindSpeedUnit.value = repository.getWindSpeedUnit()
            mutableLocationSource.value = repository.getLocationSource()
            mutableLanguage.value = repository.getLanguage()
        }
    }

    fun setLocationSource(source: LocationSource) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.setLocationSource(source)
            mutableLocationSource.value = source
        }
    }

    fun setTemperatureUnit(unit: TemperatureUnit) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.setTemperatureUnit(unit)
            mutableTemperatureUnit.value = unit
        }
    }

    fun setWindSpeedUnit(unit: SpeedUnit) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.setWindSpeedUnit(unit)
            mutableWindSpeedUnit.value = unit
        }
    }

    fun setLanguage(newLanguage: Language) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.setLanguage(newLanguage)
            mutableLanguage.value = newLanguage
        }
    }

    fun updateUnitsFromSelection(selectedTempUnit: TemperatureUnit? = null, selectedSpeedUnit: SpeedUnit? = null) {
        val newApiUnit = selectedTempUnit?.apiUnit ?: selectedSpeedUnit?.apiUnit ?: return

        val newTempUnit = selectedTempUnit ?: TemperatureUnit.entries.find { it.apiUnit == newApiUnit } ?: TemperatureUnit.KELVIN
        val newSpeedUnit = selectedSpeedUnit ?: SpeedUnit.entries.find { it.apiUnit == newApiUnit } ?: SpeedUnit.METER_PER_SECOND

        setTemperatureUnit(newTempUnit)
        setWindSpeedUnit(newSpeedUnit)
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