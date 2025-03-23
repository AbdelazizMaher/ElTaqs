package com.example.eltaqs.map

import android.location.Location
import androidx.lifecycle.ViewModel
import com.example.eltaqs.data.model.GeocodingResponse
import com.example.eltaqs.data.model.Response
import com.example.eltaqs.repo.WeatherRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

class MapViewModel(private val repository: WeatherRepository) : ViewModel() {

    private val mutableLocation = MutableStateFlow<Response<List<GeocodingResponse>>>(Response.Loading)
    val location = mutableLocation.asStateFlow()

    private val mutableMessage = MutableSharedFlow<String>()
    val message = mutableMessage.asSharedFlow()

    fun getLocation() {

    }
}