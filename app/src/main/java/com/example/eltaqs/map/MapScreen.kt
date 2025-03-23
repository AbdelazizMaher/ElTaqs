package com.example.eltaqs.map

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.eltaqs.BuildConfig
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
import com.google.android.libraries.places.compose.autocomplete.models.toPlaceDetails
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState


@Composable
fun MapScreen(){
    val context = LocalContext.current
    Places.initializeWithNewPlacesApiEnabled(context, BuildConfig.GOOGLE_MAP_API_KEY)
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

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(markerState.position, 10f)
    }

    LaunchedEffect(markerState.position) {
        cameraPositionState.position = CameraPosition.fromLatLngZoom(markerState.position, 10f)
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

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(mapType = MapType.HYBRID),
            onMapClick = {
                latLng -> markerState.position = latLng
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
        PlacesAutocompleteTextField(
            modifier = Modifier.fillMaxWidth().padding(20.dp),
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
            },
            selectedPlace = result
        )
    }

}