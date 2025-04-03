package com.example.eltaqs

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import com.example.eltaqs.utils.LocationProvider
import com.example.eltaqs.utils.NetworkConnectivity
import com.example.eltaqs.utils.settings.enums.LocationSource
import com.example.eltaqs.alarm.receiver.AlarmBroadcastReceiver
import com.example.eltaqs.utils.MediaPlayerFacade
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
    lateinit var snackbarHostState: SnackbarHostState

    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        NetworkConnectivity.startObserving(applicationContext)
        Log.d("TAG", "onCreate: ${SharedPrefDataSource.getInstance(this).getLanguage().apiCode}")
        applyLanguage(SharedPrefDataSource.getInstance(this).getLanguage().apiCode)

        val intentFilterReceiver = IntentFilter("ACTION")
        registerReceiver(
            AlarmBroadcastReceiver(), intentFilterReceiver,
            RECEIVER_EXPORTED)

        MediaPlayerFacade.stopAudio()
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll()

        locationProvider = LocationProvider(this)
        setContent {
            FluidBottomNavigationTheme {
                val navController = rememberNavController()
                showBottomBar = remember { mutableStateOf(true) }
                showfloatingBtn = remember { mutableStateOf(false) }
                onFabClick = remember { mutableStateOf({}) }
                snackbarHostState = remember { SnackbarHostState() }

                val backgroundGradient = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1b3a41),
                        Color(0xFF2d525a),
                        Color(0xFF4a757e)
                    )
                )


                Scaffold(
                    snackbarHost = { SnackbarHost(hostState = snackbarHostState, modifier = Modifier.padding(bottom = 140.dp)) },
                    floatingActionButton = {
                        if (showfloatingBtn.value) {
                            FloatingActionButton(
                                onClick = {
                                    onFabClick.value()
                                },
                                containerColor = colorResource(R.color.black),
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
                        SetUpNavHost(navController = navController, showFloatingBtn = showfloatingBtn, onFabClick= onFabClick, showBottomBar = showBottomBar, snackbarHostState = snackbarHostState)
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
                Log.d("TAG", "onStart: $lat, $lon")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        NetworkConnectivity.stopObserving()

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.d("TAG", "onRequestPermissionsResult: called")
        locationProvider.handlePermissionResult(requestCode, grantResults, this) { location ->
            if(location.latitude == 0.0 && location.longitude == 0.0) return@handlePermissionResult
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






