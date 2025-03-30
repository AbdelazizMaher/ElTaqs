package com.example.eltaqs.map

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.eltaqs.BuildConfig
import com.example.eltaqs.Utils.settings.enums.LocationSource
import com.example.eltaqs.data.local.AppDataBase
import com.example.eltaqs.data.local.WeatherLocalDataSource
import com.example.eltaqs.data.model.Response
import com.example.eltaqs.data.remote.WeatherRemoteDataSource
import com.example.eltaqs.data.sharedpreference.SharedPrefDataSource
import com.example.eltaqs.data.repo.WeatherRepository
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.LocationBias
import com.google.android.libraries.places.api.model.PlaceTypes
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.net.kotlin.awaitFindAutocompletePredictions
import com.google.android.libraries.places.compose.autocomplete.components.PlacesAutocompleteTextField
import com.google.android.libraries.places.compose.autocomplete.models.AutocompletePlace
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState


@Composable
fun MapScreen(isMap: Boolean = false, onBackClick: () -> Unit){
    val viewModel: MapViewModel = viewModel(
        factory = MapViewModelFactory(
            WeatherRepository.getInstance(
                WeatherRemoteDataSource(RetrofitHelper.apiService),
                WeatherLocalDataSource(AppDataBase.getInstance(LocalContext.current).getFavouritesDAO()),
                SharedPrefDataSource.getInstance(LocalContext.current)
            )
        )
    )

    val context = LocalContext.current
    val locationState by viewModel.locationByCity.collectAsStateWithLifecycle()


    val placesClient = Places.createClient(context)

    val bias: LocationBias = RectangularBounds.newInstance(
        LatLng(39.9, -105.5), // SW lat, lng
        LatLng(40.1, -105.0) // NE lat, lng
    )

    var isTapped by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }
    var predictions by remember { mutableStateOf(emptyList<AutocompletePrediction>()) }
    var markerState = rememberMarkerState(position = LatLng(1.35, 103.87))
    var result by remember { mutableStateOf<AutocompletePlace?>(null) }
    var selectedCityName by remember { mutableStateOf<String>("") }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(markerState.position, 10f)
    }

    LaunchedEffect(markerState.position) {
        cameraPositionState.position = CameraPosition.fromLatLngZoom(markerState.position, 10f)
    }

    LaunchedEffect(locationState) {
        if (locationState is Response.Success) {
            val locationResult = (locationState as Response.Success).data[0]
            val newLatLng = LatLng(locationResult.lat, locationResult.lon)

            markerState.position = newLatLng
            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLngZoom(newLatLng, 12f),
                durationMs = 1000
            )
            viewModel.getCityNameByLocation(newLatLng)
            Log.d("TAG", "MapScreen1: ${newLatLng}")
            Log.d("TAG", "MapScreen2: ${locationResult.name}")
            isTapped = true
        }
    }

    LaunchedEffect(searchText) {
        if (searchText.isNotEmpty()) {
            val response = placesClient.awaitFindAutocompletePredictions {
                locationBias = bias
                typesFilter = listOf(PlaceTypes.CITIES)
                query = searchText
            }
            predictions = response.autocompletePredictions
        }
    }

    val cityByLocationState by viewModel.cityByLocation.collectAsStateWithLifecycle()

    LaunchedEffect(cityByLocationState) {
        if (cityByLocationState is Response.Success) {
            selectedCityName = (cityByLocationState as Response.Success).data[0].name
            Log.d("TAG", "MapScreen3: ${selectedCityName}")
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(mapType = MapType.HYBRID),
            onMapClick = {
                latLng -> markerState.position = latLng
                cameraPositionState.position = CameraPosition.fromLatLngZoom(latLng, 10f)
                viewModel.getCityNameByLocation(latLng)
                isTapped = true
            }
        ) {
            Marker(
                state = markerState,
                title = "Add to favorites",
                snippet = result?.secondaryText.toString(),
                visible = isTapped,
            )
        }

        IconButton(
            onClick = { onBackClick() },
            modifier = Modifier
                .padding(start = 16.dp, top = 38.dp)
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.Black)
                .align(Alignment.TopStart)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.White
            )
        }

        PlacesAutocompleteTextField(
            modifier = Modifier.fillMaxWidth().padding(start = 60.dp, top = 12.dp),
            searchText = searchText,
            predictions = predictions.map { prediction ->
                AutocompletePlace(
                    placeId = prediction.placeId,
                    primaryText = prediction.getPrimaryText(null),
                    secondaryText = prediction.getSecondaryText(null)
                ) },
            onQueryChanged = { searchText = it },
            onSelected = { autocompletePlace: AutocompletePlace ->
                result = autocompletePlace
                predictions = emptyList()
                isTapped = true
                viewModel.getLocationByCityName(result?.primaryText.toString())
            },
            selectedPlace = result
        )
        if (isTapped) {
            Card(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
                    .fillMaxWidth(0.8f),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Save this location?",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            if(isMap){
                                viewModel.setHomeLocation(markerState.position)
                                viewModel.setLocationSource(LocationSource.MAP)
                            }
                            viewModel.saveLocation(selectedCityName, markerState.position)
                            isTapped = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "Save Location")
                    }
                }
            }
        }
    }

}