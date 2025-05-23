package com.example.eltaqs

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState

import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.eltaqs.features.alert.AlertsScreen
import com.example.eltaqs.data.model.FavouriteLocation
import com.example.eltaqs.features.favourite.FavouriteScreen
import com.example.eltaqs.features.favouritedetails.FavDetails
import com.example.eltaqs.features.home.HomeScreen
import com.example.eltaqs.features.map.MapScreen
import com.example.eltaqs.features.fivedaysdetails.DetailsScreen
import com.example.eltaqs.features.settings.SettingsScreen
import com.google.gson.Gson

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SetUpNavHost(
    navController: NavHostController,
    showFloatingBtn: MutableState<Boolean>,
    onFabClick: MutableState<() -> Unit>,
    showBottomBar: MutableState<Boolean>,
    snackbarHostState: SnackbarHostState
) {
    NavHost(
        navController = navController,
        startDestination = ScreenRoutes.Home
    ) {
        composable<ScreenRoutes.Home> {
            showBottomBar.value = true
            showFloatingBtn.value = false
            HomeScreen(){lat,lon, location ->
                navController.navigate(ScreenRoutes.Details(lat, lon, location))
            }
        }
        composable<ScreenRoutes.Alerts> {
            showBottomBar.value = false
            showFloatingBtn.value = true
            AlertsScreen(snackbarHostState,onFabClick){
                navController.navigate(ScreenRoutes.Home)
            }
        }
        composable<ScreenRoutes.Favorite> {
            showBottomBar.value = true
            showFloatingBtn.value = false
            FavouriteScreen(){
                navController.navigate(ScreenRoutes.FavDetails(it))
            }
        }
        composable<ScreenRoutes.Settings> {
            showBottomBar.value = true
            showFloatingBtn.value = false
            SettingsScreen() {
                navController.navigate(ScreenRoutes.Map(isMap = true))
            }
        }
        composable<ScreenRoutes.Details> {
            showBottomBar.value = false
            showFloatingBtn.value = false
            val lat = it.toRoute<ScreenRoutes.Details>().lat
            val lon = it.toRoute<ScreenRoutes.Details>().lon
            val loc = it.toRoute<ScreenRoutes.Details>().location
            DetailsScreen(lat, lon, loc){
                navController.navigateUp()
            }
        }
        composable<ScreenRoutes.Map> {
            showBottomBar.value = false
            showFloatingBtn.value = true
            val isMap = it.toRoute<ScreenRoutes.Map>().isMap
            MapScreen(isMap) {
                navController.navigate(ScreenRoutes.Home)
            }
            onFabClick.value = {
                navController.navigate(ScreenRoutes.Favorite)
            }
        }
        composable<ScreenRoutes.FavDetails> {
            showBottomBar.value = true
            showFloatingBtn.value = false
            val loc = Gson().fromJson(
                it.toRoute<ScreenRoutes.FavDetails>().location,
                FavouriteLocation::class.java
            )
            FavDetails(location = loc) {
                lat, lon, location ->
                navController.navigate(ScreenRoutes.Details(lat, lon, location))
            }
        }
    }
}
