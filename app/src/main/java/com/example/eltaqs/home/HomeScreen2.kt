package com.example.eltaqs.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.eltaqs.R
import com.example.eltaqs.data.db.AppDataBase
import com.example.eltaqs.data.local.WeatherLocalDataSource
import com.example.eltaqs.data.model.ForecastResponse
import com.example.eltaqs.data.remote.WeatherRemoteDataSource
import com.example.eltaqs.repo.WeatherRepository
import com.example.eltaqs.ui.theme.ColorGradient1
import com.example.eltaqs.ui.theme.ColorGradient2
import com.example.eltaqs.ui.theme.ColorGradient3
import com.example.eltaqs.ui.theme.ColorTextSecondary
import com.example.eltaqs.ui.theme.ColorTextSecondaryVariant
import com.example.eltaqs.ui.theme.ColorWindForecast
import java.time.LocalDate
import java.time.format.DateTimeFormatter


@Composable
@Preview(showBackground = true)
fun PreviewHomeScreen2() {
    HomeScreen2(navigateToDetailsScreen = { _, _, _, _ -> })
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen2(navigateToDetailsScreen: (location: String, weatherList: List<WeatherItem>, selectedIndex: Int, onItemSelect: (Int) -> Unit)-> Unit) {
    val viewModel: HomeViewModel = viewModel(
        factory = HomeViewModelFactory(
            WeatherRepository.getInstance(
                WeatherRemoteDataSource(RetrofitHelper.apiService),
                WeatherLocalDataSource(AppDataBase.getInstance(LocalContext.current).getFavouritesDAO())
            )
        )
    )

    val currentWeatherState = viewModel.currentWeather.observeAsState()
    val forecastState = viewModel.forecast.observeAsState()

    viewModel.getCurrentWeather(24.34, 10.99, "metric", "en")
    viewModel.getForecast(44.34, 10.99, "metric", "en")

    Column(modifier = Modifier.padding(16.dp)) {

        currentWeatherState.value?.let { current ->
            Text(
                text = current.name,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = getCurrentDate(),
                color = Color.Gray,
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            DailyForecast(
                forecast = current.weather.firstOrNull()?.main ?: "N/A",
                date = getCurrentDate()
            )

            Spacer(modifier = Modifier.height(30.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp)
            ) {
                WeatherInfoCard("Wind Speed", "${current.wind.speed} km/h", R.drawable.windspeed)
                WeatherInfoCard("Humidity", "${current.main.humidity} %", R.drawable.humidity)
                WeatherInfoCard("Max Temp", "${current.main.tempMax} °C", R.drawable.sleet)
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Today",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Text(
                text = "Next 5 Days",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF4466E5),
                modifier = Modifier
                    .clickable {
                        val location = currentWeatherState.value?.name ?: "Unknown Location"

                        val weatherList = forecastState.value?.list?.take(7)?.map { forecastItem ->
                            WeatherItem(
                                date = forecastItem.dtTxt,
                                day = getDayName(forecastItem.dtTxt),
                                temp = forecastItem.main.temp.toInt(),
                                maxTemp = forecastItem.main.tempMax.toInt(),
                                humidity = forecastItem.main.humidity,
                                windSpeed = forecastItem.wind.speed.toInt(),
                                state = forecastItem.weather.firstOrNull()?.main ?: "",
                                iconRes = getImageResId(forecastItem.weather.firstOrNull()?.main ?: "")
                            )
                        } ?: emptyList()

                        val selectedIndex = 0

                        val onItemSelect: (Int) -> Unit = { index ->
                            println("Selected index = $index")
                        }

                        navigateToDetailsScreen(location, weatherList, selectedIndex, onItemSelect)
                    }
            )
        }


        forecastState.value?.let { forecast ->
            LazyRow {
                itemsIndexed(forecast.list.take(5)) { index, day ->
                    DailyWeatherCard(
                        data = day,
                        isSelected = index == 0
                    )
                }
            }
        }
    }
}



@Composable
fun WeatherInfoCard(title: String, value: String, iconRes: Int) {
    Column(
        modifier = Modifier.width(100.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = title,
            fontSize = 12.sp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFF2F4FA)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                modifier = Modifier.size(36.dp)
            )
        }

        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(top = 6.dp)
        )
    }
}




@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DailyWeatherCard(
    data: ForecastResponse.Item0,
    isSelected: Boolean = false
) {
    val formatter = DateTimeFormatter.ofPattern("EEE")
    val date = LocalDate.parse(data.dtTxt.substring(0, 10))
    val day = formatter.format(date)

    Card(
        modifier = Modifier
            .padding(6.dp)
            .width(90.dp)
            .height(120.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFFEEF0FF) else Color.White
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 10.dp),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "${data.main.temp.toInt()}°C",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Image(
                painter = painterResource(id = getImageResId(data.weather.firstOrNull()?.main ?: "")),
                contentDescription = null,
                modifier = Modifier
                    .size(35.dp)
            )
            Text(
                text = day,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Gray
            )
        }
    }
}




fun getImageResId(weatherType: String): Int {
    return when (weatherType.lowercase()) {
        "clear" -> R.drawable.clear
        "clouds" -> R.drawable.heavycloud
        "few clouds", "scattered clouds" -> R.drawable.lightcloud
        "rain", "shower rain" -> R.drawable.heavyrain
        "thunderstorm" -> R.drawable.thunderstorm
        "snow" -> R.drawable.snow
        "mist" -> R.drawable.heavycloud
        else -> R.drawable.clear
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun getCurrentDate(): String {
    return LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, MMM d"))
}

@RequiresApi(Build.VERSION_CODES.O)
fun  getDayName(dateString: String): String {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    val date = LocalDate.parse(dateString, formatter)
    return date.format(DateTimeFormatter.ofPattern("EEEE"))
}

@Composable
fun DailyForecast(
    modifier: Modifier = Modifier,
    forecast: String = "Rain showers",
    date: String = "Monday, 12 Feb"
) {
    ConstraintLayout(
        modifier = modifier.fillMaxWidth()
    ) {
        val (forecastImage, forecastValue, windImage, title, description, background) = createRefs()

        CardBackground(
            modifier = Modifier.constrainAs(background) {
                linkTo(
                    start = parent.start,
                    end = parent.end,
                    top = parent.top,
                    bottom = description.bottom,
                    topMargin = 24.dp
                )
                height = Dimension.fillToConstraints
            }
        )

        Image(
            painter = painterResource(R.drawable.heavyrain),
            contentDescription = null,
            contentScale = ContentScale.FillHeight,
            modifier = Modifier
                .height(175.dp)
                .constrainAs(forecastImage) {
                    start.linkTo(anchor = parent.start, margin = 4.dp)
                    top.linkTo(parent.top)
                }
        )

        Text(
            text = forecast,
            style = MaterialTheme.typography.titleLarge,
            color = ColorTextSecondary,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.constrainAs(title) {
                start.linkTo(anchor = parent.start, margin = 24.dp)
                top.linkTo(anchor = forecastImage.bottom)
            }
        )

        Text(
            text = date,
            style = MaterialTheme.typography.bodyMedium,
            color = ColorTextSecondaryVariant,
            modifier = Modifier
                .constrainAs(description) {
                    start.linkTo(anchor = title.start)
                    top.linkTo(anchor = title.bottom)
                }
                .padding(bottom = 24.dp)
        )

        ForecastValue(
            modifier = Modifier.constrainAs(forecastValue) {
                end.linkTo(anchor = parent.end, margin = 24.dp)
                top.linkTo(forecastImage.top)
                bottom.linkTo(forecastImage.bottom)
            }
        )

        WindForecastImage(
            modifier = Modifier.constrainAs(windImage) {
                linkTo(
                    top = title.top,
                    bottom = title.bottom
                )
                end.linkTo(anchor = parent.end, margin = 24.dp)
            }
        )
    }
}

@Composable
private fun CardBackground(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                brush = Brush.linearGradient(
                    0f to ColorGradient1,
                    0.5f to ColorGradient2,
                    1f to ColorGradient3
                ),
                shape = RoundedCornerShape(32.dp)
            )
    )
}

@Composable
private fun ForecastValue(
    modifier: Modifier = Modifier,
    degree: String = "21",
    description: String = "Feels like 26°"
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.Start
    ) {
        Box(
            contentAlignment = Alignment.TopEnd
        ) {
            Text(
                text = degree,
                letterSpacing = 0.sp,
                style = TextStyle(
                    brush = Brush.verticalGradient(
                        0f to Color.White,
                        1f to Color.White.copy(alpha = 0.3f)
                    ),
                    fontSize = 80.sp,
                    fontWeight = FontWeight.Black
                ),
                modifier = Modifier.padding(end = 16.dp)
            )
            Text(
                text = "°",
                style = TextStyle(
                    brush = Brush.verticalGradient(
                        0f to Color.White,
                        1f to Color.White.copy(alpha = 0.3f)
                    ),
                    fontSize = 70.sp,
                    fontWeight = FontWeight.Light,
                ),
                modifier = Modifier.padding(top = 2.dp)
            )
        }
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = ColorTextSecondaryVariant
        )
    }
}

@Composable
private fun WindForecastImage(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(R.drawable.hail),
            contentDescription = null,
            modifier = Modifier.size(60.dp),
            tint = ColorWindForecast
        )
        Icon(
            painter = painterResource(R.drawable.windspeed),
            contentDescription = null,
            modifier = Modifier.size(60.dp),
            tint = ColorWindForecast
        )
    }
}






