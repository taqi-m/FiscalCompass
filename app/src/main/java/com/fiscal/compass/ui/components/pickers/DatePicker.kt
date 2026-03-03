package com.fiscal.compass.ui.components.pickers

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.fiscal.compass.domain.util.DateTimeUtil
import com.fiscal.compass.ui.components.dialogs.DatePickerDialog
import com.fiscal.compass.ui.components.input.ReadOnlyDataEntryTextField

@Composable
fun DatePicker(
    modifier: Modifier = Modifier,
    label: String = "Date",
    selectedDate: Long? = null,
    dateFormat: String = "MMM dd, yyyy",
    isError: Boolean = false,
    errorMessage: String? = null,
    onDateSelected: (Long) -> Unit = {}
) {
    var showDatePicker by remember { mutableStateOf(false) }

    val displayValue = selectedDate?.let {
        DateTimeUtil.formatTimestampAsDate(it, dateFormat)
    } ?: ""

    ReadOnlyDataEntryTextField(
        modifier = modifier,
        label = label,
        value = displayValue,
        isError = isError,
        errorMessage = errorMessage,
        onClick = { showDatePicker = true }
    )

    if (showDatePicker) {
        DatePickerDialog(
            selectedDate = selectedDate ?: DateTimeUtil.getCurrentTimestamp(),
            onDismissRequest = { showDatePicker = false },
            onDateSelected = { date ->
                date?.let { onDateSelected(it) }
                showDatePicker = false
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DatePickerPreview() {
    var selectedDate by remember { mutableStateOf<Long?>(null) }
    DatePicker(
        label = "Select Date",
        selectedDate = selectedDate,
        onDateSelected = { selectedDate = it }
    )
}