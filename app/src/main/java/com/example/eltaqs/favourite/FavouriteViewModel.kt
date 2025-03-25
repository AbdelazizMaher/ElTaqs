package com.example.eltaqs.favourite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.eltaqs.data.model.FavouriteLocation
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

class FavouriteViewModel(private val repository: WeatherRepository) : ViewModel() {
    private val mutableFavourites = MutableStateFlow<Response<List<FavouriteLocation>>>(Response.Loading)
    val favourites = mutableFavourites.asStateFlow()

    private val mutableMessage = MutableSharedFlow<String>()
    val message = mutableMessage.asSharedFlow()

    fun getFavourites(){
        viewModelScope.launch(Dispatchers.IO) {
            repository.getAllFavourites().catch {
                mutableMessage.emit(it.message ?: "Something went wrong")
            }.collect {
                mutableFavourites.value = Response.Success(it)
            }
        }
    }

    fun removeFromFavourite(location: FavouriteLocation) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteFavourite(location)
            //getFavourites()
        }
    }

    fun addToFavourite(location: FavouriteLocation) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertFavourite(location)
            //getFavourites()
        }
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