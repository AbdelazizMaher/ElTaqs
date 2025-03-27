package com.example.eltaqs.Utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices


class LocationProvider(private val context: Context) {
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    private lateinit var locationCallback: LocationCallback

    fun fetchLatLong(activity: Activity, onResult: (Location) -> Unit) {
        if (!checkPermissions()) {
            Log.d("TAG", "fetchLatLong: Permissions not granted, requesting permissions")
            requestPermissions(activity)
            return
        }

        if (!isLocationEnabled()) {
            Log.d("TAG", "fetchLatLong: Location services disabled")
            enableLocationServices()
            return
        }

        Log.d("TAG", "fetchLatLong: Fetching fresh location")
        getFreshLocation { location ->
            Log.d("TAG", "fetchLatLong: Got location - $location")
            onResult(location)
        }
    }

    @SuppressLint("MissingPermission")
    private fun getFreshLocation(onLocationFetched: (Location) -> Unit) {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val location = locationResult.lastLocation
                location?.let { onLocationFetched(it) }
                fusedLocationClient.removeLocationUpdates(this)
            }
        }

        fusedLocationClient.requestLocationUpdates(
            LocationRequest.Builder(500)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .build(),
            locationCallback,
            Looper.getMainLooper()
        )
    }

    private fun checkPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(
            context, android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    context, android.Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions(activity: Activity) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            REQUEST_LOCATION_CODE
        )
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun enableLocationServices() {
        Toast.makeText(context, "Please enable location services", Toast.LENGTH_SHORT).show()
        context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
    }

    fun handlePermissionResult(
        requestCode: Int,
        grantResults: IntArray,
        activity: Activity,
        onResult: (Location) -> Unit
    ) {
        if (requestCode == REQUEST_LOCATION_CODE) {
            Log.d("TAG", "handlePermissionResult called with grantResults: ${grantResults.joinToString()}")
            if (grantResults.isNotEmpty() &&
                (grantResults[0] == PackageManager.PERMISSION_GRANTED ||
                        grantResults.getOrNull(1) == PackageManager.PERMISSION_GRANTED)
            ) {
                fetchLatLong(activity, onResult)
            } else {
                Toast.makeText(context, "Location permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        const val REQUEST_LOCATION_CODE = 999
    }
}
