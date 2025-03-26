package com.example.eltaqs.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.eltaqs.data.local.AppDataBase
import com.example.eltaqs.data.local.WeatherLocalDataSource
import com.example.eltaqs.data.remote.WeatherRemoteDataSource
import com.example.eltaqs.data.sharedpreference.SharedPrefDataSource

import com.example.eltaqs.repo.WeatherRepository

@Composable
@Preview(showBackground = true)
fun SettingsScreen() {
    val viewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModelFactory(
            WeatherRepository.getInstance(
                WeatherRemoteDataSource(RetrofitHelper.apiService),
                WeatherLocalDataSource(AppDataBase.getInstance(LocalContext.current).getFavouritesDAO()),
                SharedPrefDataSource.getInstance(LocalContext.current)
            )
        )
    )

    val temperatureUnit by viewModel.temperatureUnit.collectAsStateWithLifecycle()
    val windSpeedUnit by viewModel.windSpeedUnit.collectAsStateWithLifecycle()
    val locationSource by viewModel.locationSource.collectAsStateWithLifecycle()
    val language by viewModel.language.collectAsStateWithLifecycle()


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F7F7))
            .padding(16.dp)
    ) {
        Text("Settings", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(20.dp))

        ToggleGroup(
            title = "Temperature",
            options = listOf("°C", "°F"),
            selected = temperatureUnit,
            onOptionSelected = { viewModel.setTemperatureUnit(it) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        ToggleGroup(
            title = "Wind Speed",
            options = listOf("m/s", "km/h", "mph", "knots"),
            selected = windSpeedUnit,
            onOptionSelected = { viewModel.setWindSpeedUnit(it) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        ToggleGroup(
            title = "Pressure",
            options = listOf("Map", "Gps"),
            selected = locationSource,
            onOptionSelected = { viewModel.setLocationSource(it) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        ToggleGroup(
            title = "Theme",
            options = listOf("English", "Arabic"),
            selected = language,
            onOptionSelected = { viewModel.setLanguage(it) }
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun ToggleGroup(
    title: String,
    options: List<String>,
    selected: String,
    onOptionSelected: (String) -> Unit
) {
    Column {
        Text(title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            options.forEach { option ->
                Button(
                    onClick = { onOptionSelected(option) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (option == selected) Color.Black else Color.LightGray,
                        contentColor = if (option == selected) Color.White else Color.Black
                    ),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.padding(4.dp)
                ) {
                    Text(option)
                }
            }
        }
    }
}


