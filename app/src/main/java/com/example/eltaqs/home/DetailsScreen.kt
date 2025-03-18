package com.example.eltaqs.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Text
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.example.eltaqs.R
import kotlinx.serialization.Serializable

@Composable
@Preview
fun PreviewDetailsScreen() {
    val weatherList = listOf(
        WeatherItem("17/3/2025", "Sun", 25, 30, 60, 10, "Sunny", R.drawable.clear),
        WeatherItem("18/3/2025", "Mon", 28, 32, 55, 12, "Cloudy", R.drawable.lightcloud),
        WeatherItem("19/3/2025", "Tue", 27, 31, 58, 11, "Rainy", R.drawable.heavyrain),
        WeatherItem("20/3/2025", "Wed", 26, 30, 62, 13, "Thunderstorm", R.drawable.thunderstorm),
        WeatherItem("21/3/2025", "Thu", 29, 33, 57, 10, "Snowy", R.drawable.snow)
    )
    DetailsScreen("Egypt", weatherList, 0) { }
}

@Composable
fun DetailsScreen(
    location: String,
    weatherList: List<WeatherItem>,
    selectedIndex: Int,
    onItemSelect: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFABCFF2))
    ) {
        WeatherAppBar(location)
        WeatherForecastRow(weatherList, selectedIndex, onItemSelect)
        Spacer(modifier = Modifier.height(16.dp))
        MainWeatherCard(weatherList[selectedIndex])
        Spacer(modifier = Modifier.height(20.dp))
        FutureForecastList(weatherList)
    }
}


@Composable
fun MainWeatherCard(item: WeatherItem) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .padding(horizontal = 20.dp)
            .background(
                Brush.verticalGradient(listOf(Color(0xFFA9C1F5), Color(0xFF6696F5))),
                shape = RoundedCornerShape(15.dp)
            )
    ) {
        Image(
            painter = painterResource(id = item.iconRes),
            contentDescription = item.state,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 20.dp)
                .size(150.dp)
        )

        Text(
            text = item.state,
            color = Color.White,
            fontSize = 20.sp,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 30.dp, top = 120.dp)
        )

        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            WeatherDetailItem("Wind Speed", "${item.windSpeed} km/h", R.drawable.windspeed)
            WeatherDetailItem("Humidity", "${item.humidity}%", R.drawable.humidity)
            WeatherDetailItem("Max Temp", "${item.maxTemp}°C", R.drawable.maxtemp)
        }

        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(20.dp)
        ) {
            Text(
                text = "${item.temp}",
                style = TextStyle(
                    fontSize = 80.sp,
                    fontWeight = FontWeight.Bold,
                    brush = Brush.linearGradient(listOf(Color(0xFFABCFF2), Color(0xFF9AC6F3)))
                )
            )
            Text(
                text = "°",
                style = TextStyle(
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold,
                    brush = Brush.linearGradient(listOf(Color(0xFFABCFF2), Color(0xFF9AC6F3)))
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherAppBar(location: String) {
    TopAppBar(
        title = { Text(text = location) },
        Modifier.background(Color(0xFFABCFF2))
    )
}

@Composable
fun WeatherForecastRow(
    weatherList: List<WeatherItem>,
    selectedIndex: Int,
    onItemClick: (Int) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        itemsIndexed(weatherList) { index, item ->
            val backgroundColor = if (index == selectedIndex) Color.White else Color(0xFF9EBCF9)
            val textColor = if (index == selectedIndex) Color.Blue else Color.White

            Column(
                modifier = Modifier
                    .padding(end = 12.dp)
                    .background(backgroundColor, shape = RoundedCornerShape(10.dp))
                    .clickable { onItemClick(index) }
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("${item.temp}°C", color = textColor)
                Image(
                    painter = painterResource(id = item.iconRes),
                    contentDescription = item.state,
                    modifier = Modifier.size(40.dp)
                )
                Text(item.day, color = textColor)
            }
        }
    }
}

@Composable
fun WeatherDetailItem(title: String, value: String, iconRes: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = title,
            modifier = Modifier.size(40.dp)
        )
        Text(text = title, fontSize = 14.sp, color = Color.White)
        Text(text = value, fontSize = 14.sp, color = Color.White)
    }
}

@Composable
fun FutureForecastList(forecastList: List<WeatherItem>) {
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
    ) {
        items(forecastList) { item ->
            Card(
                elevation = CardDefaults.cardElevation(4.dp),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(item.date, fontWeight = FontWeight.Bold)
                        Text(item.state)
                    }
                    Image(
                        painter = painterResource(id = item.iconRes),
                        contentDescription = item.state,
                        modifier = Modifier.size(40.dp)
                    )
                    Text("${item.temp}°C")
                }
            }
        }
    }
}

@Serializable
data class WeatherItem(
    val date: String,
    val day: String,
    val temp: Int,
    val maxTemp: Int,
    val humidity: Int,
    val windSpeed: Int,
    val state: String,
    val iconRes: Int
)



