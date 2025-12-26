package com.fiscal.compass.presentation.screens.transactionScreens.addTransaction

import android.annotation.SuppressLint
import android.net.Uri
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import com.fiscal.compass.domain.util.TransactionType
import com.fiscal.compass.presentation.navigation.MainScreens
import com.fiscal.compass.presentation.screens.category.UiState
import com.fiscal.compass.presentation.screens.itemselection.SelectableItem
import com.fiscal.compass.ui.components.input.DataEntryTextField
import com.fiscal.compass.ui.components.input.TypeSwitch
import com.fiscal.compass.ui.components.pickers.DatePicker
import com.fiscal.compass.ui.components.pickers.TimePicker
import com.fiscal.compass.ui.theme.FiscalCompassTheme
import com.google.gson.Gson
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import java.util.Calendar


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    appNavController: NavHostController,
    state: AddTransactionState,
    onEvent: (AddTransactionEvent) -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val uiState = state.uiState

    // Handle navigation to category selection
    LaunchedEffect(state.navigateToCategorySelection) {
        if (state.navigateToCategorySelection) {
            val allSelectableItems = state.allCategories.map { category ->
                SelectableItem(
                    id = category.categoryId.toString(),
                    name = category.name,
                    isSelected = category.categoryId == state.transaction.categoryId
                )
            }
            val itemsJson = Gson().toJson(allSelectableItems)
            val itemsEncoded = Uri.encode(itemsJson)
            appNavController.navigate(
                MainScreens.MultiSelection.passParameters(
                    itemsEncoded,
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
        if (!state.navigateToPersonSelection) {
            return@LaunchedEffect
        }
        // Add "N/A" option for persons
        val naOption = SelectableItem(
            id = "-1",
            name = "N/A",
            isSelected = state.transaction.personId == null
        )
        val personSelectableItems = state.allPersons.map { person ->
            SelectableItem(
                id = person.personId.toString(),
                name = person.name,
                isSelected = person.personId == state.transaction.personId
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

    // Handle navigation to amount screen
    LaunchedEffect(state.navigateToAmountScreen) {
        if (!state.navigateToAmountScreen) {
            return@LaunchedEffect
        }
        val jsonTransaction = Gson().toJson(state.transaction)
        val encodedTransaction = Uri.encode(jsonTransaction)
        appNavController.navigate(MainScreens.Amount.passTransaction(encodedTransaction))
        onEvent(AddTransactionEvent.ResetNavigation)
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

    var titleText by remember { mutableStateOf("Add Transaction") }


    LaunchedEffect(uiState) {
        val message: String? = when (uiState) {
            is UiState.Error -> uiState.message
            else -> null
        }
        message?.let {
            snackbarHostState.showSnackbar(
                message = it,
                duration = SnackbarDuration.Short,
                actionLabel = "Dismiss"
            )
            onEvent(AddTransactionEvent.OnUiReset)
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
                    Text(text = titleText)
                },
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
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                AddTransactionFormContent(
                    modifier = Modifier.padding(paddingValues),
                    state = state,
                    onEvent = onEvent,
                    onNextClick = {
                        onEvent(AddTransactionEvent.NavigateToAmountScreen)
                    }
                )
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
    // Derive selected category directly from state - no delay
    val selectedCategory = remember(state.transaction.categoryId, state.allCategories) {
        state.allCategories.firstOrNull { it.categoryId == state.transaction.categoryId }
    }

    // Derive selected person directly from state - no delay
    val selectedPerson = remember(state.transaction.personId, state.allPersons) {
        state.allPersons.firstOrNull { it.personId == state.transaction.personId }
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
            val selectedIndex =
                TransactionType.entries.indexOf(TransactionType.fromString(state.transaction.transactionType))
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

            SelectionField(
                modifier = Modifier.fillMaxWidth(),
                label = "Category",
                selectedValue = selectedCategory?.name ?: "Select Category",
                onClick = { onEvent(AddTransactionEvent.NavigateToCategorySelection) }
            )

            SelectionField(
                modifier = Modifier.fillMaxWidth(),
                label = "Person",
                selectedValue = selectedPerson?.name ?: "Select Person",
                onClick = { onEvent(AddTransactionEvent.NavigateToPersonSelection) }
            )


            DatePicker(
                modifier = Modifier.fillMaxWidth(),
                label = "Date",
                selectedDate = state.transaction.date.time,
                onDateSelected = { date ->
                    onEvent(AddTransactionEvent.DateSelected(date))
                }
            )

            TimePicker(
                modifier = Modifier.fillMaxWidth(),
                label = "Time",
                selectedTime = Calendar.getInstance()
                    .apply { timeInMillis = state.transaction.date.time },
                onTimeSelected = { time ->
                    onEvent(AddTransactionEvent.TimeSelected(time))
                }
            )

            DataEntryTextField(
                modifier = Modifier
                    .fillMaxWidth(),
                label = "Note",
                placeholder = "Add a short note",
                value = state.transaction.description ?: "",
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
                state = AddTransactionState(),
                onEvent = {},
                onNextClick = {}
            )
        }
    }
}

/*@Preview(
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
}*/

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
