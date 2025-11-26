package com.fiscal.compass.ui.components.pickers

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.fiscal.compass.ui.components.dialogs.TimePickerDialog
import com.fiscal.compass.ui.components.input.ReadOnlyDataEntryTextField
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePicker(
    modifier: Modifier = Modifier,
    label: String = "Time",
    selectedTime: Calendar? = null,
    timeFormat: String = "hh:mm a",
    isError: Boolean = false,
    errorMessage: String? = null,
    onTimeSelected: (Calendar) -> Unit = {}
) {
    var showTimePicker by remember { mutableStateOf(false) }

    val timeFormatter = remember { SimpleDateFormat(timeFormat, Locale.getDefault()) }
    val displayValue = selectedTime?.let { timeFormatter.format(it.time) } ?: ""

    ReadOnlyDataEntryTextField(
        modifier = modifier,
        label = label,
        value = displayValue,
        isError = isError,
        errorMessage = errorMessage,
        onClick = { showTimePicker = true }
    )

    if (showTimePicker) {
        TimePickerDialog(
            initialTime = selectedTime ?: Calendar.getInstance(),
            onDismiss = { showTimePicker = false },
            onConfirm = { timePickerState ->
                val calendar = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                    set(Calendar.MINUTE, timePickerState.minute)
                }
                onTimeSelected(calendar)
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TimePickerPreview() {
    var selectedTime by remember { mutableStateOf<Calendar?>(null) }
    TimePicker(
        label = "Select Time",
        selectedTime = selectedTime,
        onTimeSelected = { selectedTime = it }
    )
}