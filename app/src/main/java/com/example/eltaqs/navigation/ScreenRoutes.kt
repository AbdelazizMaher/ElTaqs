package com.example.eltaqs

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
}