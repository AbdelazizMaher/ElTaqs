package com.example.eltaqs.features.alert

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.eltaqs.data.model.Alarm
import com.example.eltaqs.data.repo.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AlertsViewModel(private val repository: WeatherRepository) : ViewModel()  {
    private val mutableAlerts = MutableStateFlow<List<Alarm>>(emptyList())
    val alerts = mutableAlerts.asStateFlow()

    fun getAlarms() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getAlarms().collect {
                mutableAlerts.value = it
            }
        }
    }

    fun insertAlarm(alarm: Alarm) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertAlarm(alarm)
        }
    }

    fun deleteAlarm(alarm: Alarm) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAlarm(alarm)
        }
    }
}

class AlertsViewModelFactory(private val repository: WeatherRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AlertsViewModel::class.java)) {
            return AlertsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}