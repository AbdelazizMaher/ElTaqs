package com.example.eltaqs.features.favourite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.eltaqs.data.model.FavouriteLocation
import com.example.eltaqs.data.model.Response
import com.example.eltaqs.features.home.HomeViewModel
import com.example.eltaqs.data.repo.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class FavouriteViewModel(private val repository: WeatherRepository) : ViewModel() {
    private val mutableFavourites = MutableStateFlow<Response<List<FavouriteLocation>>>(Response.Loading)
    val favourites = mutableFavourites.asStateFlow()

    private val mutableMessage = MutableSharedFlow<String>()
    val message = mutableMessage.asSharedFlow()

    fun getFavourites(){
        viewModelScope.launch(Dispatchers.IO) {
            repository.getAllFavourites().catch {
                mutableMessage.emit(it.message ?: "Something went wrong")
            }.map { list ->
                list
                    .filter { it.locationName != "CACHED_HOME" }
                    .sortedBy { it.locationName }
            }.collect {
                mutableFavourites.value = Response.Success(it)
            }
        }
    }

    fun removeFromFavourite(location: FavouriteLocation) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteFavourite(location)
        }
    }

    fun addToFavourite(location: FavouriteLocation) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertFavourite(location)
        }
    }

    fun getWindSpeedUnitSymbol(): String {
        val speedUnit = repository.getWindSpeedUnit()
        val language = repository.getLanguage()
        return speedUnit.getDisplayName(language)
    }

    fun getTemperatureUnitSymbol(): String {
        val tempUnit = repository.getTemperatureUnit()
        val language = repository.getLanguage()
        return tempUnit.getSymbol(language)
    }
}

class FavouriteViewModelFactory(private val repository: WeatherRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FavouriteViewModel::class.java)) {
            return FavouriteViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}