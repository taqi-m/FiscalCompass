package com.app.uniqueplant.presentation.transactions.addExpenseScreen

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.uniqueplant.R
import com.app.uniqueplant.data.model.Category
import com.app.uniqueplant.domain.model.InputField
import com.app.uniqueplant.ui.components.CustomExposedDropDownMenu
import com.app.uniqueplant.ui.components.DatePickerDialog
import com.app.uniqueplant.ui.components.ReadOnlyDataEntryTextField
import com.app.uniqueplant.ui.components.TimePickerDialog
import com.app.uniqueplant.ui.components.input.Calculator
import com.app.uniqueplant.ui.theme.UniquePlantTheme
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.GregorianCalendar
import java.util.Locale


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseScreen(
    state: AddExpenseState,
    onEvent: (AddExpenseEvent) -> Unit,
) {

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state) {
        if (state.isSuccess) {
            Log.d("AddExpenseScreen", "Expense added successfully: ${state.message}")
            snackbarHostState.showSnackbar(state.message)
            onEvent(AddExpenseEvent.OnSuccessHandled)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState
            )
        },
        topBar = {
            TopAppBar(
                title = { Text(text = "Add Expense") },
//                colors = TopAppBarDefaults.topAppBarColors(
//                    containerColor = MaterialTheme.colorScheme.primary,
//                    titleContentColor = MaterialTheme.colorScheme.onPrimary
//                ),
                navigationIcon = {
                    IconButton(onClick = { }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_arrow_back_24),
                            contentDescription = "Back"
                        )
                    }
                },
                /*
                actions = {
                    OutlinedButton (
                        onClick = { onEvent(AddExpenseEvent.OnSaveClicked) },
                        shape = RoundedCornerShape(25),
                        modifier = Modifier.padding(end = 8.dp),
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_check_24),
                            contentDescription = "Save Expense",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Save",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                },
                */
            )
        },
        content = { paddingValues ->
            AddExpenseContent(
                modifier = Modifier.padding(paddingValues),
                state = state,
                onEvent = onEvent
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseContent(
    modifier: Modifier = Modifier,
    state: AddExpenseState,
    onEvent: (AddExpenseEvent) -> Unit,
) {
    var isCategoriesEmpty by remember { mutableStateOf(state.categories.isEmpty()) }
    LaunchedEffect(state.categories) {
        if (state.categories.isEmpty()) {
            isCategoriesEmpty = false
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween, // Changed to SpaceBetween to push calculator to bottom
            horizontalAlignment = Alignment.Start
        )
        {
            // Top content section
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                if (!isCategoriesEmpty) {
                    CustomExposedDropDownMenu(
                        modifier = Modifier.fillMaxWidth(),
                        label = "Select Category",
                        options = state.categories,
                        onOptionSelected = { selected ->
                            onEvent(AddExpenseEvent.OnCategorySelected(selected.categoryId))
                        }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                DateAndTimeSelection(
                    modifier = Modifier.fillMaxWidth(),
                    date = state.date,
                    onDateToggle = { onEvent(AddExpenseEvent.OnDateDialogToggle) },
                    onTimeToggle = { onEvent(AddExpenseEvent.OnTimeDialogToggle) },
                )
            }

            // Calculator at the bottom
            Calculator(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                onValueChange = { value ->
                    onEvent(AddExpenseEvent.OnAmountChange(value))
                },
                onSaveClick = {
                    onEvent(AddExpenseEvent.OnSaveClicked)
                },
            )
        }

        if (state.isDateDialogOpen) {
            DatePickerDialog(
                onDismissRequest = {
                    onEvent(AddExpenseEvent.OnDateDialogToggle)
                },
                onDateSelected = { selectedDate ->
                    onEvent(AddExpenseEvent.OnDateDialogToggle)
                    onEvent(AddExpenseEvent.DateSelected(selectedDate))
                },
            )
        }
        if (state.isTimeDialogOpen) {
            val initialTime = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, state.date.hours)
                set(Calendar.MINUTE, state.date.minutes)
            }
            TimePickerDialog(
                onDismiss = { onEvent(AddExpenseEvent.OnTimeDialogToggle) },
                onConfirm = { onEvent(AddExpenseEvent.OnTimeSelected(it)) },
                initialTime = initialTime
            )
        }
    }
}


@Composable
fun DateAndTimeSelection(
    modifier: Modifier = Modifier,
    date: Date,
    onDateToggle: () -> Unit,
    onTimeToggle: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ReadOnlyDataEntryTextField(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            label = "Date",
            value = date.let { SimpleDateFormat("dd-MM-yyyy", Locale.US).format(it) },
//                isError = state.amount.error.isNotEmpty(),
//                errorMessage = state.amount.error.ifEmpty { null },
            trailingIcon = {
                IconButton(
                    onClick = onDateToggle,
                    modifier = Modifier
                        .padding(end = 8.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_calendar_24),
                        contentDescription = "Select Date",
                        modifier = Modifier.padding(8.dp),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        )
        ReadOnlyDataEntryTextField(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            label = "Time",
            value = date.let { SimpleDateFormat("hh:mm a", Locale.US).format(it) },
//                isError = state.amount.error.isNotEmpty(),
//                errorMessage = state.amount.error.ifEmpty { null },
            trailingIcon = {
                IconButton(
                    onClick = onTimeToggle,
                    modifier = Modifier
                        .padding(end = 8.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_time_24),
                        contentDescription = "Select Date",
                        modifier = Modifier.padding(8.dp),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        )
    }
}


@Preview(showSystemUi = true, showBackground = true)
@Composable
fun AddExpenseScreenPreview() {
    UniquePlantTheme {
        AddExpenseScreen(
            state = AddExpenseState(
                amount = InputField(
                    value = "5000",
                    error = ""
                ),
                description = InputField(
                    value = "Monthly Salary",
                    error = "Put a short description here"
                ),
                date = Date(
                    GregorianCalendar(
                        Calendar.YEAR,
                        Calendar.MONTH,
                        Calendar.DAY_OF_MONTH,
                        Calendar.HOUR_OF_DAY,
                        Calendar.MINUTE
                    ).timeInMillis
                ),
                categoryId = 1L,
                accountId = 1L,
                isLoading = false,
                isSuccess = false,
                categories = listOf(
                    Category(
                        categoryId = 1L,
                        name = "Salary",
                        color = 0xFF6200EE.toInt(),
                        isExpenseCategory = false
                    ),
                    Category(
                        categoryId = 2L,
                        name = "Investment",
                        color = 0xFF03DAC5.toInt(),
                        isExpenseCategory = false
                    )
                )
            ),
            onEvent = {}
        )
    }
}
