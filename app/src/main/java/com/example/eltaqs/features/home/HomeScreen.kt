package com.example.eltaqs.features.home

import android.annotation.SuppressLint
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
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.airbnb.lottie.compose.*
import com.example.eltaqs.R
import com.example.eltaqs.utils.NetworkConnectivity
import com.example.eltaqs.utils.getWeatherIcon
import com.example.eltaqs.utils.getWeatherIconForItems
import com.example.eltaqs.utils.settings.formatBasedOnLanguage
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
fun HomeScreen(onNavigateToDetails: (lat: Double, lon: Double, location: String)-> Unit) {
    val viewModel: HomeViewModel = viewModel(
        factory = HomeViewModelFactory(
            WeatherRepository.getInstance(
                WeatherRemoteDataSource(RetrofitHelper.apiService),
                WeatherLocalDataSource(AppDataBase.getInstance(LocalContext.current).getFavouritesDAO()),
                SharedPrefDataSource.getInstance(LocalContext.current)
            )
        )
    )

    val uiState = viewModel.weatherData.collectAsStateWithLifecycle()
    val locationState = viewModel.locationState.collectAsStateWithLifecycle()
    val windSpeedSymbol = viewModel.getWindSpeedUnitSymbol()
    val tempSymbol = viewModel.getTemperatureUnitSymbol()

    val isInternetAvailable by NetworkConnectivity.isInternetAvailable.collectAsState()

    LaunchedEffect(isInternetAvailable, locationState.value) {
        if (isInternetAvailable) {
            viewModel.getWeatherAndForecast()
        } else {
            viewModel.getWeatherAndForecastFromLocal()
        }
    }

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
                    text = stringResource(R.string.something_went_wrong, state.message),
                    color = Color.Red,
                    fontSize = 16.sp,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}


@SuppressLint("NewApi")
@Composable
fun CurrentWeatherSection(current: CurrentWeatherResponse, tempSymbol: String) {
    Text(
        text = current.name,
        color = Color.White,
        fontSize = 30.sp,
        fontWeight = FontWeight.Bold
    )

    Text(
        text = getCurrentDate().formatBasedOnLanguage(),
        color = Color.White,
        fontSize = 16.sp
    )

    Spacer(modifier = Modifier.height(20.dp))

    DailyForecast(
        forecast = current.weather.firstOrNull()?.description ?: stringResource(R.string.n_a),
        icon = current.weather.firstOrNull()?.icon ?: "",
        temp = current.main.temp.toInt(),
        feelsLike = current.main.feelsLike.toInt(),
        sunrise = current.sys.sunrise.toLong(),
        sunset = current.sys.sunset.toLong(),
        cloud = current.clouds.all.toString().formatBasedOnLanguage(),
        tempSymbol = tempSymbol.formatBasedOnLanguage()
    )

    Spacer(modifier = Modifier.height(30.dp))
}

@Composable
fun WeatherStatsRow(current: CurrentWeatherResponse, tempSymbol: String, windSpeedSymbol: String) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2d525a)
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ){
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.padding(end = 8.dp)
        ) {
            WeatherInfoCard(stringResource(R.string.wind_speed), "${current.wind.speed}" + windSpeedSymbol.formatBasedOnLanguage(), R.drawable.windspeed)
            WeatherInfoCard(stringResource(R.string.humidity), "${current.main.humidity} %", R.drawable.humidity)
            WeatherInfoCard(stringResource(R.string.max_temp), "${current.main.tempMax}" + tempSymbol.formatBasedOnLanguage(), R.drawable.sleet)
            WeatherInfoCard(stringResource(R.string.pressure),
                stringResource(R.string.hpa, current.main.pressure), R.drawable.hail)
        }
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
            color = Color.White
        )

        Text(
            text = stringResource(R.string.next_5_days),
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
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
        modifier = Modifier
            .width(90.dp)
            .height(120.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = title,
            fontSize = 12.sp,
            color = Color.White,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        Box(
            modifier = Modifier
                .size(32.dp)
                .height(32.dp)
                .clip(RoundedCornerShape(8.dp))
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
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
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
        shape = RoundedCornerShape(25.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF2d525a))
                .padding(vertical = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = data.main.temp.toInt().toString().formatBasedOnLanguage() + tempSymbol.formatBasedOnLanguage(),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFFFFFF)
                )
                Image(
                    painter = painterResource(id = getWeatherIconForItems(data.weather.firstOrNull()?.icon ?: "")),
                    contentDescription = null,
                    modifier = Modifier.size(35.dp)
                )
                Text(
                    text = hour.formatBasedOnLanguage(),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFFFFFFFF)
                )
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
fun getCurrentDate(): String {
    return LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, MMM d"))
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
    cloud: String,
    tempSymbol: String,
    icon: String
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

            val weatherAnimationRef = createRef()

            WeatherAnimation(
                forecast = icon,
                modifier = Modifier.constrainAs(weatherAnimationRef) {
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
                    top.linkTo(anchor = weatherAnimationRef.bottom)
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
                            start.linkTo(parent.start, margin = 24.dp)
                        } else {
                            end.linkTo(parent.end, margin = 24.dp)
                        }
                        top.linkTo(weatherAnimationRef.top)
                        bottom.linkTo(weatherAnimationRef.bottom)
                    },
                degree = temp.toString().formatBasedOnLanguage(),
                tempSymbol = tempSymbol.formatBasedOnLanguage()
            )

            SunriseSunsetRow(
                sunrise = sunrise,
                sunset = sunset,
                cloud = cloud,
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
fun WeatherAnimation(forecast: String, modifier: Modifier = Modifier) {
    val animationResId = getWeatherIcon(forecast)

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(animationResId))
    val progress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever
    )

    LottieAnimation(
        composition = composition,
        progress = progress,
        modifier = modifier
            .size(200.dp)
            .height(175.dp)
    )
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
    cloud: String,
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
                painter = painterResource(R.drawable.sunrise),
                contentDescription = stringResource(R.string.sunrise),
                modifier = Modifier.size(32.dp),
                tint = Color.Yellow
            )
            Text(text = sunriseTime.formatBasedOnLanguage(), fontSize = 12.sp, color = Color.White)
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                painter = painterResource(R.drawable.sunset),
                contentDescription = stringResource(R.string.sunset),
                modifier = Modifier.size(32.dp),
                tint = Color(0xFFFFA500)
            )
            Text(text = sunsetTime.formatBasedOnLanguage(), fontSize = 12.sp, color = Color.White)
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                painter = painterResource(R.drawable.heavycloud),
                contentDescription = "clouds",
                modifier = Modifier.size(32.dp),
                tint = Color.LightGray
            )
            Text(
                text = "${cloud}%",
                fontSize = 12.sp,
                color = Color.White
            )
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








