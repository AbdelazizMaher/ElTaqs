package com.example.eltaqs.alert

import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.eltaqs.R
import com.example.eltaqs.Utils.isEndTimeValid
import com.example.eltaqs.Utils.isFutureDateTime
import com.example.eltaqs.Utils.startOfDayMillis
import com.example.eltaqs.alert.manager.AlarmScheduler
import com.example.eltaqs.data.local.AppDataBase
import com.example.eltaqs.data.local.WeatherLocalDataSource
import com.example.eltaqs.data.model.Alarm
import com.example.eltaqs.data.remote.WeatherRemoteDataSource
import com.example.eltaqs.data.sharedpreference.SharedPrefDataSource
import com.example.eltaqs.data.repo.WeatherRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun AlertsScreen(snackbarHostState: SnackbarHostState, onFabClick: MutableState<() -> Unit>) {
    val viewModel: AlertsViewModel = viewModel(
        factory = AlertsViewModelFactory(
            WeatherRepository.getInstance(
                WeatherRemoteDataSource(RetrofitHelper.apiService),
                WeatherLocalDataSource(AppDataBase.getInstance(LocalContext.current).getFavouritesDAO()),
                SharedPrefDataSource.getInstance(LocalContext.current)
            )
        )
    )

    val context = LocalContext.current
    val alerts = viewModel.alerts.collectAsStateWithLifecycle()
    val alarmScheduler = remember { AlarmScheduler(context) }
    LaunchedEffect(Unit) {
        viewModel.getAlarms()
    }

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

    Box(modifier = Modifier.fillMaxSize()) {
        onFabClick.value = {
            showBottomSheet.value = true
        }
        if (showBottomSheet.value) {
            BottomSheetCompose(showBottomSheet, viewModel, alarmScheduler)
        }

        Column(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Top
            ) {
                items(
                    alerts.value,
                    key = { it.id }
                    ) { alert ->
                    SwipeToDeleteContainer(
                        item = alert,
                        onDelete = {
                            viewModel.deleteAlarm(it)
                            alarmScheduler.cancelAlarm(it) },
                        onRestore = {
                            viewModel.insertAlarm(it)
                            alarmScheduler.scheduleAlarm(it)
                                    },
                        snackBarHostState = snackbarHostState
                    ) { _ ->
                        AlertCard(
                            startTime = alert.startTime,
                            endTime = alert.endTime,
                            location = "Egypt"
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetCompose(
    showBottomSheet: MutableState<Boolean>,
    viewModel: AlertsViewModel,
    alarmScheduler: AlarmScheduler
) {
    val modalBottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val coroutineScope = rememberCoroutineScope()

    var selectedOption by remember { mutableStateOf("Alarm") }

    var selectedDate by remember { mutableStateOf<Long?>(null) }
    var startDuration by remember { mutableStateOf("") }
    var endDuration by remember { mutableStateOf("") }

    var showDatePicker by remember { mutableStateOf(false) }
    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }

    var startError by remember { mutableStateOf<String?>(null) }
    var endError by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current

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
                value = selectedDate?.let { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(it)) } ?: "",
                onValueChange = {},
                label = { Text("Select Date") },
                leadingIcon = { Icon(imageVector = Icons.Default.CalendarToday, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                isError = selectedDate == null,
                interactionSource = remember { MutableInteractionSource() }.also { source ->
                    LaunchedEffect(source) {
                        source.interactions.collect { interaction ->
                            if (interaction is PressInteraction.Press) {
                                showDatePicker = true
                            }
                        }
                    }
                }
            )

            OutlinedTextField(
                value = startDuration,
                onValueChange = {},
                label = { Text("Start Time") },
                leadingIcon = { Icon(imageVector = Icons.Default.AccessTime, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                isError = startError != null,
                enabled = selectedDate != null,
                interactionSource = remember { MutableInteractionSource() }.also { source ->
                    LaunchedEffect(source) {
                        source.interactions.collect { interaction ->
                            if (interaction is PressInteraction.Press && selectedDate != null) {
                                showStartTimePicker = true
                            }
                        }
                    }
                }
            )
            if (startError != null) {
                Text(text = startError ?: "", color = Color.Red, fontSize = 12.sp, modifier = Modifier.padding(start = 16.dp, top = 4.dp))
            }

            OutlinedTextField(
                value = endDuration,
                onValueChange = {},
                label = { Text("End Time") },
                leadingIcon = { Icon(imageVector = Icons.Default.Timer, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                isError = endError != null,
                enabled = startDuration.isNotBlank(),
                interactionSource = remember { MutableInteractionSource() }.also { source ->
                    LaunchedEffect(source) {
                        source.interactions.collect { interaction ->
                            if (interaction is PressInteraction.Press && startDuration.isNotBlank()) {
                                showEndTimePicker = true
                            }
                        }
                    }
                }
            )
            if (endError != null) {
                Text(text = endError ?: "", color = Color.Red, fontSize = 12.sp, modifier = Modifier.padding(start = 16.dp, top = 4.dp))
            }

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
                    modifier = Modifier.weight(5f).padding(horizontal = 15.dp),
                    onClick = { coroutineScope.launch { modalBottomSheetState.hide(); showBottomSheet.value = false } },
                    border = BorderStroke(1.dp, Color.Red),
                    shape = RoundedCornerShape(50)
                ) {
                    Text(text = "Cancel", modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp), fontSize = 12.sp, color = Color.Red)
                }

                Button(
                    onClick = {
                        if (selectedDate == null) {
                            startError = "Please select a date"
                        } else if (startDuration.isBlank()) {
                            startError = "Start time is required"
                        } else if (endDuration.isBlank()) {
                            endError = "End time is required"
                        } else {
                            val id = System.currentTimeMillis().toInt()
                            viewModel.insertAlarm(Alarm(id, startDuration, endDuration))
                            alarmScheduler.scheduleAlarm(Alarm(id, startDuration, endDuration))
                            coroutineScope.launch { modalBottomSheetState.hide(); showBottomSheet.value = false }
                        }
                    },
                    modifier = Modifier.weight(5f).padding(horizontal = 15.dp),
                    shape = RoundedCornerShape(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text(text = "Save", modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp), color = Color.White, fontSize = 12.sp)
                }
            }

            if (showDatePicker) {
                DatePickerModal(
                    onDateSelected = { date ->
                        selectedDate = date
                        startError = null
                    },
                    onDismiss = { showDatePicker = false }
                )
            }

            if (showStartTimePicker) {
                TimePickerDialog(
                    onCancel = { showStartTimePicker = false },
                    onConfirm = { newTime ->
                        if (isFutureDateTime(selectedDate, newTime)) {
                            startDuration = newTime
                            startError = null
                            showStartTimePicker = false
                        } else {
                            startError = "Start time must be in the future"
                        }
                    }
                )
            }

            if (showEndTimePicker) {
                TimePickerDialog(
                    onCancel = { showEndTimePicker = false },
                    onConfirm = { newTime ->
                        if (isEndTimeValid(startDuration, newTime)) {
                            endDuration = newTime
                            endError = null
                            showEndTimePicker = false
                        } else {
                            endError = "End time must be after start time"
                        }
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

@Composable
fun AlertCard(
    startTime: String,
    endTime: String,
    location: String,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clip(RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF181A2A))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Start from",
                    color = Color.Gray,
                    fontSize = 12.sp
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = startTime,
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "Arrow",
                        tint = Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    Text(
                        text = endTime,
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Text(
                text = location,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            IconButton(onClick = {  }) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notification Bell",
                    tint = Color.White
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModal(
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = System.currentTimeMillis(),
        initialDisplayedMonthMillis = System.currentTimeMillis()
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                val selectedMillis = datePickerState.selectedDateMillis
                if (selectedMillis != null && selectedMillis >= System.currentTimeMillis().startOfDayMillis()) {
                    onDateSelected(selectedMillis)
                    onDismiss()
                }
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(state = datePickerState)
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



