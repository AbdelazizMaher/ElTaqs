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
    object Map : ScreenRoutes("map")

    @Serializable
    data class Details(val lat: Double, val lon: Double, val location: String) : ScreenRoutes("details")

}