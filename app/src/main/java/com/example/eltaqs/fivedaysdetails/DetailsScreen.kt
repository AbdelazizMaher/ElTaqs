package com.example.eltaqs.fivedaysdetails

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.eltaqs.R
import com.example.eltaqs.utils.getWeatherIconForItems
import com.example.eltaqs.utils.settings.formatBasedOnLanguage
import com.example.eltaqs.data.local.AppDataBase
import com.example.eltaqs.data.local.WeatherLocalDataSource
import com.example.eltaqs.data.model.ForecastResponse
import com.example.eltaqs.data.model.Response
import com.example.eltaqs.data.remote.WeatherRemoteDataSource
import com.example.eltaqs.data.sharedpreference.SharedPrefDataSource
import com.example.eltaqs.data.repo.WeatherRepository
import com.example.eltaqs.home.WeatherAnimation
import com.example.eltaqs.ui.theme.ColorTextSecondary
import com.example.eltaqs.ui.theme.ColorTextSecondaryVariant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DetailsScreen(
    lat: Double,
    lon: Double,
    location: String,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: DetailsViewModel = viewModel(
        factory = DetailsViewModelFactory(
            WeatherRepository.getInstance(
                WeatherRemoteDataSource(RetrofitHelper.apiService),
                WeatherLocalDataSource(AppDataBase.getInstance(LocalContext.current).getFavouritesDAO()),
                SharedPrefDataSource.getInstance(LocalContext.current)
            )
        )
    )

    val uiState by viewModel.forecast.collectAsStateWithLifecycle()
    var selectedDay by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        viewModel.getForecast(lat, lon)
    }

    Scaffold(
        topBar = { TopBarSection(location, onBackClick) },
        backgroundColor = Color(0xFF4a757e)
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

                    Spacer(modifier = Modifier.height(60.dp))

                    Box (

                    ){
                        Box(
                            modifier = Modifier
                                .padding(top = 70.dp)
                                .clip(RoundedCornerShape(topStart = 50.dp, topEnd = 50.dp))
                                .background(Color(0xFFE5F2FF))
                                .fillMaxSize()
                        ) {

                        }
                        Column(

                        ) {
                            groupedByDay[selectedDay]?.firstOrNull()?.let {
                                WeatherDetailsCard1(it)
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            selectedDay?.let { dayKey ->
                                val hourlyList = groupedByDay[dayKey] ?: emptyList()

                                LazyColumn(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color.Transparent)
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
    }
}



@Composable
fun TopBarSection(location: String, onBackClick: () -> Unit) {
    TopAppBar(
        backgroundColor = Color(0xFF4a757e),
        elevation = 0.dp,
        contentPadding = PaddingValues(horizontal = 8.dp)
    ) {
        IconButton(onClick = onBackClick) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Text(location, style = MaterialTheme.typography.h6, color = Color.White)
        }
    }
}


@SuppressLint("NewApi")
@Composable
fun DaySelectorItem(
    day: String,
    selectedDay: String?,
    icon: String,
    temp: Int,
    onClick: () -> Unit
) {
    val displayDay = LocalDate.parse(day).dayOfWeek.toString().take(3)
    val isSelected = day == selectedDay

    Card(
        modifier = Modifier
            .width(90.dp)
            .height(140.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(25.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFF2d525a) else Color(0xFF1a3a44)
        ),
        elevation = CardDefaults.cardElevation(if (isSelected) 6.dp else 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "$temp°",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Image(
                    painter = painterResource(id = getWeatherIconForItems(icon)),
                    contentDescription = null,
                    modifier = Modifier.size(35.dp)
                )
                Text(
                    text = displayDay,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun WeatherDetailsCard1(today: ForecastResponse.Item0) {
    Box(
        modifier = Modifier
            .wrapContentHeight()
            .background(
                color = Color.Transparent,
                shape = RoundedCornerShape(topStart = 50.dp, topEnd = 50.dp)
            )
    ) {
        DailyForecast(
            modifier = Modifier.wrapContentHeight(),
            forecast = today.weather.firstOrNull()?.description.orEmpty(),
            icon = today.weather.firstOrNull()?.icon ?: "",
            temp = today.main.temp.toInt(),
            feelsLike = today.main.feelsLike.toInt(),
            date = today.dtTxt,
            tempSymbol = "°C",
            today = today
        )
    }

}

@Composable
fun DailyForecast(
    modifier: Modifier = Modifier,
    forecast: String,
    temp: Int,
    feelsLike: Int,
    date: String,
    tempSymbol: String,
    today: ForecastResponse.Item0,
    icon: String
) {
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl

    CompositionLocalProvider(LocalLayoutDirection provides if (isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr) {
        Column(
            modifier = modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ConstraintLayout(
                modifier = Modifier.fillMaxWidth()
            ) {
                val (forecastImage, forecastValue, weatherInfo, title, description, background) = createRefs()

                CardBackground(
                    modifier = Modifier
                        .constrainAs(background) {
                        linkTo(
                            start = parent.start,
                            end = parent.end,
                            top = parent.top,
                            bottom = weatherInfo.bottom,
                            topMargin = 24.dp
                        )
                        height = Dimension.fillToConstraints
                    }
                )

                WeatherAnimation(
                    forecast = icon,
                    modifier = Modifier.constrainAs(forecastImage) {
                        if (isRtl) {
                            end.linkTo(parent.end, margin = 4.dp)
                        } else {
                            start.linkTo(parent.start, margin = 4.dp)
                        }
                        top.linkTo(parent.top)
                    }
                )

                androidx.compose.material3.Text(
                    text = forecast,
                    style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
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
                        .padding(start = 16.dp)
                )

                androidx.compose.material3.Text(
                    text = stringResource(R.string.feels_like, feelsLike.toString().formatBasedOnLanguage() + tempSymbol),
                    style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
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
                        .padding(start = 16.dp)
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

                WeatherInfoRow(
                    today,
                    modifier = Modifier.constrainAs(weatherInfo) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        top.linkTo(description.bottom)
                    }
                        .padding(bottom = 16.dp)
                )
            }
        }
    }
}



@Composable
private fun CardBackground(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF1b3a41),
                        Color(0xFF2d525a),
                        Color(0xFF4a757e)
                    )
                ),
                shape = RoundedCornerShape(16.dp)
            )
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
            androidx.compose.material3.Text(
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
            androidx.compose.material3.Text(
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


@Composable
fun WeatherInfoRow(item:  ForecastResponse.Item0, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        WeatherInfoCard("Humidity", "${item.main.humidity}%", R.drawable.humidity)
        WeatherInfoCard("Wind", "${item.wind.speed} km/h", R.drawable.windspeed)
        WeatherInfoCard("Max Temp", "${item.main.tempMax.toInt()}°C", R.drawable.maxtemp)
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HourlyForecastItem(hourForecast: ForecastResponse.Item0) {
    val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    val outputFormatter = DateTimeFormatter.ofPattern("h:mm a")

    val dateTime = LocalDateTime.parse(hourForecast.dtTxt, inputFormatter)
    val time = outputFormatter.format(dateTime)

    val temp = hourForecast.main.temp.toInt()
    val icon = hourForecast.weather.firstOrNull()?.icon ?: "01d"
    val desc = hourForecast.weather.firstOrNull()?.description.orEmpty()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .background(Color.White, RoundedCornerShape(10.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.Start
        ) {
            Text("$temp°C", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Text(desc.replaceFirstChar { it.uppercase() }, fontSize = 12.sp)
        }

        Image(
            painter = painterResource(getWeatherIconForItems(icon)),
            contentDescription = null,
            modifier = Modifier.size(64.dp)
        )

        Text(
            time,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.End
        )
    }
}

@Composable
fun WeatherInfoCard(title: String, value: String, iconResId: Int) {
    Column(
        modifier = Modifier.width(100.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        androidx.compose.material3.Text(
            text = title,
            fontSize = 12.sp,
            color = Color.White,
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
                painter = painterResource(id = iconResId),
                contentDescription = null,
                modifier = Modifier.size(36.dp)
            )
        }

        androidx.compose.material3.Text(
            text = value.formatBasedOnLanguage(),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(top = 6.dp)
        )
    }
}

