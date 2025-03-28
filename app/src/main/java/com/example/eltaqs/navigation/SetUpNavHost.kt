package com.example.eltaqs

import android.location.Location
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState

import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.eltaqs.alert.AlertsScreen
import com.example.eltaqs.data.model.FavouriteLocation
import com.example.eltaqs.favourite.FavouriteScreen
import com.example.eltaqs.favouritedetails.FavDetails
import com.example.eltaqs.home.HomeScreen
import com.example.eltaqs.map.MapScreen
import com.example.eltaqs.fivedaysdetails.DetailsScreen
import com.example.eltaqs.settings.SettingsScreen
import com.google.gson.Gson

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SetUpNavHost(
    navController: NavHostController,
    location: Location,
    showBottomBar: MutableState<Boolean>
) {
    NavHost(
        navController = navController,
        startDestination = ScreenRoutes.Home
    ) {
        composable<ScreenRoutes.Home> {
            showBottomBar.value = true
            HomeScreen(location){lat,lon, location ->
                navController.navigate(ScreenRoutes.Details(lat, lon, location))
            }
        }
        composable<ScreenRoutes.Alerts> {
            showBottomBar.value = false
            AlertsScreen()
        }
        composable<ScreenRoutes.Favorite> {
            showBottomBar.value = true
            FavouriteScreen(){
                navController.navigate(ScreenRoutes.FavDetails(it))
            }
        }
        composable<ScreenRoutes.Settings> {
            showBottomBar.value = true
            SettingsScreen() {
                navController.navigate(ScreenRoutes.Map(isMap = true))
            }
        }
        composable<ScreenRoutes.Details> {
            showBottomBar.value = false
            val lat = it.toRoute<ScreenRoutes.Details>().lat
            val lon = it.toRoute<ScreenRoutes.Details>().lon
            val loc = it.toRoute<ScreenRoutes.Details>().location
            DetailsScreen(lat, lon, loc){
                navController.popBackStack()
            }
        }
        composable<ScreenRoutes.Map> {
            showBottomBar.value = false
            val isMap = it.toRoute<ScreenRoutes.Map>().isMap
            MapScreen(isMap)
        }
        composable<ScreenRoutes.FavDetails> {
            showBottomBar.value = true
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
