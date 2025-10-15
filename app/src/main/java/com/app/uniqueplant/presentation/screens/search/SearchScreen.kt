package com.app.uniqueplant.presentation.screens.search

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.uniqueplant.R
import com.app.uniqueplant.presentation.model.TransactionUi
import com.app.uniqueplant.presentation.screens.categories.UiState
import com.app.uniqueplant.presentation.screens.transactionScreens.viewTransactions.DateHeader
import com.app.uniqueplant.ui.components.cards.ChipFlow
import com.app.uniqueplant.ui.components.cards.TransactionCard
import com.app.uniqueplant.ui.theme.UniquePlantTheme
import java.text.SimpleDateFormat
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
    onNavigateClicked: (TransactionUi) -> Unit,
) {
    var currentScreen by rememberSaveable { mutableStateOf(SearchScreens.RESULTS) }

    BackHandler(enabled = currentScreen == SearchScreens.FILTERS) {
        currentScreen = SearchScreens.RESULTS
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    val titleText = when (currentScreen) {
                        SearchScreens.RESULTS -> "Search Transactions"
                        SearchScreens.FILTERS -> "Filter Transactions"
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
//                                onEvent(SearchEvent.OnBackPressed)
                            }
                        }
                    ){
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
                }
            )
        }
    ) { paddingValues ->

        when(currentScreen) {
            SearchScreens.RESULTS -> {
                ResultsScreen(
                    modifier = Modifier.padding(paddingValues).padding(horizontal = 8.dp),
                    state = state,
                    onNavigateClicked = onNavigateClicked
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

    if (state.showCategoryFilterDialog) {
        MultiSelectDialog(
            items = state.allCategories,
            selectedItems = state.allCategories.filter {
                state.filterCategories?.contains(it.categoryId) == true
            },
            itemToLabel = { it.name },
            onDismissRequest = { onEvent(SearchEvent.OnDismissCategoryFilterDialog) },
            onConfirmation = { updatedList ->
                for (category in updatedList) {
                    onEvent(SearchEvent.SubmitFilterCategory(category.categoryId))
                }
                onEvent(SearchEvent.OnDismissCategoryFilterDialog)
            }
        )
    }

    if (state.showPersonFilterDialog) {
        MultiSelectDialog(
            items = state.allPersons,
            selectedItems = state.allPersons.filter {
                state.filterPersons?.contains(it.personId) == true
            },
            itemToLabel = { it.name },
            onDismissRequest = { onEvent(SearchEvent.OnDismissPersonFilterDialog) },
            onConfirmation = { updatedList ->
                for (person in updatedList) {
                    onEvent(SearchEvent.SubmitFilterPerson(person.personId))
                }
                onEvent(SearchEvent.OnDismissPersonFilterDialog)
            }
        )
    }
}


@Composable
fun ResultsScreen(
    modifier: Modifier = Modifier,
    state: SearchScreenState,
    onNavigateClicked: (TransactionUi) -> Unit,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {
        if (state.uiState is UiState.Loading) {
            CircularProgressIndicator(modifier = Modifier.padding(vertical = 16.dp))
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
                            onNavigateClicked(transactionsForDate[index])
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
fun FilterScreen(
    modifier: Modifier = Modifier,
    state: SearchScreenState,
    onEvent: (SearchEvent) -> Unit,
    onDismissRequest: () -> Unit ,
) {
    // Remember mutable states for temporary selections within the dialog
    var tempFilterType by remember { mutableStateOf(state.filterType ?: "") }

    // Use rememberUpdatedState for categories and persons
    val categories by rememberUpdatedState(
        state.allCategories.filter { state.filterCategories?.contains(it.categoryId) == true }
    )
    val persons by rememberUpdatedState(
        state.allPersons.filter { state.filterPersons?.contains(it.personId) == true }
    )

    // Add states for start and end date if you have date pickers
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // Main content area that scrolls
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Filter by Type:", style = MaterialTheme.typography.titleMedium)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState(0)),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = tempFilterType == "",
                    onClick = { tempFilterType = "" },
                    label = { Text("All") }
                )
                FilterChip(
                    selected = tempFilterType == "Income",
                    onClick = { tempFilterType = "Income" },
                    label = { Text("Income") }
                )
                FilterChip(
                    selected = tempFilterType == "Expense",
                    onClick = { tempFilterType = "Expense" },
                    label = { Text("Expense") }
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 4.dp)
            )


            //Show Selected Categories as Chips
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
            )
            {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                )
                {
                    Text(
                        "Categories",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = {
                            onEvent(SearchEvent.OnShowCategoryFilterDialog)
                        }
                    ) {
                        Icon(
                            painterResource(id = R.drawable.ic_keyboard_arrow_right_24),
                            contentDescription = "Select Categories"
                        )
                    }
                }
                ChipFlow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    placeholder = "No categories selected.",
                    chips = categories,
                    maxLines = 1,
                    onChipClick = { category ->
                        //Remove Category on click if already selected
                        onEvent(SearchEvent.SubmitFilterCategory(category.categoryId))
                    },
                    chipToLabel = { it.name }
                )

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            //Show Selected Persons as Chips
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                )
                {
                    Text(
                        "Persons",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = {
                            onEvent(SearchEvent.OnShowPersonFilterDialog)
                        }
                    )
                    {
                        Icon(
                            painterResource(id = R.drawable.ic_keyboard_arrow_right_24),
                            contentDescription = "Select Persons"
                        )
                    }
                }
                ChipFlow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    placeholder = "No persons selected.",
                    chips = persons,
                    maxLines = 1,
                    onChipClick = { person ->
                        //Remove Person on click if already selected
                        onEvent(SearchEvent.SubmitFilterPerson(person.personId))
                    },
                    chipToLabel = { it.name }
                )

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            Text("Date Range", style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = "Start Date",
                onValueChange = {},
                label = { Text("Start Date") })
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = "End Date",
                onValueChange = {},
                label = { Text("End Date") })


        }
        // Sticky bottom action buttons
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
                    onEvent(SearchEvent.UpdateFilterType(tempFilterType.ifEmpty { null }))
                    // onEvent(SearchEvent.UpdateFilterDateRange(startDate, endDate)) // Get values from date pickers
                    onEvent(SearchEvent.ApplyFilters)
                    onDismissRequest()
                }) {
                Text("Apply")
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun SearchScreenPreview() {
    UniquePlantTheme { // Ensure a MaterialTheme is applied for previews
        SearchScreen(
            state = SearchScreenState(),
            onEvent = {},
            onNavigateClicked = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun FilterDialogPreview() {
    UniquePlantTheme {
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

