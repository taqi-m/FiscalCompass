package com.fiscal.compass.ui.components.pickers

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.fiscal.compass.domain.util.DateTimeUtil
import com.fiscal.compass.domain.util.DateTimeUtil.calendarToDate
import com.fiscal.compass.domain.util.DateTimeUtil.formatTime
import com.fiscal.compass.domain.util.DateTimeUtil.formatTimestampAsTime
import com.fiscal.compass.domain.util.DateTimeUtil.getCurrentCalendar
import com.fiscal.compass.domain.util.DateTimeUtil.getCurrentTimestamp
import com.fiscal.compass.domain.util.DateTimeUtil.setTimeOnTimestamp
import com.fiscal.compass.domain.util.DateTimeUtil.timestampToCalendar
import com.fiscal.compass.ui.components.dialogs.TimePickerDialog
import com.fiscal.compass.ui.components.input.ReadOnlyDataEntryTextField
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePicker(
    modifier: Modifier = Modifier,
    selectedTime: Long? = null,
    label: String = "Time",
    timeFormat: String = "hh:mm a",
    isError: Boolean = false,
    errorMessage: String? = null,
    onTimeSelected: (Long) -> Unit = {}
) {
    var showTimePicker by remember { mutableStateOf(false) }

    ReadOnlyDataEntryTextField(
        modifier = modifier,
        label = label,
        value = formatTimestampAsTime(selectedTime ?: getCurrentTimestamp(), timeFormat),
        isError = isError,
        errorMessage = errorMessage,
        onClick = { showTimePicker = true }
    )

    if (showTimePicker) {
        TimePickerDialog(
            initialTime = timestampToCalendar(selectedTime ?: getCurrentTimestamp()),
            onDismiss = { showTimePicker = false },
            onConfirm = { timePickerState ->
                val currentTime = getCurrentTimestamp()
                val selectedTime = setTimeOnTimestamp(currentTime, timePickerState.hour, timePickerState.minute)
                onTimeSelected(selectedTime)
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TimePickerPreview() {
    var selectedTime by remember { mutableStateOf<Long?>(null) }
    TimePicker(
        label = "Select Time",
        onTimeSelected = { selectedTime = it }
    )
}