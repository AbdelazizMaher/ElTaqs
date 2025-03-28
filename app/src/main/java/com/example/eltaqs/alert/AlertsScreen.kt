package com.example.eltaqs.alert

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.eltaqs.data.local.AppDataBase
import com.example.eltaqs.data.local.WeatherLocalDataSource
import com.example.eltaqs.data.remote.WeatherRemoteDataSource
import com.example.eltaqs.data.sharedpreference.SharedPrefDataSource
import com.example.eltaqs.data.repo.WeatherRepository
import kotlinx.coroutines.launch

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

    var showBottomSheet = remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Button(onClick = {
            showBottomSheet.value = true
        }) {
            Text(text = "Open Sheet")

        }

        if (showBottomSheet.value) {
            BottomSheetCompose(showBottomSheet)
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetCompose(showBottomSheet: MutableState<Boolean>) {
    val modalBottomSheetState = rememberModalBottomSheetState()
    val coroutineScope = rememberCoroutineScope()

    var startDuration by remember { mutableStateOf("") }
    var endDuration by remember { mutableStateOf("") }

    var selectedOption by remember { mutableStateOf("Alarm") }

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
            Text(
                text = "Set Alarm",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )

            OutlinedTextField(
                value = startDuration,
                onValueChange = { startDuration = it },
                label = { Text("Start duration") },
                leadingIcon = { Icon(imageVector = Icons.Default.AccessTime, contentDescription = null) },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = endDuration,
                onValueChange = { endDuration = it },
                label = { Text("End duration") },
                leadingIcon = { Icon(imageVector = Icons.Default.Timer, contentDescription = null) },
                modifier = Modifier.fillMaxWidth()
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
        }
    }
}
