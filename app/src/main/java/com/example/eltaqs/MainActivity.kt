package com.example.eltaqs

import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.example.eltaqs.ui.theme.FluidBottomNavigationTheme
import com.example.eltaqs.Utils.LocationProvider
import com.example.eltaqs.Utils.settings.enums.LocationSource
import com.example.eltaqs.component.AnimatedBottomSection
import com.example.eltaqs.data.sharedpreference.SharedPrefDataSource
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch
import java.util.Locale


class MainActivity : ComponentActivity() {
    private lateinit var locationProvider: LocationProvider
    private lateinit var locationState: MutableState<Location>
    lateinit var showBottomBar : MutableState<Boolean>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("TAG", "onCreate: ${SharedPrefDataSource.getInstance(this).getLanguage().apiCode}")
        applyLanguage(SharedPrefDataSource.getInstance(this).getLanguage().apiCode)

        locationProvider = LocationProvider(this)
        setContent {
            FluidBottomNavigationTheme {
                val navController = rememberNavController()
                locationState = remember { mutableStateOf(Location(LocationManager.GPS_PROVIDER)) }
                showBottomBar = remember { mutableStateOf(true) }

                val backgroundGradient = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1B1F36),
                        Color(0xFF2C2F48),
                        Color(0xFF3A3C5B)
                    )
                )

                Scaffold(
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .background(brush = backgroundGradient)
                            .padding(innerPadding)
                            .fillMaxSize()
                    ) {
                        SetUpNavHost(navController = navController, location = locationState.value, showBottomBar = showBottomBar)
                        if(showBottomBar.value) {
                            AnimatedBottomSection(navController = navController)
                        }
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()

        val sharedPref = SharedPrefDataSource.getInstance(this)
        if (SharedPrefDataSource.getInstance(this@MainActivity).getLocationSource() == LocationSource.GPS) {
            locationProvider.fetchLatLong(this) { location ->
                locationState.value = location
                SharedPrefDataSource.getInstance(this@MainActivity).setMapCoordinates(location.latitude, location.longitude)
            }
        }
        lifecycleScope.launch {
            sharedPref.getLocationChange().collect { (lat, lon) ->
                locationState.value.latitude = lat
                locationState.value.longitude = lon
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
        deviceId: Int
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults, deviceId)
        Log.d("TAG", "onRequestPermissionsResult: called")
        locationProvider.handlePermissionResult(requestCode, grantResults, this) { location ->
            locationState.value = location
            SharedPrefDataSource.getInstance(this@MainActivity).setMapCoordinates(location.latitude, location.longitude)
        }
    }

    private fun applyLanguage(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val config = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }
}






