package com.app.uniqueplant.presentation.transactions

import android.annotation.SuppressLint
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.app.uniqueplant.R
import com.app.uniqueplant.domain.model.InputField
import com.app.uniqueplant.presentation.model.CategoryUi
import com.app.uniqueplant.ui.components.dialogs.DatePickerDialog
import com.app.uniqueplant.ui.components.dialogs.TimePickerDialog
import com.app.uniqueplant.ui.components.input.Calculator
import com.app.uniqueplant.ui.components.input.CustomExposedDropDownMenu
import com.app.uniqueplant.ui.components.input.ReadOnlyDataEntryTextField
import com.app.uniqueplant.ui.components.input.TransactionTypeSelector
import com.app.uniqueplant.ui.theme.UniquePlantTheme
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.GregorianCalendar
import java.util.Locale

enum class AddTransactionScreen {
    FORM,
    CALCULATOR
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    appNavController: NavHostController,
    state: AddTransactionState,
    onEvent: (AddTransactionEvent) -> Unit,
) {



    var currentScreen by remember { mutableStateOf(AddTransactionScreen.FORM) }
    val snackbarHostState = remember { SnackbarHostState() }


    BackHandler(enabled = currentScreen == AddTransactionScreen.CALCULATOR) {
            currentScreen = AddTransactionScreen.FORM
        }


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
                title = {
                    Text(
                        text = when (currentScreen) {
                            AddTransactionScreen.FORM -> "Add Transaction"
                            AddTransactionScreen.CALCULATOR -> "Enter Amount"
                        }
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        when (currentScreen) {
                            AddTransactionScreen.FORM -> appNavController.navigateUp()
                            AddTransactionScreen.CALCULATOR -> currentScreen =
                                AddTransactionScreen.FORM
                        }
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
            when (currentScreen) {
                AddTransactionScreen.FORM -> {
                    AddTransactionFormContent(
                        modifier = Modifier.padding(paddingValues),
                        state = state,
                        onEvent = onEvent,
                        onNextClick = { currentScreen = AddTransactionScreen.CALCULATOR }
                    )
                }

                AddTransactionScreen.CALCULATOR -> {
                    AddTransactionCalculatorContent(
                        modifier = Modifier.padding(paddingValues),
                        onEvent = onEvent,
                    )
                }
            }
        }
    )

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
            val calendar = Calendar.getInstance()
            val initialTime = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY))
                set(Calendar.MINUTE, calendar.get(Calendar.MINUTE))
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionFormContent(
    modifier: Modifier = Modifier,
    state: AddTransactionState,
    onEvent: (AddTransactionEvent) -> Unit,
    onNextClick: () -> Unit
) {
    var isCategoriesEmpty by remember { mutableStateOf(state.categories.isEmpty()) }
    LaunchedEffect(state.categories) {
        if (state.categories.isEmpty()) {
            isCategoriesEmpty = false
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp)
            .padding(bottom = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Form content
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp)
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

                val mainCategories = state.categories.keys.toList()
                val subCategories = state.categories.entries
                    .firstOrNull { it.key.categoryId == state.categoryId }?.value
                    ?: emptyList()

                CustomExposedDropDownMenu(
                    modifier = Modifier.fillMaxWidth(),
                    label = "Category",
                    options = mainCategories,
                    selectedOption = mainCategories.first { it.categoryId == state.categoryId },
                    onOptionSelected = { selected ->
                        onEvent(AddTransactionEvent.OnCategorySelected(selected.categoryId))
                    },
                    optionToString = {it.name}
                )

                CustomExposedDropDownMenu(
                    modifier = Modifier.fillMaxWidth(),
                    label = "Sub Category",
                    options = subCategories,
                    selectedOption = subCategories.firstOrNull { it.categoryId == state.subCategoryId }
                        ?: subCategories.firstOrNull(),
                    onOptionSelected = { selected ->
                        onEvent(AddTransactionEvent.OnSubCategorySelected(selected.categoryId))
                    },
                    optionToString = {
                        it.name
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

            ReadOnlyDataEntryTextField(
                modifier = Modifier
                    .fillMaxWidth(),
                onClick = {
                    onEvent(
                        AddTransactionEvent.OnAddTransactionDialogToggle(
                            AddTransactionDialog.DatePicker
                        )
                    )
                },
                label = "Date",
                value = state.date.let { SimpleDateFormat("dd MMM, yyyy", Locale.US).format(it) }
            )

            ReadOnlyDataEntryTextField(
                modifier = Modifier
                    .fillMaxWidth(),
                onClick = {
                    onEvent(
                        AddTransactionEvent.OnAddTransactionDialogToggle(
                            AddTransactionDialog.TimePicker
                        )
                    )
                },
                label = "Time",
                value = state.date.let { SimpleDateFormat("hh:mm a", Locale.US).format(it) }
            )
        }

        // Next button
        Button(
            shape = RoundedCornerShape(8.dp),
            onClick = onNextClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text(text = "Next - Enter Amount")
        }
    }
}

@Composable
fun AddTransactionCalculatorContent(
    modifier: Modifier = Modifier,
    onEvent: (AddTransactionEvent) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp)
            .padding(bottom = 8.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Calculator(
            modifier = Modifier.fillMaxSize(),
            onValueChange = { value ->
                onEvent(AddTransactionEvent.OnAmountChange(value))
            },
            onSaveClick = {
                onEvent(AddTransactionEvent.OnSaveClicked)
            },
        )
    }
}


@Preview(showSystemUi = true, showBackground = true, name = "Form Screen - Pixel 4", device = "spec:width=1080px,height=2280px,dpi=420,navigation=buttons")
//@Preview(showSystemUi = true, showBackground = true, name = "Form Screen - Nexus 7", device = Devices.NEXUS_7)
@Composable
fun AddTransactionFormContentPreview() {
    UniquePlantTheme {
        Scaffold {
            AddTransactionFormContent(
                modifier = Modifier.padding(it),
                state = AddTransactionState(
                    amount = InputField(
                        value = "",
                        error = ""
                    ),
                    description = InputField(
                        value = "Monthly Salary",
                        error = ""
                    ),
                    date = Date(
                        GregorianCalendar(
                            2025,
                            Calendar.JANUARY,
                            15,
                            14,
                            30
                        ).timeInMillis
                    ),
                    categoryId = 1L,
                    accountId = 1L,
                    isLoading = false,
                    isSuccess = false,
                    categories = mapOf(
                        CategoryUi(
                            categoryId = 1L,
                            name = "Food",
                            icon = "",
                        ) to listOf(
                            CategoryUi(
                                categoryId = 2L,
                                name = "Groceries",
                                icon = "",
                            ),
                            CategoryUi(
                                categoryId = 3L,
                                name = "Restaurants",
                                icon = "",
                            )
                        ),
                        CategoryUi(
                            categoryId = 4L,
                            name = "Salary",
                            icon = "",
                        ) to emptyList()
                    )
                ),
                onEvent = {},
                onNextClick = {}
            )
        }
    }
}

@Preview(showSystemUi = true, showBackground = true, name = "Calculator Screen - Pixel 4", device = "spec:width=1080px,height=2280px,dpi=420,navigation=buttons")
//@Preview(showSystemUi = true, showBackground = true, name = "Calculator Screen - Nexus 7", device = Devices.NEXUS_7)
@Composable
fun AddTransactionCalculatorContentPreview() {
    UniquePlantTheme {
        Scaffold {
            AddTransactionCalculatorContent(
                modifier = Modifier.padding(it),
                onEvent = {},
            )
        }
    }
}
