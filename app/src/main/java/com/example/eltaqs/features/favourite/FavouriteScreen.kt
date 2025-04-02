package com.example.eltaqs.features.favourite

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.eltaqs.R
import com.example.eltaqs.data.local.AppDataBase
import com.example.eltaqs.data.local.WeatherLocalDataSource
import com.example.eltaqs.data.model.FavouriteLocation
import com.example.eltaqs.data.model.Response
import com.example.eltaqs.data.remote.WeatherRemoteDataSource
import com.example.eltaqs.data.sharedpreference.SharedPrefDataSource
import com.example.eltaqs.data.repo.WeatherRepository
import com.example.eltaqs.features.home.WeatherAnimation
import com.example.eltaqs.ui.theme.ColorTextSecondary
import com.example.eltaqs.utils.translateWeatherCondition
import com.google.gson.Gson
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone


@Composable
fun FavouriteScreen(onNavigateToFavDetails: (String) -> Unit) {
    val viewModel: FavouriteViewModel = viewModel(
        factory = FavouriteViewModelFactory(
            WeatherRepository.getInstance(
                WeatherRemoteDataSource(RetrofitHelper.apiService),
                WeatherLocalDataSource(AppDataBase.getInstance(LocalContext.current).getFavouritesDAO()),
                    SharedPrefDataSource.getInstance(LocalContext.current)
            )
        )
    )

    val uiState = viewModel.favourites.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val favouriteList = remember { mutableStateListOf<FavouriteLocation>() }

    val tempSymbol = viewModel.getTemperatureUnitSymbol()

    LaunchedEffect(uiState.value) {
        if (uiState.value is Response.Success) {
            Log.d("TAG", "FavouriteScreen: ${uiState.value}")
            favouriteList.clear()
            favouriteList.addAll((uiState.value as Response.Success).data)
        }
    }

    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF1b3a41),
            Color(0xFF2d525a),
            Color(0xFF4a757e)
        )
    )

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState, modifier = Modifier.padding(bottom = 140.dp)) },
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->

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
                LazyColumn(
                    modifier = Modifier
                        .background(brush = backgroundGradient)
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    item {
                        Row(
                            modifier = Modifier.padding(top = 16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = Color.White
                            )
                            Text(
                                text = stringResource(R.string.favourite_locations),
                                fontSize = 24.sp
                            )
                            Spacer(modifier = Modifier.height(60.dp))
                        }
                    }

                    items(
                        items = favouriteList,
                        key = { it.locationName }
                    ) { favouriteLocation ->
                        val currentWeather = favouriteLocation.currentWeather
                        val cityName = favouriteLocation.locationName
                        val temperature = currentWeather.main.temp
                        val lastUpdated = SimpleDateFormat("HH:mm", Locale.getDefault()).apply {
                            timeZone = TimeZone.getDefault()
                        }.format(Date(currentWeather.dt * 1000L))

                        SwipeToDeleteContainer(
                            item = favouriteLocation,
                            onDelete = { viewModel.removeFromFavourite(it) },
                            onRestore = { viewModel.addToFavourite(it) },
                            snackBarHostState = snackbarHostState
                        ) { _ ->
                            FavouriteItem(
                                favLoc = favouriteLocation,
                                forecast = favouriteLocation.currentWeather.weather.firstOrNull()?.main ?: "",
                                icon = favouriteLocation.currentWeather.weather.firstOrNull()?.icon ?: "",
                                cityName = cityName,
                                temp = temperature.toInt(),
                                lastUpdated = lastUpdated,
                                tempSymbol = tempSymbol,
                                onNavigateToFavDetails = onNavigateToFavDetails
                            )
                        }
                    }
                    item {
                        Spacer(Modifier.height(180.dp))
                    }
                }
            }

            is Response.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Error: ${(state as Response.Error).message}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Red
                    )
                }
            }
        }

        LaunchedEffect(Unit) {
            viewModel.getFavourites()
        }
    }
}

@Composable
fun FavouriteItem(
    favLoc: FavouriteLocation,
    modifier: Modifier = Modifier,
    forecast: String,
    cityName: String,
    temp: Int,
    lastUpdated: String,
    tempSymbol: String,
    onNavigateToFavDetails: (String) -> Unit,
    icon: String
) {

    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    CompositionLocalProvider(LocalLayoutDirection provides if (isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr) {
        ConstraintLayout(
            modifier = modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp)
                .clickable { onNavigateToFavDetails(Gson().toJson(favLoc)) }
        ) {
            val (cityTitle, forecastImage, forecastValue, title, lastUpdatedText, background) = createRefs()

            CardBackground(
                modifier = Modifier.constrainAs(background) {
                    linkTo(
                        start = parent.start,
                        end = parent.end,
                        top = parent.top,
                        bottom = lastUpdatedText.bottom,
                        topMargin = 24.dp
                    )
                    height = Dimension.fillToConstraints
                }
            )

            Text(
                text = cityName,
                style = MaterialTheme.typography.headlineSmall,
                color = ColorTextSecondary,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                modifier = Modifier
                    .padding(top = 2.dp, start = 2.dp)
                    .constrainAs(forecastValue) {
                        if (isRtl) {
                            start.linkTo(parent.start, margin = 24.dp)
                        } else {
                            end.linkTo(parent.end, margin = 24.dp)
                        }
                        top.linkTo(forecastImage.top)
                        bottom.linkTo(forecastImage.bottom)
                    }
            )

            WeatherAnimation(
                forecast = icon,
                modifier = Modifier.constrainAs(forecastImage) {
                    if (isRtl) {
                        end.linkTo(parent.end, margin = 24.dp)
                    } else {
                        start.linkTo(parent.start, margin = 24.dp)
                    }
                    top.linkTo(parent.top)
                }
            )

            Text(
                text = forecast.toLowerCase(Locale.ROOT).translateWeatherCondition(),
                style = MaterialTheme.typography.titleLarge,
                color = ColorTextSecondary,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .constrainAs(title) {
                        if (isRtl) {
                            end.linkTo(parent.end, margin = 32.dp)
                        } else {
                            start.linkTo(parent.start, margin = 32.dp)
                        }
                        top.linkTo(forecastImage.bottom)
                    }
                    .padding(horizontal = 32.dp)
            )

            Text(
                text = stringResource(R.string.last_updated, lastUpdated),
                style = MaterialTheme.typography.bodySmall,
                color = Color.White,
                fontWeight = FontWeight.Light,
                fontSize = 18.sp,
                modifier = Modifier
                    .constrainAs(lastUpdatedText) {
                        if (isRtl) {
                            end.linkTo(title.end)
                        } else {
                            start.linkTo(title.start)
                        }
                        top.linkTo(title.bottom)
                    }
                    .padding(horizontal = 12.dp, vertical = 12.dp)
            )

        }
    }
}

@Composable
private fun ForecastValue(
    modifier: Modifier = Modifier,
    degree: String,
    tempSymbol: String,
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

@Composable
fun <T> SwipeToDeleteContainer(
    item: T,
    onDelete: (T) -> Unit,
    onRestore: (T) -> Unit,
    snackBarHostState: SnackbarHostState,
    animationDuration: Int = 500,
    content: @Composable (T) -> Unit
) {
    val context = LocalContext.current
    var isRemoved by remember { mutableStateOf(false) }
    val currentItem by rememberUpdatedState(item)

    val state = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) {
                isRemoved = true
                true
            } else {
                false
            }
        }
    )

    LaunchedEffect(isRemoved, currentItem) {
        if (isRemoved) {
            snackBarHostState.currentSnackbarData?.dismiss()
            val result = snackBarHostState.showSnackbar(
                message = context.getString(R.string.item_deleted),
                actionLabel = context.getString(R.string.undo),
                duration = SnackbarDuration.Short
            )

            if (result == SnackbarResult.ActionPerformed) {
                onRestore(currentItem)
                isRemoved = false

                state.snapTo(SwipeToDismissBoxValue.Settled)
            } else {
                delay(animationDuration.toLong())
                onDelete(currentItem)
            }
        }
    }

    AnimatedVisibility(
        visible = !isRemoved,
        enter = expandVertically(
            animationSpec = tween(durationMillis = animationDuration),
            expandFrom = Alignment.Top
        ) + fadeIn(),
        exit = shrinkVertically(
            animationSpec = tween(durationMillis = animationDuration),
            shrinkTowards = Alignment.Top
        ) + fadeOut()
    ) {
        SwipeToDismissBox(
            state = rememberSwipeToDismissBoxState(
                confirmValueChange = { value ->
                    if (value == SwipeToDismissBoxValue.EndToStart) {
                        isRemoved = true
                        true
                    } else {
                        false
                    }
                }
            ),
            backgroundContent = { DeleteBackground(swipeDismissState = state) },
            enableDismissFromStartToEnd = false
        ) {
            content(currentItem)
        }
    }
}
@Composable
fun DeleteBackground(swipeDismissState: SwipeToDismissBoxState) {
    val color = if (swipeDismissState.dismissDirection == SwipeToDismissBoxValue.EndToStart) {
        Color.Red
    } else {
        Color.Transparent
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color)
            .padding(horizontal = 64.dp, vertical = 64.dp),
        contentAlignment = Alignment.CenterEnd
    ) {
        Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.padding(top = 32.dp)
        )
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

