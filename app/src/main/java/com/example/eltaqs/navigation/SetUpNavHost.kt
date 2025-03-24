package com.example.eltaqs

import android.location.Location
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable

import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.eltaqs.home.HomeScreen
import com.example.eltaqs.map.MapScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SetUpNavHost(navController: NavHostController, location: Location) {
    NavHost(
        navController = navController,
        startDestination = ScreenRoutes.Home
    ) {
        composable<ScreenRoutes.Home> {
            HomeScreen(location)
        }
        composable<ScreenRoutes.Alerts> {
            AlertsScreen()
        }
        composable<ScreenRoutes.Favorite> {
            FavoriteScreen()
        }
        composable<ScreenRoutes.Settings> {
            SettingsScreen()
        }
        composable<ScreenRoutes.Details> {
           // DetailsScreen()
        }
        composable<ScreenRoutes.Map> {
            MapScreen()
        }
    }
}
