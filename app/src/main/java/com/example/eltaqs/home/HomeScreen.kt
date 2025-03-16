package com.example.eltaqs.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.eltaqs.R
import com.example.eltaqs.data.db.AppDataBase
import com.example.eltaqs.data.local.WeatherLocalDataSource
import com.example.eltaqs.data.model.CurrentWeatherResponse
import com.example.eltaqs.data.model.ForecastResponse
import com.example.eltaqs.data.remote.WeatherRemoteDataSource
import com.example.eltaqs.repo.WeatherRepository


@Composable
fun HomeScreen() {
    val viewModel : HomeViewModel = viewModel(factory = HomeViewModelFactory(WeatherRepository.getInstance(
        WeatherRemoteDataSource(RetrofitHelper.apiService), WeatherLocalDataSource(AppDataBase.getInstance(LocalContext.current).getFavouritesDAO())
    )))

    val currentWeatherState = viewModel.currentWeather.observeAsState()
    val forecastState = viewModel.forecast.observeAsState()

    viewModel.getCurrentWeather(44.34,10.99, "metric", "en")
    viewModel.getForecast(44.34,10.99, "metric", "en")


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0E3245))
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            item {
                Text(
                    text = stringResource(id = R.string.city),
                    fontSize = 23.sp,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                Text(
                    text = stringResource(id = R.string.dayformat),
                    fontSize = 20.sp,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item { WeatherCard(currentWeatherState.value) }

            item { TodayForecast(forecastState.value?.list ?:  emptyList<ForecastResponse.Item0>()) }

            item { FiveDayForecast(forecastState.value?.list ?:  emptyList<ForecastResponse.Item0>()) }
        }
    }
}


@Composable
fun WeatherCard(currentWeather : CurrentWeatherResponse?) {
    val composition by rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(R.raw.sunnyrain)
    )
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.2f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LottieAnimation(
                modifier = Modifier.size(180.dp),
                composition = composition,
                progress = { progress }
            )

            Text(
                text = currentWeather?.weather?.get(0)?.description ?: stringResource(id = R.string.weatherstatus),
                fontSize = 20.sp,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Text(
                text = currentWeather?.main?.temp.toString(),
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Text(
                text = "H:27 L:19",
                fontSize = 16.sp,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            WeatherDetailsRow()
        }
    }
}

@Composable
fun WeatherDetailsRow() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        WeatherDetailItem(R.drawable.cloudsicon, stringResource(id = R.string.clouds), "20%")
        WeatherDetailItem(R.drawable.windicon, stringResource(id = R.string.winds), "12 km/h")
        WeatherDetailItem(R.drawable.humiditypercent, stringResource(id = R.string.humidity), "50%")
        WeatherDetailItem(R.drawable.pressureicon, stringResource(id = R.string.pressure), "997 hPa")
    }
}

@Composable
fun WeatherDetailItem(icon: Int, label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Image(
            painter = painterResource(id = icon),
            contentDescription = null,
            modifier = Modifier.size(50.dp)
        )
        Text(text = label, color = Color.White, fontSize = 16.sp)
        Text(text = value, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun TodayForecast(param: Any) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(id = R.string.today),
            color = Color.Yellow,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )

        LazyRow {
            items(5) { index ->
                ForecastItem()
            }
        }
    }
}

@Composable
fun FiveDayForecast(param: Any) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(id = R.string.next5days),
            color = Color.Yellow,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )

        repeat(5) {
            ForecastItem()
        }
    }
}


@Composable
fun ForecastItem() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.2f)
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.cloudsicon), // Replace with your drawable
                contentDescription = null,
                modifier = Modifier.size(50.dp)
            )

            Column(modifier = Modifier.padding(start = 16.dp)) {
                Text(text = "Monday", color = Color.White, fontSize = 18.sp)
                Text(text = "25°C / 18°C", color = Color.White, fontSize = 16.sp)
            }
        }
    }
}




