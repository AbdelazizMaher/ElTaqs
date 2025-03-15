package com.example.logindemo

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.productscoroutine.db.AppDataBase
import com.example.productscoroutine.network.RetrofitHelper
import com.example.productsmvvm.data.local.WeatherLocalDataSource
import com.example.productsmvvm.data.remote.WeatherRemoteDataSource
import com.example.projecttest.AlertsScreen
import com.example.projecttest.FavoriteScreen
import com.example.projecttest.SettingsScreen
import com.example.projecttest.home.HomeScreen
import com.example.projecttest.home.HomeViewModelFactory
import com.example.projecttest.repo.WeatherRepository

@Composable
fun SetUpNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = ScreenRoutes.Home
    ) {
        composable<ScreenRoutes.Home> {
            HomeScreen()
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
    }
}
