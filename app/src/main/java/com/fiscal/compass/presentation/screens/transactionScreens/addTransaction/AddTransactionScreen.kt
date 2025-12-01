package com.fiscal.compass.presentation.screens.transactionScreens.addTransaction

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
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
import com.fiscal.compass.R
import com.fiscal.compass.presentation.model.CategoryUi
import com.fiscal.compass.presentation.model.InputField
import com.fiscal.compass.presentation.model.PersonUi
import com.fiscal.compass.presentation.model.TransactionType
import com.fiscal.compass.presentation.navigation.MainScreens
import com.fiscal.compass.presentation.screens.category.UiState
import com.fiscal.compass.presentation.screens.itemselection.SelectableItem
import com.fiscal.compass.presentation.utilities.CurrencyFormater
import com.fiscal.compass.presentation.utils.AmountInputType
import com.fiscal.compass.ui.components.input.Calculator
import com.fiscal.compass.ui.components.input.DataEntryTextField
import com.fiscal.compass.ui.components.input.TypeSwitch
import com.fiscal.compass.ui.components.pickers.DatePicker
import com.fiscal.compass.ui.components.pickers.TimePicker
import com.fiscal.compass.ui.theme.FiscalCompassTheme
import com.google.gson.Gson
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest

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

    // Handle navigation to category selection
    LaunchedEffect(state.navigateToCategorySelection) {
        if (state.navigateToCategorySelection) {
            val allSelectableItems = state.allCategories.map { category ->
                SelectableItem(
                    id = category.categoryId.toString(),
                    name = category.name,
                    isSelected = category.categoryId == state.categoryId
                )
            }
            appNavController.navigate(
                MainScreens.MultiSelection.passParameters(
                    Uri.encode(Gson().toJson(allSelectableItems)),
                    "category",
                    "selectedCategoryId",
                    singleSelectionMode = true
                )
            )
            onEvent(AddTransactionEvent.ResetNavigation)
        }
    }

    // Handle navigation to person selection
    LaunchedEffect(state.navigateToPersonSelection) {
        if (state.navigateToPersonSelection) {
            // Add "N/A" option for persons
            val naOption = SelectableItem(
                id = "-1",
                name = "N/A",
                isSelected = state.personId == null
            )
            val personSelectableItems = state.allPersons.map { person ->
                SelectableItem(
                    id = person.personId.toString(),
                    name = person.name,
                    isSelected = person.personId == state.personId
                )
            }
            val allSelectableItems = listOf(naOption) + personSelectableItems

            appNavController.navigate(
                MainScreens.MultiSelection.passParameters(
                    Uri.encode(Gson().toJson(allSelectableItems)),
                    "person",
                    "selectedPersonId",
                    singleSelectionMode = true
                )
            )
            onEvent(AddTransactionEvent.ResetNavigation)
        }
    }

    // Get selected category ID from navigation result
    LaunchedEffect(appNavController.currentBackStackEntry) {
        appNavController.currentBackStackEntry
            ?.savedStateHandle
            ?.get<String>("selectedCategoryId")
            ?.let { idsString ->
                if (idsString.isNotEmpty()) {
                    // For single selection, we take the first (and only) ID
                    val categoryId = idsString.split(",").firstOrNull()?.toLongOrNull()
                    categoryId?.let {
                        onEvent(AddTransactionEvent.UpdateSelectedCategory(it))
                    }
                }
                appNavController.currentBackStackEntry?.savedStateHandle?.remove<String>("selectedCategoryId")
            }
    }

    // Get selected person ID from navigation result
    LaunchedEffect(appNavController.currentBackStackEntry) {
        appNavController.currentBackStackEntry
            ?.savedStateHandle
            ?.get<String>("selectedPersonId")
            ?.let { idsString ->
                if (idsString.isNotEmpty()) {
                    // For single selection, we take the first (and only) ID
                    val personId = idsString.split(",").firstOrNull()?.toLongOrNull()
                    if (personId == -1L) {
                        onEvent(AddTransactionEvent.UpdateSelectedPerson(null))
                    } else {
                        personId?.let {
                            onEvent(AddTransactionEvent.UpdateSelectedPerson(it))
                        }
                    }
                }
                appNavController.currentBackStackEntry?.savedStateHandle?.remove<String>("selectedPersonId")
            }
    }

    BackHandler(enabled = currentScreen == AddTransactionScreen.CALCULATOR || currentScreen == AddTransactionScreen.SUCCESS) {
        currentScreen = AddTransactionScreen.FORM
    }

    var titleText by remember { mutableStateOf("Add Transaction") }


    LaunchedEffect(uiState) {
        val message: String? = when (uiState) {
            is UiState.Error -> uiState.message
            is UiState.Success -> {
                currentScreen = AddTransactionScreen.SUCCESS
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
                            state = state
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
                val allCategories = state.categories
                val selectedCategory = allCategories.firstOrNull { it.categoryId == state.categoryId }

                SelectionField(
                    modifier = Modifier.fillMaxWidth(),
                    label = "Category",
                    selectedValue = selectedCategory?.name ?: "Select Category",
                    onClick = { onEvent(AddTransactionEvent.NavigateToCategorySelection) }
                )
            }

            val selectedPerson = state.persons.firstOrNull { it.personId == state.personId }
            val personDisplayName = selectedPerson?.name ?: "N/A"

            SelectionField(
                modifier = Modifier.fillMaxWidth(),
                label = "Person",
                selectedValue = personDisplayName,
                onClick = { onEvent(AddTransactionEvent.NavigateToPersonSelection) }
            )


            DatePicker(
                modifier = Modifier.fillMaxWidth(),
                label = "Date",
                selectedDate = state.selectedDate,
                onDateSelected = { date ->
                    onEvent(AddTransactionEvent.DateSelected(date))
                }
            )

            TimePicker(
                modifier = Modifier.fillMaxWidth(),
                label = "Time",
                selectedTime = state.selectedTime,
                onTimeSelected = { time ->
                    onEvent(AddTransactionEvent.TimeSelected(time))
                }
            )

            DataEntryTextField(
                modifier = Modifier
                    .fillMaxWidth(),
                label = "Note",
                placeholder = "Add a short note",
                value = state.description.value,
                onValueChange = { onEvent(AddTransactionEvent.OnDescriptionChange(it)) },
                maxLines = 4,
                singleLine = false,
                minLines = 4
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
    onEvent: (AddTransactionEvent) -> Unit,
    state: AddTransactionState
) {
    // Persistent field selection - remembers last active field
    var activeField by rememberSaveable { mutableStateOf<AmountField>(AmountField.TOTAL_AMOUNT) }

    // Calculate remaining amount and progress
    val totalAmount = state.totalAmount.value.toDoubleOrNull() ?: 0.0
    val paidAmount = state.paidAmount.value.toDoubleOrNull() ?: 0.0
    val remainingAmount = (totalAmount - paidAmount).coerceAtLeast(0.0)
    val progressPercentage = if (totalAmount > 0) {
        ((paidAmount / totalAmount) * 100).coerceIn(0.0, 100.0)
    } else {
        0.0
    }

    // Get the current value to pass to Calculator based on active field
    val currentDisplayValue = when (activeField) {
        AmountField.TOTAL_AMOUNT -> state.totalAmount.value.ifBlank { "0" }
        AmountField.AMOUNT_PAID -> state.paidAmount.value.ifBlank { "0" }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp)
            .padding(bottom = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Payment Progress Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Payment Progress",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${progressPercentage.toInt()}%",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                        color = when {
                            progressPercentage >= 100 -> MaterialTheme.colorScheme.primary
                            progressPercentage > 0 -> MaterialTheme.colorScheme.tertiary
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }

                // Progress indicator
                LinearProgressIndicator(
                    progress = { (progressPercentage / 100).toFloat() },
                    modifier = Modifier.fillMaxWidth(),
                    color = when {
                        progressPercentage >= 100 -> MaterialTheme.colorScheme.primary
                        progressPercentage > 50 -> MaterialTheme.colorScheme.tertiary
                        else -> MaterialTheme.colorScheme.secondary
                    },
                )

                // Remaining amount display
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Remaining:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = CurrencyFormater.formatCurrency(remainingAmount),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                        color = if (remainingAmount > 0)
                            MaterialTheme.colorScheme.error
                        else
                            MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        // Field selector tabs with auto-fill button
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Total Amount Card
            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
                onClick = { activeField = AmountField.TOTAL_AMOUNT },
                shape = MaterialTheme.shapes.small,
                border = BorderStroke(
                    width = if (activeField == AmountField.TOTAL_AMOUNT) 2.dp else 1.dp,
                    color = if (activeField == AmountField.TOTAL_AMOUNT)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.outline
                )
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Total Amount",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (activeField == AmountField.TOTAL_AMOUNT)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = CurrencyFormater.formatCalculatorCurrency(state.totalAmount.value.ifBlank { "0" }),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                        color = if (activeField == AmountField.TOTAL_AMOUNT)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // Amount Paid Card
            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
                onClick = { activeField = AmountField.AMOUNT_PAID },
                shape = MaterialTheme.shapes.small,
                border = BorderStroke(
                    width = if (activeField == AmountField.AMOUNT_PAID) 2.dp else 1.dp,
                    color = if (activeField == AmountField.AMOUNT_PAID)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.outline
                )
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Amount Paid",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (activeField == AmountField.AMOUNT_PAID)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = CurrencyFormater.formatCalculatorCurrency(state.paidAmount.value.ifBlank { "0" }),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                        color = if (activeField == AmountField.AMOUNT_PAID)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        // Auto-fill button for full payment
        if (totalAmount > 0 && paidAmount < totalAmount) {
            Button(
                onClick = {
                    onEvent(AddTransactionEvent.OnAmountPaidChange(state.totalAmount.value))
                    activeField = AmountField.AMOUNT_PAID
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(8.dp),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                )
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_check_24),
                    contentDescription = "Mark as Fully Paid",
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = "Mark as Fully Paid",
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }

        // Calculator with direct value binding - receives initialValue from state
        // No TextField inside Calculator - values come from parent state
        Calculator(
            modifier = Modifier.fillMaxSize(),
            initialValue = currentDisplayValue,
            label = when (activeField) {
                AmountField.TOTAL_AMOUNT -> "Total Amount"
                AmountField.AMOUNT_PAID -> "Amount Paid"
            },
            inputType = when (activeField) {
                AmountField.TOTAL_AMOUNT -> AmountInputType.TOTAL_AMOUNT
                AmountField.AMOUNT_PAID -> AmountInputType.AMOUNT_PAID
            },
            onValueChange = { value, inputType ->
                Log.d("AddTransactionScreen", "onValueChange: $value, $inputType")
                // Update the appropriate field based on inputType
                when (inputType) {
                    AmountInputType.TOTAL_AMOUNT -> onEvent(AddTransactionEvent.OnAmountChange(value, inputType))
                    AmountInputType.AMOUNT_PAID -> onEvent(AddTransactionEvent.OnAmountPaidChange(value))
                }
            },
            onSaveClick = {
                onEvent(AddTransactionEvent.OnSaveClicked)
            },
        )
    }
}

// Enum for field selection
private enum class AmountField {
    TOTAL_AMOUNT,
    AMOUNT_PAID
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
    name = "Form Screen - Pixel 7a",
    device = "id:pixel_7a"
)
//@Preview(showSystemUi = true, showBackground = true, name = "Form Screen - Nexus 7", device = Devices.NEXUS_7)
@Composable
fun AddTransactionFormContentPreview() {
    FiscalCompassTheme {
        Scaffold {
            AddTransactionFormContent(
                modifier = Modifier.padding(it),
                state = AddTransactionState(
                    totalAmount = InputField(
                        value = "",
                        error = ""
                    ),
                    description = InputField(
                        value = "",
                        error = ""
                    ),
                    personId = 1L,
                    categoryId = 1L,
                    selectedDate = System.currentTimeMillis(),
                    selectedTime = java.util.Calendar.getInstance(),
                    categories = CategoryUi.dummyList,
                    persons = listOf(
                        PersonUi(
                            personId = 1L,
                            name = "Alice",
                            personType = "DEALER"
                        )
                    ),
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
    name = "Form Screen - Pixel 7a",
    device = "id:pixel_7a"
)
//@Preview(showSystemUi = true, showBackground = true, name = "Calculator Screen - Nexus 7", device = Devices.NEXUS_7)
@Composable
fun AddTransactionCalculatorContentPreview() {
    FiscalCompassTheme {
        Scaffold {
            AddTransactionCalculatorContent(
                modifier = Modifier.padding(it),
                onEvent = {},
                state = AddTransactionState(
                    totalAmount = InputField(value = "1000.0"),
                    paidAmount = InputField(value = "600.0"),
                    categoryId = 1L
                )
            )
        }
    }
}

@Preview(
    showSystemUi = true,
    showBackground = true,
    name = "Form Screen - Pixel 7a",
    device = "id:pixel_7a"
)
@Composable
fun AddTransactionSuccessContentPreview() {
    FiscalCompassTheme {
        Scaffold {
            AddTransactionSuccessContent(
                modifier = Modifier.padding(it),
                onGoToHomeClick = {}
            )
        }
    }
}

/**
 * A clickable field that displays a label and selected value, navigating to a selection screen when clicked.
 * Similar to OutlinedTextField but for navigation-based selection.
 */
@Composable
private fun SelectionField(
    modifier: Modifier = Modifier,
    label: String,
    selectedValue: String,
    onClick: () -> Unit
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 16.dp, bottom = 4.dp)
        )
        OutlinedCard(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick),
            shape = MaterialTheme.shapes.extraSmall
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = selectedValue,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Select $label",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
