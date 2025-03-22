package com.example.eltaqs

import android.location.Location
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable

import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.eltaqs.home.HomeScreen2

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SetUpNavHost(navController: NavHostController, location: Location) {
    NavHost(
        navController = navController,
        startDestination = ScreenRoutes.Home
    ) {
        composable<ScreenRoutes.Home> {
            HomeScreen2(location)
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
    }
}
