package com.example.eltaqs.details

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.eltaqs.R
import com.example.eltaqs.data.db.AppDataBase
import com.example.eltaqs.data.local.WeatherLocalDataSource
import com.example.eltaqs.data.model.ForecastResponse
import com.example.eltaqs.data.model.Response
import com.example.eltaqs.data.remote.WeatherRemoteDataSource
import com.example.eltaqs.repo.WeatherRepository
import java.time.LocalDate

@Composable
@Preview(showBackground = true)
fun PreviewWeatherDetailScreen() {
    WeatherDetailScreen(
        lat = 32.34,
        lon = 20.99,
        location = "Cairo",
        onBackClick = {}
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WeatherDetailScreen(
    lat: Double,
    lon: Double,
    units: String = "metric",
    lang: String = "en",
    location: String,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: DetailsViewModel = viewModel(
        factory = DetailsViewModelFactory(
            WeatherRepository.getInstance(
                WeatherRemoteDataSource(RetrofitHelper.apiService),
                WeatherLocalDataSource(AppDataBase.getInstance(context).getFavouritesDAO())
            )
        )
    )

    val uiState by viewModel.forecast.collectAsStateWithLifecycle()
    var selectedDay by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        viewModel.getForecast(lat, lon, units, lang)
    }

    Scaffold(
        topBar = { TopBarSection(location, onBackClick) },
        backgroundColor = Color(0xFFD6E0F5)
    ) { paddingValues ->

        when (val state = uiState) {
            is Response.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is Response.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = state.message, color = Color.Red)
                }
            }

            is Response.Success -> {
                val forecast = state.data
                val groupedByDay = forecast.list.groupBy { it.dtTxt.split(" ")[0] }
                val days = groupedByDay.keys.toList()
                if (selectedDay == null && days.isNotEmpty()) selectedDay = days[0]

                Column(modifier = Modifier.padding(paddingValues)) {
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp, start = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(days) { day ->
                            val item = groupedByDay[day]?.first()
                            DaySelectorItem(
                                day = day,
                                selectedDay = selectedDay,
                                icon = item?.weather?.firstOrNull()?.icon ?: "01d",
                                temp = item?.main?.temp?.toInt() ?: 0
                            ) {
                                selectedDay = day
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(100.dp))

                    groupedByDay[selectedDay]?.firstOrNull()?.let {
                        WeatherDetailsCard(it)
                    }

                    selectedDay?.let { dayKey ->
                        val hourlyList = groupedByDay[dayKey] ?: emptyList()

                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.White)
                                .padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(hourlyList) { forecastItem ->
                                HourlyForecastItem(forecastItem)
                            }
                        }
                    }
                }
            }
        }
    }
}



@Composable
fun TopBarSection(location: String, onBackClick: () -> Unit) {
    TopAppBar(
        backgroundColor = Color(0xFFD6E0F5),
        elevation = 0.dp,
        contentPadding = PaddingValues(horizontal = 8.dp)
    ) {
        IconButton(onClick = onBackClick) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Text(location, style = MaterialTheme.typography.h6)
        }
    }
}


@Composable
fun DaySelectorItem(
    day: String,
    selectedDay: String?,
    icon: String,
    temp: Int,
    onClick: () -> Unit
) {
    val displayDay = LocalDate.parse(day).dayOfWeek.toString().take(3)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(70.dp)
            .clip(RoundedCornerShape(60.dp))
            .background(
                if (day == selectedDay) Color(0xFF004D6D) else Color(0xFF0B698B)
            )
            .clickable { onClick() }
            .padding(vertical = 12.dp)
    ) {
        Text(text = displayDay, color = Color.White, fontSize = 14.sp)
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(Color(0xFF002B59)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(getWeatherIcon(icon)),
                contentDescription = null,
                modifier = Modifier.size(32.dp)
            )
        }
        Text(text = "$temp°C", color = Color.White, fontSize = 16.sp)
    }
}


@Composable
fun WeatherDetailsCard(today:  ForecastResponse.Item0) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(
                color = Color.White,
                shape = RoundedCornerShape(topStart = 50.dp, topEnd = 50.dp)
            )
    ) {
        Box(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .offset(y = (-60).dp)
                .fillMaxWidth()
                .height(300.dp)
                .background(
                    brush = Brush.linearGradient(
                        listOf(
                            Color(0xffa9c1f5),
                            Color(0xff6696f5)
                        )
                    ),
                    shape = RoundedCornerShape(15.dp)
                )
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                val icon = today.weather.firstOrNull()?.icon ?: "01d"
                val description = today.weather.firstOrNull()?.description.orEmpty()

                Image(
                    painter = painterResource(id = getWeatherIcon(icon)),
                    contentDescription = null,
                    modifier = Modifier
                        .size(100.dp)
                        .align(Alignment.CenterHorizontally)
                )

                Text(
                    text = description.replaceFirstChar { it.uppercase() },
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(16.dp))
                WeatherInfoRow(today)
                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "${today.main.temp.toInt()}°C",
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}


@Composable
fun WeatherInfoRow(item:  ForecastResponse.Item0) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        WeatherInfoCard("Humidity", "${item.main.humidity}%", R.drawable.humidity)
        WeatherInfoCard("Wind", "${item.wind.speed} km/h", R.drawable.windspeed)
        WeatherInfoCard("Max Temp", "${item.main.tempMax.toInt()}°C", R.drawable.maxtemp)
        WeatherInfoCard("Pressure", "${item.main.pressure} hPa", R.drawable.hail)
    }
}


@Composable
fun HourlyForecastItem(hourForecast:  ForecastResponse.Item0) {
    val time = hourForecast.dtTxt.split(" ")[1].substring(0, 5)
    val temp = hourForecast.main.temp.toInt()
    val icon = hourForecast.weather.firstOrNull()?.icon ?: "01d"
    val desc = hourForecast.weather.firstOrNull()?.description.orEmpty()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(10.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(time, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Image(
            painter = painterResource(getWeatherIcon(icon)),
            contentDescription = null,
            modifier = Modifier.size(32.dp)
        )
        Column(horizontalAlignment = Alignment.End) {
            Text("$temp°C", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Text(desc.replaceFirstChar { it.uppercase() }, fontSize = 12.sp)
        }
    }
}



@Composable
fun WeatherInfoCard(title: String, value: String, iconResId: Int) {
    Column(
        modifier = Modifier
            .width(100.dp)
            .padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = iconResId),
            contentDescription = null,
            modifier = Modifier.size(32.dp)
        )
        Text(text = title, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.Normal)
    }
}

fun getWeatherIcon(icon: String): Int {
    return when (icon) {
        "01d" -> R.drawable.clear
        "02d" -> R.drawable.lightcloud
        "03d", "04d" -> R.drawable.heavycloud
        "09d", "10d" -> R.drawable.heavyrain
        "11d" -> R.drawable.thunderstorm
        "13d" -> R.drawable.snow
        "50d" -> R.drawable.snow
        else -> R.drawable.clear
    }
}
