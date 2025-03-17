package com.example.eltaqs

import com.example.eltaqs.home.WeatherItem
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
sealed class ScreenRoutes() {
    @Serializable
    object Home : ScreenRoutes()

    @Serializable
    object Alerts : ScreenRoutes()

    @Serializable
    object Favorite : ScreenRoutes()

    @Serializable
    object Settings : ScreenRoutes()

    @Serializable
    data class Details(val location: String = "", val weatherList: List<WeatherItem> = emptyList(), val selectedIndex: Int = 0, val onItemSelect: (Int) -> Unit = {}) : ScreenRoutes()

}