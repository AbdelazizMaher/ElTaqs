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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.example.eltaqs.ui.theme.FluidBottomNavigationTheme
import com.example.eltaqs.Utils.LocationProvider
import com.example.eltaqs.Utils.NetworkConnectivity
import com.example.eltaqs.Utils.settings.enums.LocationSource
import com.example.eltaqs.component.AnimatedBottomSection
import com.example.eltaqs.data.sharedpreference.SharedPrefDataSource
import com.google.android.libraries.places.api.Places
import kotlinx.coroutines.launch
import java.util.Locale


class MainActivity : ComponentActivity() {
    private lateinit var locationProvider: LocationProvider
    lateinit var showBottomBar : MutableState<Boolean>
    lateinit var showfloatingBtn: MutableState<Boolean>
    lateinit var onFabClick: MutableState<() -> Unit>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        NetworkConnectivity.startObserving(applicationContext)
        Log.d("TAG", "onCreate: ${SharedPrefDataSource.getInstance(this).getLanguage().apiCode}")
        applyLanguage(SharedPrefDataSource.getInstance(this).getLanguage().apiCode)
        Places.initializeWithNewPlacesApiEnabled(this, BuildConfig.GOOGLE_MAP_API_KEY)

        locationProvider = LocationProvider(this)
        setContent {
            FluidBottomNavigationTheme {
                val navController = rememberNavController()
                showBottomBar = remember { mutableStateOf(true) }
                showfloatingBtn = remember { mutableStateOf(false) }
                onFabClick = remember { mutableStateOf({}) }

                val backgroundGradient = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1B1F36),
                        Color(0xFF2C2F48),
                        Color(0xFF3A3C5B)
                    )
                )

                Scaffold(
                    floatingActionButton = {
                        if (showfloatingBtn.value) {
                            FloatingActionButton(
                                onClick = {
                                    onFabClick.value()
                                },
                                containerColor = colorResource(R.color.purple_700),
                                shape = CircleShape,
                                modifier = Modifier.offset(y = (-120).dp)
                            ) {
                                Icon(
                                    Icons.Default.Favorite,
                                    contentDescription = "Favorite",
                                    tint = Color.White
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .background(brush = backgroundGradient)
                            .padding(innerPadding)
                            .fillMaxSize()
                    ) {
                        SetUpNavHost(navController = navController, showFloatingBtn = showfloatingBtn, onFabClick= onFabClick, showBottomBar = showBottomBar)
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
        lifecycleScope.launch {
            sharedPref.getLocationSourceFlow().collect { locationSource ->
                if (locationSource == LocationSource.GPS) {
                    locationProvider.fetchLatLong(this@MainActivity) { location ->
                        sharedPref.setMapCoordinates(location.latitude, location.longitude)
                    }
                }
            }
        }

        lifecycleScope.launch {
            sharedPref.getLocationChange().collect { (lat, lon) ->
                sharedPref.setMapCoordinates(lat, lon)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        NetworkConnectivity.stopObserving()
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






