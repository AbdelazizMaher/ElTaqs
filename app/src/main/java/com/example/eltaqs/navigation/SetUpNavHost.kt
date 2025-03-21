package com.example.eltaqs

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable

import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.eltaqs.AlertsScreen
import com.example.eltaqs.FavoriteScreen
import com.example.eltaqs.SettingsScreen
import com.example.eltaqs.home.DetailsScreen
import com.example.eltaqs.home.HomeScreen
import com.example.eltaqs.home.HomeScreen2

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SetUpNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = ScreenRoutes.Home
    ) {
        composable<ScreenRoutes.Home> {
            HomeScreen2()
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
