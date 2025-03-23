package com.example.eltaqs

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
    object Map : ScreenRoutes()

    @Serializable
    data class Details(val lat: Double, val lon: Double, val location: String) : ScreenRoutes()

}