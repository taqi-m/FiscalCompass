package com.app.uniqueplant.presentation.transactions

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.app.uniqueplant.R
import com.app.uniqueplant.domain.model.Category
import com.app.uniqueplant.domain.model.InputField
import com.app.uniqueplant.ui.components.dialogs.DatePickerDialog
import com.app.uniqueplant.ui.components.dialogs.TimePickerDialog
import com.app.uniqueplant.ui.components.input.Calculator
import com.app.uniqueplant.ui.components.input.CustomExposedDropDownMenu
import com.app.uniqueplant.ui.components.input.ReadOnlyDataEntryTextField
import com.app.uniqueplant.ui.components.input.TransactionTypeSelector
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.GregorianCalendar
import java.util.Locale


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    appNavController: NavHostController,
    state: AddTransactionState,
    onEvent: (AddTransactionEvent) -> Unit,
) {

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state) {
        if (state.isSuccess) {
            Log.d("AddExpenseScreen", state.message)
            snackbarHostState.showSnackbar(state.message)
            onEvent(AddTransactionEvent.OnSuccessHandled)
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
                title = { Text(text = "Add Transaction") },
                navigationIcon = {
                    IconButton(onClick = {
                        appNavController.navigateUp()
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_arrow_back_24),
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        content = { paddingValues ->
            AddTransactionContent(
                modifier = Modifier.padding(paddingValues),
                state = state,
                onEvent = onEvent
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionContent(
    modifier: Modifier = Modifier,
    state: AddTransactionState,
    onEvent: (AddTransactionEvent) -> Unit,
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
            .padding(horizontal = 9.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.Start
        )
        {
            // Top content section
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                TransactionTypeSelector(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    selectedOption = state.transactionType,
                    onOptionSelected = { selectedType ->
                        onEvent(AddTransactionEvent.OnTypeSelected(selectedType))
                    }
                )


                if (!isCategoriesEmpty) {
                    CustomExposedDropDownMenu(
                        modifier = Modifier.fillMaxWidth(),
                        label = "Category",
                        options = state.categories,
                        selectedOption = state.categories.firstOrNull { it.categoryId == state.categoryId }
                            ?: state.categories.firstOrNull(),
                        onOptionSelected = { selected ->
                            onEvent(AddTransactionEvent.OnCategorySelected(selected.categoryId))
                        }
                    )
                }

                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(text = "Note") },
                    placeholder = { Text(text = "Add a short description") },
                    value = state.description.value,
                    onValueChange = {
                        onEvent(AddTransactionEvent.OnDescriptionChange(it))
                    }
                )

                DateAndTimeSelection(
                    modifier = Modifier.fillMaxWidth(),
                    date = state.date,
                    onDateToggle = {
                        onEvent(
                            AddTransactionEvent.OnAddTransactionDialogToggle(
                                AddTransactionDialog.DatePicker
                            )
                        )
                    },
                    onTimeToggle = {
                        onEvent(
                            AddTransactionEvent.OnAddTransactionDialogToggle(
                                AddTransactionDialog.TimePicker
                            )
                        )
                    },
                )


            }

            // Calculator at the bottom
            Calculator(
                modifier = Modifier
                    .wrapContentSize(),
                onValueChange = { value ->
                    onEvent(AddTransactionEvent.OnAmountChange(value))
                },
                onSaveClick = {
                    onEvent(AddTransactionEvent.OnSaveClicked)
                },
            )
        }

        when (state.currentDialog) {
            is AddTransactionDialog.DatePicker -> {
                DatePickerDialog(
                    onDismissRequest = {
                        onEvent(
                            AddTransactionEvent.OnAddTransactionDialogToggle(
                                AddTransactionDialog.Hidden
                            )
                        )
                    },
                    onDateSelected = { selectedDate ->
                        onEvent(
                            AddTransactionEvent.OnAddTransactionDialogSubmit(
                                AddTransactionDialogSubmit.DateSelected(selectedDate)
                            )
                        )
                    },
                )
            }

            is AddTransactionDialog.TimePicker -> {
                val initialTime = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, state.date.hours)
                    set(Calendar.MINUTE, state.date.minutes)
                }
                TimePickerDialog(
                    onDismiss = {
                        onEvent(
                            AddTransactionEvent.OnAddTransactionDialogToggle(
                                AddTransactionDialog.Hidden
                            )
                        )
                    },
                    onConfirm = {
                        onEvent(
                            AddTransactionEvent.OnAddTransactionDialogSubmit(
                                AddTransactionDialogSubmit.TimeSelected(it)
                            )
                        )
                    },
                    initialTime = initialTime
                )
            }

            else -> {}
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
            onClick = onDateToggle,
            label = "Date",
            value = date.let { SimpleDateFormat("dd-MM-yyyy", Locale.US).format(it) }
        )
        ReadOnlyDataEntryTextField(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            onClick = onTimeToggle,
            label = "Time",
            value = date.let { SimpleDateFormat("hh:mm a", Locale.US).format(it) }
        )
    }
}

@Preview(
    showSystemUi = true, showBackground = true, name = "Pixel 4",
    device = "spec:width=1080px,height=2280px,dpi=420,navigation=buttons"
)
@Preview(showSystemUi = true, showBackground = true, name = "Nexus 7", device = Devices.NEXUS_7)
@Preview(
    showSystemUi = true, showBackground = true,
    device = "spec:parent=pixel_5,navigation=buttons"
)
@Composable
fun AddTransactionScreenPreview() {
    AddTransactionScreen(
        appNavController = rememberNavController(),
        state = AddTransactionState(
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
                    2025,
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
        onEvent = {},
    )
}
