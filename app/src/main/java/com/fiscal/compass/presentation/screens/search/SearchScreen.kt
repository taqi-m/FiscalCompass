package com.fiscal.compass.presentation.screens.search

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.fiscal.compass.R
import com.fiscal.compass.domain.model.Transaction
import com.fiscal.compass.domain.util.DateRange
import com.fiscal.compass.domain.util.TransactionType
import com.fiscal.compass.presentation.navigation.MainScreens
import com.fiscal.compass.presentation.screens.category.UiState
import com.fiscal.compass.presentation.screens.itemselection.SelectableItem
import com.fiscal.compass.ui.components.cards.ChipFlow
import com.fiscal.compass.ui.components.cards.TransactionCard
import com.fiscal.compass.ui.components.pickers.DatePicker
import com.fiscal.compass.ui.theme.FiscalCompassTheme
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


private enum class SearchScreens {
    RESULTS,
    FILTERS
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    state: SearchScreenState,
    onEvent: (SearchEvent) -> Unit,
    appNavController: NavHostController,
) {
    var currentScreen by rememberSaveable { mutableStateOf(SearchScreens.RESULTS) }

    // Handle navigation to category selection
    LaunchedEffect(state.navigateToCategorySelection) {
        if (state.navigateToCategorySelection) {
            val allSelectableItems = state.allCategories.map { category ->
                SelectableItem(
                    id = category.categoryId.toString(),
                    name = category.name,
                    isSelected = state.searchCriteria.getCategoryIds().contains(category.categoryId)
                )
            }
            appNavController.navigate(
                MainScreens.MultiSelection.passParameters(
                    Uri.encode(
                        Gson().toJson(
                            allSelectableItems
                        )
                    ), "category", "selectedIds"
                )
            )
            onEvent(SearchEvent.ResetNavigation)
        }
    }

    // Handle navigation to person selection
    LaunchedEffect(state.navigateToPersonSelection) {
        if (state.navigateToPersonSelection) {
            val allSelectableItems = state.allPersons.map { person ->
                SelectableItem(
                    id = person.personId.toString(),
                    name = person.name,
                    isSelected = state.searchCriteria.getPersonIds().contains(person.personId)
                )
            }
            appNavController.navigate(
                MainScreens.MultiSelection.passParameters(
                    Gson().toJson(
                        allSelectableItems
                    ), "person", "selectedPersonIds"
                )
            )
            onEvent(SearchEvent.ResetNavigation)
        }
    }

    // Get selected category IDs from navigation result
    LaunchedEffect(appNavController.currentBackStackEntry) {
        appNavController.currentBackStackEntry
            ?.savedStateHandle
            ?.get<String>("selectedIds")
            ?.let { idsString ->
                val ids = if (idsString.isEmpty()) emptyList()
                else idsString.split(",").map { it.toLong() }
                onEvent(SearchEvent.UpdateSelectedCategories(ids))
                appNavController.currentBackStackEntry?.savedStateHandle?.remove<String>("selectedIds")
            }
    }

    // Get selected person IDs from navigation result
    LaunchedEffect(appNavController.currentBackStackEntry) {
        appNavController.currentBackStackEntry
            ?.savedStateHandle
            ?.get<String>("selectedPersonIds")
            ?.let { idsString ->
                val ids = if (idsString.isEmpty()) emptyList()
                else idsString.split(",").map { it.toLong() }
                onEvent(SearchEvent.UpdateSelectedPersons(ids))
                appNavController.currentBackStackEntry?.savedStateHandle?.remove<String>("selectedPersonIds")
            }
    }

    BackHandler(enabled = currentScreen == SearchScreens.FILTERS) {
        currentScreen = SearchScreens.RESULTS
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    val titleText = when (currentScreen) {
                        SearchScreens.RESULTS -> "Transaction History"
                        SearchScreens.FILTERS -> "Filters"
                    }
                    Text(
                        text = titleText
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (currentScreen == SearchScreens.FILTERS) {
                                currentScreen = SearchScreens.RESULTS
                            } else {
                                appNavController.popBackStack()
                            }
                        }
                    ) {
                        Icon(
                            painterResource(id = R.drawable.ic_arrow_back_24),
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    if (currentScreen == SearchScreens.RESULTS) {
                        IconButton(onClick = {
                            currentScreen = SearchScreens.FILTERS
                        }) {
                            Icon(
                                painterResource(R.drawable.ic_filter_list_24),
                                contentDescription = "Filter Transactions"
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->

        when (currentScreen) {
            SearchScreens.RESULTS -> {
                ResultsScreen(
                    modifier = Modifier
                        .padding(paddingValues)
                        .padding(horizontal = 8.dp),
                    state = state,
                    onTransactionSelected = {
                        val transaction = Uri.encode(Gson().toJson(it))
                        appNavController.navigate(
                            MainScreens.TransactionDetail.passTransaction(
                                transaction
                            )
                        )
                    }
                )
            }

            SearchScreens.FILTERS -> {
                FilterScreen(
                    modifier = Modifier.padding(paddingValues),
                    state = state,
                    onEvent = onEvent,
                    onDismissRequest = {
                        currentScreen = SearchScreens.RESULTS
                    }
                )
            }
        }
    }

}


@Composable
fun ResultsScreen(
    modifier: Modifier = Modifier,
    state: SearchScreenState,
    onTransactionSelected: (Transaction) -> Unit,
) {
    val searchCriteria = state.searchCriteria
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (state.uiState is UiState.Loading) {
            Box(Modifier.fillMaxSize()){ CircularProgressIndicator(modifier = Modifier.padding(vertical = 16.dp)) }
        } else if (state.searchCriteria.areAnyFiltersActive()) {
            // Create a list of active filter strings
            val activeFilters = mutableListOf<String>()
            state.searchCriteria.getTransactionType()?.let { activeFilters.add(it.name) }

            val selectedCategoryNames = state.searchCriteria.getCategories()?.map { it.name } ?: emptyList()
            activeFilters.addAll(selectedCategoryNames)

            val selectedPersonNames = state.searchCriteria.getPersons()?.map { it.name } ?: emptyList()
            activeFilters.addAll(selectedPersonNames)

            val dateFormatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            state.searchCriteria.getDateRange()?.startDate?.let {
                activeFilters.add("From: ${dateFormatter.format(Date(it))}")
            }
            state.searchCriteria.getDateRange()?.endDate?.let {
                activeFilters.add("To: ${dateFormatter.format(Date(it))}")
            }

            if (activeFilters.isNotEmpty()) {
                ChipFlow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .horizontalScroll(rememberScrollState()),
                    chips = activeFilters,
                    maxLines = 1,
                    onChipClick = {},
                    chipToLabel = { it }
                )
            }
        }

        if (state.uiState is UiState.Error) {
            Text(
                "Error loading data",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(vertical = 16.dp)
            )
        }

        if (state.uiState !is UiState.Loading && state.searchResults.isEmpty()) {
            Text("No results found.", modifier = Modifier.padding(vertical = 16.dp))
        }

        val transactions = state.searchResults


/*        TransactionHeading(
            currentBalance = "0",
            incoming = "0",
            outgoing = "0"
        )*/



        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Display transactions grouped by date
            transactions.forEach { (date, transactionsForDate) ->
                stickyHeader {
                    // Convert date to string if it's a Date object
                    val formattedDate =
                        SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(date)
                    DateHeader(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.background)
                            .padding(vertical = 8.dp),
                        date = formattedDate
                    )
                }

                items(transactionsForDate.size) { index ->
                    TransactionCard(
                        transaction = transactionsForDate[index],
                        onClicked = {
                            onTransactionSelected(transactionsForDate[index])
                        },
                        onEditClicked = {},
                        onDeleteClicked = {},
                    )
                }
            }
        }
    }
}

@Composable
fun TransactionHeading(
    modifier: Modifier = Modifier, currentBalance: String, incoming: String, outgoing: String
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors().copy(
                containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
            )
        ) {
            val textColor = MaterialTheme.colorScheme.onSecondaryContainer
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Total Balance",
                    style = MaterialTheme.typography.labelLarge,
                    color = textColor
                )
                Text(
                    text = currentBalance,
                    style = MaterialTheme.typography.headlineMedium,
                    color = textColor
                )
                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant
                )
                Row {
                    BalanceView(
                        label = "Incoming",
                        amount = incoming,
                        modifier = Modifier
                            .weight(1f)
                            .padding(8.dp),
                        textAlign = TextAlign.Start
                    )
                    BalanceView(
                        label = "Outgoing",
                        amount = outgoing,
                        modifier = Modifier
                            .weight(1f)
                            .padding(8.dp),
                        textAlign = TextAlign.End
                    )
                }
            }
        }
    }
}


@Composable
private fun BalanceView(
    label: String,
    amount: String,
    modifier: Modifier = Modifier,
    textAlign: TextAlign = TextAlign.Start
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = label,
            style = MaterialTheme.typography.labelLarge,
            textAlign = textAlign
        )
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = amount,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = textAlign
        )
    }
}

@Composable
fun FilterScreen(
    modifier: Modifier = Modifier,
    state: SearchScreenState,
    onEvent: (SearchEvent) -> Unit,
    onDismissRequest: () -> Unit,
) {
    var tempFilterType by remember {
        mutableStateOf(state.searchCriteria.getTransactionType()?.name ?: "")
    }

    val selectedCategories = state.searchCriteria.getCategories() ?: emptyList()
    val selectedPersons = state.searchCriteria.getPersons() ?: emptyList()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Type:", style = MaterialTheme.typography.titleMedium)
            SingleChoiceSegmentedButtonRow(
                modifier = Modifier.fillMaxWidth()
            ) {
                SegmentedButton(
                    selected = tempFilterType == "",
                    onClick = {
                        tempFilterType = ""
                        onEvent(SearchEvent.UpdateFilterType(null))
                    },
                    shape = SegmentedButtonDefaults.itemShape(index = 0, count = 3)
                ) {
                    Text("All")
                }
                SegmentedButton(
                    selected = tempFilterType == "INCOME",
                    onClick = {
                        tempFilterType = "INCOME"
                        onEvent(SearchEvent.UpdateFilterType(TransactionType.INCOME))
                    },
                    shape = SegmentedButtonDefaults.itemShape(index = 1, count = 3)
                ) {
                    Text("Income")
                }
                SegmentedButton(
                    selected = tempFilterType == "EXPENSE",
                    onClick = {
                        tempFilterType = "EXPENSE"
                        onEvent(SearchEvent.UpdateFilterType(TransactionType.EXPENSE))
                    },
                    shape = SegmentedButtonDefaults.itemShape(index = 2, count = 3)
                ) {
                    Text("Expense")
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

            // Categories Selection Card
            Text("Categories", style = MaterialTheme.typography.titleMedium)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.small)
                    .clickable { onEvent(SearchEvent.NavigateToCategorySelection) },
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f),
                    contentColor = MaterialTheme.colorScheme.onSurface
                ),
                border = CardDefaults.outlinedCardBorder(),
                shape = MaterialTheme.shapes.small
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (selectedCategories.isEmpty()) "None selected"
                            else "${selectedCategories.size} selected",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = "Select categories",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    if (selectedCategories.isNotEmpty()) {
                        ChipFlow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            placeholder = "",
                            maxLines = 1,
                            chips = selectedCategories,
                            onChipClick = {},
                            chipToLabel = {
                                it.name
                            }
                        )
                    }
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

            // Persons Selection Card
            Text("Persons", style = MaterialTheme.typography.titleMedium)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.small)
                    .clickable { onEvent(SearchEvent.NavigateToPersonSelection) },
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f),
                    contentColor = MaterialTheme.colorScheme.onSurface
                ),
                border = CardDefaults.outlinedCardBorder(),
                shape = MaterialTheme.shapes.small
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (selectedPersons.isEmpty()) "None selected"
                            else "${selectedPersons.size} selected",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = "Select persons",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    if (selectedPersons.isNotEmpty()) {
                        ChipFlow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            placeholder = "",
                            maxLines = 1,
                            chips = selectedPersons,
                            onChipClick = {},
                            chipToLabel = {
                                it.name
                            }
                        )
                    }
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

            Text("Date Range", style = MaterialTheme.typography.titleMedium)
            DatePicker(
                modifier = Modifier.fillMaxWidth(),
                label = "Start Date",
                selectedDate = state.searchCriteria.getDateRange()?.startDate,
                onDateSelected = { date ->
                    onEvent(SearchEvent.StartDateSelected(date))
                }
            )
            DatePicker(
                modifier = Modifier.fillMaxWidth(),
                label = "End Date",
                selectedDate = state.searchCriteria.getDateRange()?.endDate,
                onDateSelected = { date ->
                    onEvent(SearchEvent.EndDateSelected(date))
                }
            )
        }

        Column(modifier = Modifier.padding(bottom = 16.dp)) {
            OutlinedButton(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth(),
                shape = MaterialTheme.shapes.small,
                onClick = {
                    tempFilterType = ""
                    onEvent(SearchEvent.ClearFilters)
                }) {
                Text("Clear Filters")
            }
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                shape = MaterialTheme.shapes.medium,
                onClick = {
                    onEvent(SearchEvent.ApplyFilters)
                    onDismissRequest()
                }) {
                Text("Apply")
            }
        }
    }
}


@Composable
fun DateHeader(modifier: Modifier = Modifier, date: String) {
    Card(
        modifier = modifier, colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ), shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = date,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}


@Preview(showBackground = true)
@Composable
fun SearchScreenPreview() {
    FiscalCompassTheme { // Ensure a MaterialTheme is applied for previews
        SearchScreen(
            state = SearchScreenState(
                searchResults = mapOf(
                    Date(System.currentTimeMillis()) to Transaction.sampleList()
                ),
                searchCriteria = com.fiscal.compass.domain.util.SearchCriteria().apply {
                    setTransactionType(TransactionType.INCOME)
                    setDateRange(DateRange(System.currentTimeMillis(), null))
                }
            ),
            onEvent = {},
            appNavController = rememberNavController()
        )
    }
}

@Preview(showBackground = true)
@Composable
fun FilterScreenPreview() {

    FiscalCompassTheme {
        Surface {
            FilterScreen(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                state = SearchScreenState(),
                onEvent = {},
                onDismissRequest = {}
            )
        }

    }
}

