package com.app.uniqueplant.presentation.screens.transactionScreens.addTransaction

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.app.uniqueplant.R
import com.app.uniqueplant.presentation.model.CategoryUi
import com.app.uniqueplant.presentation.model.InputField
import com.app.uniqueplant.presentation.model.PersonUi
import com.app.uniqueplant.presentation.model.TransactionType
import com.app.uniqueplant.presentation.screens.category.UiState
import com.app.uniqueplant.ui.components.dialogs.DatePickerDialog
import com.app.uniqueplant.ui.components.dialogs.TimePickerDialog
import com.app.uniqueplant.ui.components.input.Calculator
import com.app.uniqueplant.ui.components.input.CustomExposedDropDownMenu
import com.app.uniqueplant.ui.components.input.ReadOnlyDataEntryTextField
import com.app.uniqueplant.ui.components.input.TypeSwitch
import com.app.uniqueplant.ui.theme.UniquePlantTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import java.util.Calendar

private enum class AddTransactionScreen {
    FORM,
    SUCCESS,
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

    var currentScreen by rememberSaveable { mutableStateOf(AddTransactionScreen.FORM) }
    val snackbarHostState = remember { SnackbarHostState() }
    val uiState = state.uiState

    BackHandler(enabled = currentScreen == AddTransactionScreen.CALCULATOR || currentScreen == AddTransactionScreen.SUCCESS) {
        currentScreen = AddTransactionScreen.FORM
    }

    var titleText by remember { mutableStateOf("Add Transaction") }


    LaunchedEffect(uiState) {
        val message: String? = when (uiState) {
            is UiState.Error -> uiState.message
            is UiState.Success -> {
                currentScreen = AddTransactionScreen.SUCCESS;
                null
            }

            else -> null
        }
        if (uiState !is UiState.Success) {
            message?.let { msg ->
                if (msg.isNotEmpty()) {
                    snackbarHostState.showSnackbar(
                        message = msg,
                        duration = SnackbarDuration.Short,
                        actionLabel = "Dismiss"
                    )
                    onEvent(AddTransactionEvent.OnUiReset)
                }
            }
        }
    }

    LaunchedEffect(currentScreen) {
        titleText = when (currentScreen) {
            AddTransactionScreen.FORM -> "Add Transaction"
            AddTransactionScreen.CALCULATOR -> "Enter Amount"
            AddTransactionScreen.SUCCESS -> ""
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
            if (currentScreen != AddTransactionScreen.SUCCESS) {
                TopAppBar(
                    title = {
                        Text(text = titleText)
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            when (currentScreen) {
                                AddTransactionScreen.SUCCESS -> appNavController.navigateUp()
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
            }
        },
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
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

                    AddTransactionScreen.SUCCESS -> {
                        AddTransactionSuccessContent(
                            modifier = Modifier.padding(paddingValues),
                            onGoToHomeClick = { appNavController.navigateUp() }
                        )
                    }
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AddTransactionFormContent(
    modifier: Modifier = Modifier,
    state: AddTransactionState,
    onEvent: (AddTransactionEvent) -> Unit,
    onNextClick: () -> Unit
) {
    var isCategoriesEmpty by remember { mutableStateOf(state.categories.isEmpty()) }
    LaunchedEffect(state.categories, state.persons) {
        isCategoriesEmpty = state.categories.isEmpty()


    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp)
            .padding(bottom = 8.dp),
    ) {
        // Form content
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            val selectedIndex = TransactionType.entries.indexOf(state.transactionType)
            TypeSwitch(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(bottom = 8.dp)
                    .height(40.dp),
                shape = MaterialTheme.shapes.small,
                typeOptions = TransactionType.entries.map { it.name },
                selectedTypeIndex = selectedIndex,
                onTypeSelected = { index ->
                    val selectedType = TransactionType.entries[index]
                    onEvent(AddTransactionEvent.OnTypeSelected(selectedType))
                }
            )

            if (!isCategoriesEmpty) {
                val mainCategories = state.categories.keys.toList()
                var subCategories = state.categories.entries
                    .firstOrNull { it.key.categoryId == state.categoryId }?.value
                    ?: emptyList()
                subCategories = listOf(
                    CategoryUi(
                        categoryId = -1L,
                        parentId = null,
                        isExpenseCategory = false,
                        name = "N/A",
                        description = "",
                        icon = "",
                        color = ""
                    )
                ) + subCategories

                CustomExposedDropDownMenu(
                    modifier = Modifier.fillMaxWidth(),
                    label = "Category",
                    options = mainCategories,
                    selectedOption = mainCategories.first { it.categoryId == state.categoryId },
                    onOptionSelected = { selected ->
                        onEvent(AddTransactionEvent.OnCategorySelected(selected.categoryId))
                    },
                    optionToString = { it.name }
                )

                CustomExposedDropDownMenu(
                    modifier = Modifier.fillMaxWidth(),
                    label = "Sub Category",
                    options = subCategories,
                    selectedOption = subCategories.firstOrNull { it.categoryId == state.subCategoryId }
                        ?: subCategories.firstOrNull(),
                    onOptionSelected = { selected ->
                        if (selected.categoryId == -1L) {
                            onEvent(AddTransactionEvent.OnSubCategorySelected(null))
                            return@CustomExposedDropDownMenu
                        }
                        onEvent(AddTransactionEvent.OnSubCategorySelected(selected.categoryId))
                    },
                    optionToString = {
                        it.name
                    }
                )
            }

            var persons = state.persons
            persons = listOf(
                PersonUi(
                    personId = -1L,
                    name = "N/A",
                    personType = "DEALER"
                )
            ) + persons

            CustomExposedDropDownMenu(
                modifier = Modifier.fillMaxWidth(),
                label = "Person",
                options = persons,
                selectedOption = persons.firstOrNull { it.personId == state.personId }
                    ?: persons.firstOrNull(),
                onOptionSelected = { selected ->
                    if (selected.personId == -1L) {
                        onEvent(AddTransactionEvent.OnPersonSelected(null))
                        return@CustomExposedDropDownMenu
                    }
                    onEvent(AddTransactionEvent.OnPersonSelected(selected.personId))
                },
                optionToString = {
                    it.name
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
                value = state.formatedDate
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
                value = state.formatedTime
            )

            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Note",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outline,
                            shape = MaterialTheme.shapes.extraSmall
                        ),
                    value = state.description.value,
                    onValueChange = { onEvent(AddTransactionEvent.OnDescriptionChange(it)) },
                    placeholder = { Text(text = "Add a short note") },
                    minLines = 4,
                    maxLines = 4,
                    shape = MaterialTheme.shapes.extraSmall,
                    colors = OutlinedTextFieldDefaults.colors()
                )
            }
        }

        // Next button
        Button(
            shape = RoundedCornerShape(8.dp),
            onClick = onNextClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text(
                text = "Next - Enter Amount",
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(vertical = 8.dp)
            )
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

@Composable
fun AddTransactionSuccessContent(
    modifier: Modifier = Modifier,
    onGoToHomeClick: () -> Unit
) {
    val countdownFrom = 3
    var remainingTime by remember { mutableIntStateOf(countdownFrom) }
    var triggerAnimation by remember { mutableStateOf(false) }

    val progress by animateFloatAsState(
        targetValue = if (triggerAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = countdownFrom * 1000),
        label = "progress"
    )

    LaunchedEffect(Unit) {
        triggerAnimation = true

        snapshotFlow { progress }
            .collectLatest {
                remainingTime = countdownFrom - (countdownFrom * it).toInt()
                if (it == 1f) {
                    delay(200) // Small delay to ensure UI updates before navigation
                    onGoToHomeClick()
                }
            }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier
                .padding(bottom = 32.dp)
                .graphicsLayer {
                    shadowElevation = 8.dp.toPx()
                    shape = CircleShape
                    clip = true
                }
        ) {
            Box(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_check_24),
                    contentDescription = "Success",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier
                        .height(48.dp)
                        .aspectRatio(1f)
                )
            }
        }



        Text(
            text = "Transaction Added Successfully!",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier
                .padding(bottom = 24.dp)
                .fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Column(
            modifier = Modifier
                .padding(bottom = 24.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = "Closing in $remainingTime seconds...")
            LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth())
        }

        Button(
            shape = RoundedCornerShape(8.dp),
            onClick = onGoToHomeClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
        ) {
            Text(text = "Done")
        }
    }
}

@Preview(
    showSystemUi = true,
    showBackground = true,
    name = "Form Screen - Pixel 4",
    device = "spec:width=1080px,height=2280px,dpi=420,navigation=buttons"
)
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
                        value = "",
                        error = ""
                    ),
                    personId = 1L,
                    categoryId = 1L,
                    categories = mapOf(
                        CategoryUi(
                            categoryId = 1L,
                            parentId = null,
                            isExpenseCategory = false,
                            name = "Income",
                            description = "Income Category",
                            icon = "ic_income",
                            color = "FF4CAF50"
                        ) to listOf(
                            CategoryUi(
                                categoryId = 2L,
                                parentId = 1L,
                                isExpenseCategory = false,
                                name = "Salary",
                                description = "Salary Category",
                                icon = "ic_salary",
                                color = "FF2196F3"
                            ),
                            CategoryUi(
                                categoryId = 3L,
                                parentId = 1L,
                                isExpenseCategory = false,
                                name = "Business",
                                description = "Business Category",
                                icon = "ic_business",
                                color = "FFFF9800"
                            )
                        ),
                        CategoryUi(
                            categoryId = 4L,
                            parentId = null,
                            isExpenseCategory = true,
                            name = "Expense",
                            description = "Expense Category",
                            icon = "ic_expense",
                            color = "FFF44336"
                        ) to listOf(
                            CategoryUi(
                                categoryId = 5L,
                                parentId = 4L,
                                isExpenseCategory = true,
                                name = "Food",
                                description = "Food Category",
                                icon = "ic_food",
                                color = "FFFFEB3B"
                            ),
                            CategoryUi(
                                categoryId = 6L,
                                parentId = 4L,
                                isExpenseCategory = true,
                                name = "Transport",
                                description = "Transport Category",
                                icon = "ic_transport",
                                color = "FF9C27B0"
                            )
                        )
                    ),
                    persons = listOf(
                        PersonUi(
                            personId = 1L,
                            name = "Alice",
                            personType = "DEALER"
                        )
                    ),
                    formatedDate = "2024-01-01",
                    formatedTime = "12:00 PM",
                ),
                onEvent = {},
                onNextClick = {}
            )
        }
    }
}

@Preview(
    showSystemUi = true,
    showBackground = true,
    name = "Calculator Screen - Pixel 4",
    device = "spec:width=1080px,height=2280px,dpi=420,navigation=buttons"
)
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

@Preview(
    showSystemUi = true,
    showBackground = true,
    name = "Success Screen - Pixel 4",
    device = "spec:width=1080px,height=2280px,dpi=420,navigation=buttons"
)
@Composable
fun AddTransactionSuccessContentPreview() {
    UniquePlantTheme {
        Scaffold {
            AddTransactionSuccessContent(
                modifier = Modifier.padding(it),
                onGoToHomeClick = {}
            )
        }
    }
}
