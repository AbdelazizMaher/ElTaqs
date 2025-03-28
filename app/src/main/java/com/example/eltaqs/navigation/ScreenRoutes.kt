package com.example.eltaqs

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
sealed class ScreenRoutes(val route: String) {
    @Serializable
    object Home : ScreenRoutes("home")

    @Serializable
    object Alerts : ScreenRoutes("alerts")

    @Serializable
    object Favorite : ScreenRoutes("favorite")

    @Serializable
    object Settings : ScreenRoutes("settings")

    @Serializable
    data class Map(val isMap: Boolean) : ScreenRoutes("map")

    @Serializable
    data class Details(val lat: Double, val lon: Double, val location: String) : ScreenRoutes("details")

    @Serializable
    data class FavDetails(val location: String) : ScreenRoutes("favDetails")

}