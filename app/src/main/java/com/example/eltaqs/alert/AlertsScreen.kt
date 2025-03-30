package com.example.eltaqs.alert

import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.eltaqs.alert.manager.AlarmScheduler
import com.example.eltaqs.data.local.AppDataBase
import com.example.eltaqs.data.local.WeatherLocalDataSource
import com.example.eltaqs.data.model.Alarm
import com.example.eltaqs.data.remote.WeatherRemoteDataSource
import com.example.eltaqs.data.sharedpreference.SharedPrefDataSource
import com.example.eltaqs.data.repo.WeatherRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Preview(showBackground = true)
@Composable
fun AlertsScreen() {
    val viewModel: AlertsViewModel = viewModel(
        factory = AlertsViewModelFactory(
            WeatherRepository.getInstance(
                WeatherRemoteDataSource(RetrofitHelper.apiService),
                WeatherLocalDataSource(AppDataBase.getInstance(LocalContext.current).getFavouritesDAO()),
                SharedPrefDataSource.getInstance(LocalContext.current)
            )
        )
    )

    val alerts = viewModel.alerts.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        viewModel.getAlarms()
    }

    val context = LocalContext.current

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->

    }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    context, android.Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }




    var showBottomSheet = remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Button(onClick = {
            showBottomSheet.value = true
        }) {
            Text(text = "Open Sheet")

        }

        if (showBottomSheet.value) {
            BottomSheetCompose(showBottomSheet, viewModel)
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetCompose(showBottomSheet: MutableState<Boolean>, viewModel: AlertsViewModel) {
    val modalBottomSheetState = rememberModalBottomSheetState()
    val coroutineScope = rememberCoroutineScope()

    var selectedOption by remember { mutableStateOf("Alarm") }

    var startDuration by remember { mutableStateOf("") }
    var endDuration by remember { mutableStateOf("") }

    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }

    val startInteractionSource = remember { MutableInteractionSource() }
    val endInteractionSource = remember { MutableInteractionSource() }

    val context = LocalContext.current
    val alarmScheduler = remember { AlarmScheduler(context) }

    LaunchedEffect(startInteractionSource) {
        startInteractionSource.interactions.collect { interaction ->
            if (interaction is PressInteraction.Press) {
                showStartTimePicker = true
            }
        }
    }

    LaunchedEffect(endInteractionSource) {
        endInteractionSource.interactions.collect { interaction ->
            if (interaction is PressInteraction.Press) {
                showEndTimePicker = true
            }
        }
    }

    ModalBottomSheet(
        onDismissRequest = { showBottomSheet.value = false },
        sheetState = modalBottomSheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = "Set Alarm", fontWeight = FontWeight.Bold, fontSize = 16.sp)

            OutlinedTextField(
                value = startDuration,
                onValueChange = {},
                label = { Text("Start duration") },
                leadingIcon = { Icon(imageVector = Icons.Default.AccessTime, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                interactionSource = startInteractionSource
            )

            OutlinedTextField(
                value = endDuration,
                onValueChange = {},
                label = { Text("End duration") },
                leadingIcon = { Icon(imageVector = Icons.Default.Timer, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                interactionSource = endInteractionSource
            )

            Text(text = "Notify me by", fontSize = 14.sp)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = selectedOption == "Alarm",
                        onClick = { selectedOption = "Alarm" }
                    )
                    Text("Alarm")
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = selectedOption == "Notification",
                        onClick = { selectedOption = "Notification" }
                    )
                    Text("Notification")
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(
                    modifier = Modifier
                        .weight(5f)
                        .padding(horizontal = 15.dp),
                    onClick = {
                        coroutineScope.launch {
                            modalBottomSheetState.hide()
                            showBottomSheet.value = false
                        }
                    },
                    border = BorderStroke(1.dp, Color.Red),
                    shape = RoundedCornerShape(50)
                ) {
                    Text(
                        text = "Cancel",
                        modifier = Modifier.padding(
                            horizontal = 10.dp,
                            vertical = 6.dp
                        ),
                        fontSize = 12.sp,
                        color = Color.Red
                    )

                }

                Button(
                    onClick = {
                        val id = System.currentTimeMillis().toInt()
                        viewModel.insertAlarm(Alarm(id, startDuration, endDuration))
                        alarmScheduler.scheduleAlarm(Alarm(id, startDuration, endDuration))
                        coroutineScope.launch {
                            modalBottomSheetState.hide()
                            showBottomSheet.value = false
                        }
                    },
                    modifier = Modifier
                        .weight(5f)
                        .padding(horizontal = 15.dp),
                    shape = RoundedCornerShape(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text(
                        text = "Save",
                        modifier = Modifier.padding(horizontal = 10.dp, 6.dp),
                        color = Color.White,
                        fontSize = 12.sp
                    )

                }
            }

            if (showStartTimePicker) {
                TimePickerDialog(
                    onCancel = { showStartTimePicker = false },
                    onConfirm = { newTime ->
                        startDuration = newTime
                        showStartTimePicker = false
                    }
                )
            }

            if (showEndTimePicker) {
                TimePickerDialog(
                    onCancel = { showEndTimePicker = false },
                    onConfirm = { newTime ->
                        endDuration = newTime
                        showEndTimePicker = false
                    }
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    onCancel: () -> Unit,
    onConfirm: (String) -> Unit
) {
    val state = rememberTimePickerState(is24Hour = false)
    val formatter = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }

    Dialog(
        onDismissRequest = onCancel,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = 6.dp,
            modifier = Modifier
                .width(IntrinsicSize.Min)
                .height(IntrinsicSize.Min)
                .background(
                    shape = MaterialTheme.shapes.extraLarge,
                    color = MaterialTheme.colorScheme.surface
                ),
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    text = "Select Time",
                    style = MaterialTheme.typography.labelMedium
                )
                TimePicker(state = state)

                Row(
                    modifier = Modifier
                        .height(40.dp)
                        .fillMaxWidth()
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    TextButton(onClick = onCancel) {
                        Text("Cancel")
                    }
                    TextButton(onClick = {
                        val cal = Calendar.getInstance()
                        cal.set(Calendar.HOUR_OF_DAY, state.hour)
                        cal.set(Calendar.MINUTE, state.minute)
                        cal.isLenient = false
                        onConfirm(formatter.format(cal.time))
                    }) {
                        Text("OK")
                    }
                }
            }
        }
    }
}

