package com.example.eltaqs.home

import android.annotation.SuppressLint
import android.location.Location
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.eltaqs.R
import com.example.eltaqs.Utils.NetworkConnectivity
import com.example.eltaqs.Utils.settings.formatBasedOnLanguage
import com.example.eltaqs.data.local.AppDataBase
import com.example.eltaqs.data.local.WeatherLocalDataSource
import com.example.eltaqs.data.model.CurrentWeatherResponse
import com.example.eltaqs.data.model.ForecastResponse
import com.example.eltaqs.data.model.Response
import com.example.eltaqs.data.remote.WeatherRemoteDataSource
import com.example.eltaqs.data.sharedpreference.SharedPrefDataSource
import com.example.eltaqs.data.repo.WeatherRepository
import com.example.eltaqs.ui.theme.ColorTextSecondary
import com.example.eltaqs.ui.theme.ColorTextSecondaryVariant
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(location: Location, onNavigateToDetails: (lat: Double, lon: Double, location: String)-> Unit) {
    val viewModel: HomeViewModel = viewModel(
        factory = HomeViewModelFactory(
            WeatherRepository.getInstance(
                WeatherRemoteDataSource(RetrofitHelper.apiService),
                WeatherLocalDataSource(AppDataBase.getInstance(LocalContext.current).getFavouritesDAO()),
                SharedPrefDataSource.getInstance(LocalContext.current)
            )
        )
    )

    val context = LocalContext.current

    val uiState = viewModel.weatherData.collectAsStateWithLifecycle()
    val locationState = viewModel.locationState.collectAsStateWithLifecycle()
    val windSpeedSymbol = viewModel.getWindSpeedUnitSymbol()
    val tempSymbol = viewModel.getTemperatureUnitSymbol()


    val isInternetAvailable by NetworkConnectivity.isInternetAvailable.collectAsState()
    //if (locationState.value.lat != 0.0 && locationState.value.lng != 0.0) {

    LaunchedEffect(isInternetAvailable, locationState.value) {
        if (isInternetAvailable) {
            Log.d("TAG", "HomeScreen: ${locationState.value.lng}, ${locationState.value.lat}")
            viewModel.getWeatherAndForecast(locationState.value.lat, locationState.value.lng)
        } else {
            viewModel.getWeatherAndForecastFromLocal(locationState.value.lat, locationState.value.lng)
        }
    }
    //}
    Column(modifier = Modifier.padding(16.dp)) {
        when (val state = uiState.value) {
            is Response.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentSize()
                ) {
                    CircularProgressIndicator()
                }
            }

            is Response.Success -> {
                val current = state.data.first
                val forecast = state.data.second

                CurrentWeatherSection(current, tempSymbol)
                WeatherStatsRow(current, tempSymbol, windSpeedSymbol)
                TodayForecastRow(locationState.value.lat, locationState.value.lng,current.name, onNavigateToDetails)
                HourlyForecastRow(forecast, tempSymbol)
            }

            is Response.Error -> {
                Text(
                    text = "Something went wrong: ${state.message}",
                    color = Color.Red,
                    fontSize = 16.sp,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}


@Composable
fun CurrentWeatherSection(current: CurrentWeatherResponse, tempSymbol: String) {
    Text(
        text = current.name,
        fontSize = 30.sp,
        fontWeight = FontWeight.Bold
    )

    Text(
        text = getCurrentDate().formatBasedOnLanguage(),
        color = Color.Gray,
        fontSize = 16.sp
    )

    Spacer(modifier = Modifier.height(20.dp))

    DailyForecast(
        forecast = current.weather.firstOrNull()?.description ?: stringResource(R.string.n_a),
        temp = current.main.temp.toInt(),
        feelsLike = current.main.feelsLike.toInt(),
        sunrise = current.sys.sunrise.toLong(),
        sunset = current.sys.sunset.toLong(),
        date = getCurrentDate().formatBasedOnLanguage(),
        tempSymbol = tempSymbol.formatBasedOnLanguage()
    )

    Spacer(modifier = Modifier.height(30.dp))
}

@Composable
fun WeatherStatsRow(current: CurrentWeatherResponse, tempSymbol: String, windSpeedSymbol: String) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.padding(end = 8.dp)
    ) {
        WeatherInfoCard(stringResource(R.string.wind_speed), "${current.wind.speed}" + windSpeedSymbol.formatBasedOnLanguage(), R.drawable.windspeed)
        WeatherInfoCard(stringResource(R.string.humidity), "${current.main.humidity} %", R.drawable.humidity)
        WeatherInfoCard(stringResource(R.string.max_temp), "${current.main.tempMax}" + tempSymbol.formatBasedOnLanguage(), R.drawable.sleet)
        WeatherInfoCard(stringResource(R.string.pressure), "${current.main.pressure} hPa", R.drawable.hail)
    }
}

@Composable
fun TodayForecastRow(lat: Double, lon: Double, location: String, onNavigateToDetails: (lat: Double, lon: Double, location: String)-> Unit) {
    Spacer(modifier = Modifier.height(30.dp))

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.today),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Text(
            text = stringResource(R.string.next_5_days),
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF4466E5),
            modifier = Modifier.clickable {
                onNavigateToDetails(lat, lon, location)
            }
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HourlyForecastRow(forecast: ForecastResponse, tempSymbol: String) {
    val today = LocalDate.now()

    val todayHourlyList = forecast.list.filter { item ->
        val forecastDate = LocalDate.parse(item.dtTxt.substring(0, 10))
        forecastDate == today
    }

    LazyRow {
        itemsIndexed(todayHourlyList) { index, hour ->
            DailyWeatherCard(
                data = hour,
                isSelected = index == 0,
                tempSymbol = tempSymbol.formatBasedOnLanguage()
            )
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
            text = value.formatBasedOnLanguage(),
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
    isSelected: Boolean = false,
    tempSymbol: String
) {
    val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    val outputFormatter = DateTimeFormatter.ofPattern("h:mm a")

    val dateTime = LocalDateTime.parse(data.dtTxt, inputFormatter)
    val hour = outputFormatter.format(dateTime)

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
                text = data.main.temp.toInt().toString().formatBasedOnLanguage() + tempSymbol.formatBasedOnLanguage(),
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
                text = hour.formatBasedOnLanguage(),
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
fun getDayName(dateString: String): String {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    val date = LocalDate.parse(dateString, formatter)
    return date.format(DateTimeFormatter.ofPattern("EEEE"))
}

@SuppressLint("NewApi")
@Composable
fun DailyForecast(
    modifier: Modifier = Modifier,
    forecast: String,
    temp: Int,
    feelsLike: Int,
    sunset: Long,
    sunrise: Long,
    date: String,
    tempSymbol: String
) {
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl

    CompositionLocalProvider(LocalLayoutDirection provides if (isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr) {
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
                painter = painterResource(id = getImageResId(forecast)),
                contentDescription = null,
                contentScale = ContentScale.FillHeight,
                modifier = Modifier
                    .height(175.dp)
                    .constrainAs(forecastImage) {
                        if (isRtl) {
                            end.linkTo(parent.end, margin = 4.dp)
                        } else {
                            start.linkTo(parent.start, margin = 4.dp)
                        }
                        top.linkTo(parent.top)
                    }
            )

            Text(
                text = forecast,
                style = MaterialTheme.typography.titleLarge,
                color = ColorTextSecondary,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.constrainAs(title) {
                    if (isRtl) {
                        end.linkTo(parent.end, margin = 24.dp)
                    } else {
                        start.linkTo(parent.start, margin = 24.dp)
                    }
                    top.linkTo(anchor = forecastImage.bottom)
                }
            )

            Text(
                text = stringResource(R.string.feels_like , feelsLike.toString().formatBasedOnLanguage() +tempSymbol),
                style = MaterialTheme.typography.bodyMedium,
                color = ColorTextSecondaryVariant,
                modifier = Modifier
                    .constrainAs(description) {
                        if (isRtl) {
                            end.linkTo(title.end)
                        } else {
                            start.linkTo(title.start)
                        }
                        top.linkTo(anchor = title.bottom)
                    }
                    .padding(bottom = 24.dp)
            )

            ForecastValue(
                modifier = Modifier
                    .constrainAs(forecastValue) {
                        if (isRtl) {
                            start.linkTo(parent.start, margin = 36.dp)
                        } else {
                            end.linkTo(parent.end, margin = 36.dp)
                        }
                        top.linkTo(forecastImage.top)
                        bottom.linkTo(forecastImage.bottom)
                    },
                degree = temp.toString().formatBasedOnLanguage(),
                tempSymbol = tempSymbol.formatBasedOnLanguage()
            )

            SunriseSunsetRow(
                sunrise = sunrise,
                sunset = sunset,
                modifier = Modifier
                    .constrainAs(windImage) {
                        linkTo(top = title.top, bottom = title.bottom)
                        if (isRtl) {
                            start.linkTo(parent.start, margin = 24.dp)
                        } else {
                            end.linkTo(parent.end, margin = 24.dp)
                        }
                    }
                    .padding(bottom = 24.dp)
            )
        }
    }
}


@Composable
private fun CardBackground(
    modifier: Modifier = Modifier
) {
    Image(
        painter = painterResource(R.drawable.custom_card_background),
        contentDescription = null,
        contentScale = ContentScale.Fit,
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(32.dp))
            .graphicsLayer {
                rotationY = 180f
            }
    )
}

@Composable
private fun ForecastValue(
    modifier: Modifier = Modifier,
    degree: String,
    tempSymbol: String
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.Start
    ) {
        Box(
            contentAlignment = Alignment.TopEnd
        ) {
            Text(
                text = degree.formatBasedOnLanguage(),
                letterSpacing = 0.sp,
                style = TextStyle(
                    brush = Brush.verticalGradient(
                        0f to Color.White,
                        1f to Color.White.copy(alpha = 0.3f)
                    ),
                    fontSize = 80.sp,
                    fontWeight = FontWeight.Black
                ),
                modifier = Modifier.padding(end = 48.dp)
            )
            Text(
                text = tempSymbol,
                style = TextStyle(
                    brush = Brush.verticalGradient(
                        0f to Color.White,
                        1f to Color.White.copy(alpha = 0.3f)
                    ),
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Light,
                ),
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SunriseSunsetRow(
    sunrise: Long,
    sunset: Long,
    modifier: Modifier = Modifier
) {
    val sunriseTime = remember(sunrise) {
        formatUnixTimeToHourAMPM(sunrise)
    }
    val sunsetTime = remember(sunset) {
        formatUnixTimeToHourAMPM(sunset)
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                painter = painterResource(R.drawable.windicon),
                contentDescription = stringResource(R.string.sunrise),
                modifier = Modifier.size(40.dp),
                tint = Color.Yellow
            )
            Text(text = sunriseTime.formatBasedOnLanguage(), fontSize = 14.sp, color = Color.Black)
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                painter = painterResource(R.drawable.cloudsicon),
                contentDescription = stringResource(R.string.sunset),
                modifier = Modifier.size(40.dp),
                tint = Color(0xFFFFA500)
            )
            Text(text = sunsetTime.formatBasedOnLanguage(), fontSize = 14.sp, color = Color.Black)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun formatUnixTimeToHourAMPM(timestamp: Long): String {
    val dateTime = Instant.ofEpochSecond(timestamp)
        .atZone(ZoneId.systemDefault())
        .toLocalTime()
    val formatter = DateTimeFormatter.ofPattern("h:mm a")
    return dateTime.format(formatter)
}








