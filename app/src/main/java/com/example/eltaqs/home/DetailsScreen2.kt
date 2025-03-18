package com.example.eltaqs.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.eltaqs.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
@Preview(showBackground = true)
fun PreviewWeatherDetailScreen() {
    val sampleWeatherList = listOf(
        mapOf(
            "weather_state_name" to "Clear",
            "the_temp" to 25.3,
            "applicable_date" to "2025-03-17",
            "wind_speed" to 12.5,
            "humidity" to 60.0,
            "max_temp" to 28.0,
            "min_temp" to 18.0
        ),
        mapOf(
            "weather_state_name" to "Light Cloud",
            "the_temp" to 22.1,
            "applicable_date" to "2025-03-18",
            "wind_speed" to 10.0,
            "humidity" to 55.0,
            "max_temp" to 25.0,
            "min_temp" to 16.0
        ),
        mapOf(
            "weather_state_name" to "Rain",
            "the_temp" to 19.5,
            "applicable_date" to "2025-03-19",
            "wind_speed" to 20.0,
            "humidity" to 80.0,
            "max_temp" to 21.0,
            "min_temp" to 15.0
        )
    )

    WeatherDetailScreen(
        consolidatedWeatherList = sampleWeatherList,
        selectedId = 0,
        location = "Cairo",
        onBackClick = {}
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WeatherDetailScreen(
    consolidatedWeatherList: List<Map<String, Any>>,
    selectedId: Int,
    location: String,
    onBackClick: () -> Unit
) {
    val selectedIndex = remember { mutableStateOf(selectedId) }
    val weatherStateName = consolidatedWeatherList[selectedIndex.value]["weather_state_name"]?.toString() ?: ""
    val imageUrl = weatherStateName.replace(" ", "").lowercase()

    val gradientBrush = Brush.linearGradient(
        colors = listOf(Color(0xffABCFF2), Color(0xff9AC6F3))
    )

    Scaffold(
        topBar = {
            TopAppBar(
                backgroundColor = Color(0xFFD6E0F5),
                elevation = 0.dp,
                contentPadding = PaddingValues(horizontal = 8.dp)
            ) {
                // Back Button
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back"
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = location,
                        style = MaterialTheme.typography.h6,
                        maxLines = 1
                    )
                }
            }
        },
        backgroundColor = Color(0xFFD6E0F5)
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, start = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(consolidatedWeatherList.size) { index ->
                        val weather = consolidatedWeatherList[index]
                        val temp = (weather["the_temp"] as Double).toInt()
                        val dateStr = weather["applicable_date"].toString()
                        val parsedDate = LocalDate.parse(dateStr)
                        val day = parsedDate.dayOfWeek.toString().take(3)
                        val icon = weather["weather_state_name"].toString().replace(" ", "").lowercase()

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .width(80.dp)
                                .background(
                                    if (index == selectedIndex.value) Color.White else Color(0xff9ebcf9),
                                    RoundedCornerShape(10.dp)
                                )
                                .padding(vertical = 12.dp)
                                .shadow(4.dp)
                        ) {
                            Text(
                                "$temp°C",
                                fontSize = 17.sp,
                                color = if (index == selectedIndex.value) Color.Blue else Color.White
                            )
                            Image(
                                painter = painterResource(id = getWeatherIcon(icon)),
                                contentDescription = null,
                                modifier = Modifier.size(40.dp)
                            )
                            Text(
                                text = day,
                                fontSize = 17.sp,
                                color = if (index == selectedIndex.value) Color.Blue else Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(100.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .background(
                            Color.White,
                            shape = RoundedCornerShape(topStart = 50.dp, topEnd = 50.dp)
                        )
                ) {
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 20.dp)
                            .offset(y = (-60).dp)
                            .fillMaxWidth()
                            .height(300.dp)
                            .background(
                                brush = Brush.linearGradient(listOf(Color(0xffa9c1f5), Color(0xff6696f5))),
                                shape = RoundedCornerShape(15.dp)
                            )
                            .shadow(5.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                        ) {
                            WeatherCardLayout(
                                weatherStateName = weatherStateName,
                                date = "Today", // Replace with dynamic date logic if needed
                                imageUrl = imageUrl,
                                consolidatedWeatherList = consolidatedWeatherList,
                                selectedIndex = selectedIndex
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                verticalAlignment = Alignment.Top
                            ) {
                                Text(
                                    text = (consolidatedWeatherList[selectedIndex.value]["the_temp"] as Double).toInt().toString(),
                                    fontSize = 80.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "°",
                                    fontSize = 40.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 260.dp, start = 20.dp, end = 20.dp)
                    ) {
                        items(consolidatedWeatherList.size) { index ->
                            val weather = consolidatedWeatherList[index]
                            val date = LocalDate.parse(weather["applicable_date"].toString())
                            val dateFormatted = date.format(DateTimeFormatter.ofPattern("d MMMM, EEEE"))

                            val max = (weather["max_temp"] as Double).toInt()
                            val min = (weather["min_temp"] as Double).toInt()
                            val icon = weather["weather_state_name"].toString().replace(" ", "").lowercase()

                            Card(
                                modifier = Modifier
                                    .padding(vertical = 8.dp)
                                    .fillMaxWidth(),
                                elevation = 4.dp,
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .padding(12.dp)
                                        .fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = dateFormatted, color = Color(0xff6696f5))

                                    Row {
                                        Text("$max", fontSize = 24.sp, fontWeight = FontWeight.SemiBold, color = Color.Gray)
                                        Text("/", fontSize = 24.sp, color = Color.Gray)
                                        Text("$min", fontSize = 20.sp, color = Color.Gray)
                                    }

                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Image(
                                            painter = painterResource(id = getWeatherIcon(icon)),
                                            contentDescription = null,
                                            modifier = Modifier.size(30.dp)
                                        )
                                        Text(text = weather["weather_state_name"].toString(), fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WeatherInfoItem(text: String, value: Int, unit: String, imageRes: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Image(painter = painterResource(id = imageRes), contentDescription = null, modifier = Modifier.size(30.dp))
        Text(text = "$value $unit", color = Color.White, fontSize = 14.sp)
        Text(text = text, color = Color.White, fontSize = 14.sp)
    }
}

// Replace these with actual drawable resource mappings
fun getWeatherIcon(name: String): Int {
    return when (name) {
        "clear" -> R.drawable.clear
        "lightcloud" -> R.drawable.lightcloud
        "heavycloud" -> R.drawable.heavycloud
        "showers" -> R.drawable.showers
        "rain" -> R.drawable.heavyrain
        "snow" -> R.drawable.snow
        "thunder" -> R.drawable.thunderstorm
        else -> R.drawable.clear
    }
}

@Composable
fun WeatherCardLayout(
    weatherStateName: String,
    date: String,
    consolidatedWeatherList: List<Map<String, Any>>,
    selectedIndex: MutableState<Int>
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .offset(y = (-60).dp)
    ) {
        DailyForecast(
            forecast = weatherStateName,
            date = date,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            WeatherInfoCard(
                title = "Wind Speed",
                value = "${(consolidatedWeatherList[selectedIndex.value]["wind_speed"] as Double).toInt()} km/h",
                iconRes = R.drawable.windspeed
            )
            WeatherInfoCard(
                title = "Humidity",
                value = "${(consolidatedWeatherList[selectedIndex.value]["humidity"] as Double).toInt()} %",
                iconRes = R.drawable.humidity
            )
            WeatherInfoCard(
                title = "Max Temp",
                value = "${(consolidatedWeatherList[selectedIndex.value]["max_temp"] as Double).toInt()}°C",
                iconRes = R.drawable.maxtemp
            )
        }
    }
}
