package com.example.eltaqs.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.eltaqs.R
import com.example.eltaqs.Utils.restartActivity
import com.example.eltaqs.Utils.settings.enums.Language
import com.example.eltaqs.Utils.settings.enums.LocationSource
import com.example.eltaqs.Utils.settings.enums.SpeedUnit
import com.example.eltaqs.Utils.settings.enums.TemperatureUnit
import com.example.eltaqs.data.local.AppDataBase
import com.example.eltaqs.data.local.WeatherLocalDataSource
import com.example.eltaqs.data.remote.WeatherRemoteDataSource
import com.example.eltaqs.data.sharedpreference.SharedPrefDataSource

import com.example.eltaqs.data.repo.WeatherRepository

@Composable
fun SettingsScreen(onNavigateToMap: (isMap: Boolean) -> Unit) {
    val viewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModelFactory(
            WeatherRepository.getInstance(
                WeatherRemoteDataSource(RetrofitHelper.apiService),
                WeatherLocalDataSource(AppDataBase.getInstance(LocalContext.current).getFavouritesDAO()),
                SharedPrefDataSource.getInstance(LocalContext.current)
            )
        )
    )

    val context = LocalContext.current
    val temperatureUnit by viewModel.temperatureUnit.collectAsStateWithLifecycle()
    val windSpeedUnit by viewModel.windSpeedUnit.collectAsStateWithLifecycle()
    val locationSource by viewModel.locationSource.collectAsStateWithLifecycle()
    val language by viewModel.language.collectAsStateWithLifecycle()

    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF1b3a41),
            Color(0xFF2d525a),
            Color(0xFF4a757e)
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGradient)
    ) {

        BackgroundWithOverlayAndText()

        Spacer(modifier = Modifier.height(20.dp))

        ToggleGroup(
            title = stringResource(R.string.language),
            options = Language.entries.map { it.getDisplayName(language) },
            selected = language.getDisplayName(language),
            onOptionSelected = { selectedName ->
                val selectedLanguage = Language.entries.find {
                    it.getDisplayName(language) == selectedName
                }
                selectedLanguage?.let {
                    viewModel.setLanguage(it)
                    restartActivity(context)
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        ToggleGroup(
            title = stringResource(R.string.wind_speed),
            options = SpeedUnit.entries.map { it.getDisplayName(language) },
            selected = windSpeedUnit.getDisplayName(language),
            onOptionSelected = { selectedName ->
                val selectedUnit = SpeedUnit.entries.find {
                    it.getDisplayName(language) == selectedName
                }
                selectedUnit?.let { viewModel.updateUnitsFromSelection(selectedSpeedUnit = it) }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        ToggleGroup(
            title = stringResource(R.string.temperature),
            options = TemperatureUnit.entries.map { it.getDisplayName(language) },
            selected = temperatureUnit.getDisplayName(language),
            onOptionSelected = { selectedValue ->
                val selectedUnit = TemperatureUnit.entries.find {
                    it.getDisplayName(language) == selectedValue
                }
                selectedUnit?.let { viewModel.updateUnitsFromSelection(selectedTempUnit = it) }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        ToggleGroup(
            title = stringResource(R.string.location),
            options = LocationSource.entries.map { it.getDisplayName(language) },
            selected = locationSource.getDisplayName(language),
            onOptionSelected = { selectedName ->
                val selectedSource = LocationSource.entries.find {
                    it.getDisplayName(language) == selectedName
                }
                selectedSource?.let {
                    when (it) {
                        LocationSource.GPS -> { viewModel.setLocationSource(it) }
                        LocationSource.MAP -> {
                            //viewModel.setLocationSource(it)
                            onNavigateToMap(true)
                        }
                    }
                }
            }
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
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 8.dp, horizontal = 16.dp)) {
        Text(
            text = title,
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFFF0F0F3)),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            options.forEach { option ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(24.dp))
                        .background(if (option == selected) Color(0xFF2d525a) else Color.Transparent)
                        .clickable { onOptionSelected(option) }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = option,
                        color = if (option == selected) Color.White else Color.Gray,
                        fontWeight = FontWeight.Medium,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
fun BackgroundWithOverlayAndText() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .paint(
                painter = painterResource(id = R.drawable.top_background),
                contentScale = ContentScale.Crop
            )
    ) {
        // Overlay Image
        Image(
            painter = painterResource(id = R.drawable.circle),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.Center)
                .size(150.dp)
        )

        // Text
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineLarge.copy(color = Color.White),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
        )
    }
}



