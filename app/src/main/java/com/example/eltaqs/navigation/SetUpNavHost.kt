package com.example.eltaqs

import android.location.Location
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable

import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.eltaqs.favourite.FavouriteScreen
import com.example.eltaqs.home.HomeScreen
import com.example.eltaqs.map.MapScreen
import com.example.eltaqs.settings.SettingsScreen

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
            //AlertsScreen()
        }
        composable<ScreenRoutes.Favorite> {
            FavouriteScreen()
        }
        composable<ScreenRoutes.Settings> {
            SettingsScreen() {
                navController.navigate(ScreenRoutes.Map)
            }
        }
        composable<ScreenRoutes.Details> {
           // DetailsScreen()
        }
        composable<ScreenRoutes.Map> {
            MapScreen()
        }
    }
}
