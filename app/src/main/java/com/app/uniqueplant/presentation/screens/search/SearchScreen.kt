package com.app.uniqueplant.presentation.screens.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
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
import com.app.uniqueplant.presentation.model.TransactionUi
import com.app.uniqueplant.presentation.screens.categories.UiState
import com.app.uniqueplant.presentation.screens.transactionScreens.viewTransactions.DateHeader
import com.app.uniqueplant.ui.components.cards.TransactionCard
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    state: SearchScreenState,
    onEvent: (SearchEvent) -> Unit,
    onNavigateClicked: (TransactionUi) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Search Transactions"
                    )
                },
                actions = {
                    IconButton(onClick = { onEvent(SearchEvent.OnFilterIconClicked) }) {
                        Icon(painterResource(R.drawable.ic_filter_list_24), contentDescription = "Filter Transactions")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (state.uiState is UiState.Loading) {
                CircularProgressIndicator(modifier = Modifier.padding(vertical = 16.dp))
            }

            if (state.uiState is UiState.Error) {
                Text("Error loading data", color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(vertical = 16.dp))
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

    if (state.showFilterDialog) {
        FilterDialog(state = state, onEvent = onEvent)
    }
}

@Composable
fun FilterDialog(
    state: SearchScreenState,
    onEvent: (SearchEvent) -> Unit
) {
    // Remember mutable states for temporary selections within the dialog
    var tempFilterType by remember { mutableStateOf(state.filterType ?: "") }
    var tempFilterCategories by remember { mutableStateOf(state.filterCategories) }
    // Add states for start and end date if you have date pickers

    AlertDialog(
        onDismissRequest = { onEvent(SearchEvent.OnDismissFilterDialog) },
        title = { Text("Filter Transactions") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Filter by Type:", style = MaterialTheme.typography.titleMedium)
                Row {
                    RadioButton(
                        selected = tempFilterType == "Income",
                        onClick = { tempFilterType = "Income" }
                    )
                    Text("Income", Modifier.padding(start = 4.dp).align(Alignment.CenterVertically))
                    Spacer(Modifier.width(8.dp))
                    RadioButton(
                        selected = tempFilterType == "Expense",
                        onClick = { tempFilterType = "Expense" }
                    )
                    Text("Expense", Modifier.padding(start = 4.dp).align(Alignment.CenterVertically))
                     Spacer(Modifier.width(8.dp))
                    RadioButton(
                        selected = tempFilterType == "",
                        onClick = { tempFilterType = "" }
                    )
                    Text("Any", Modifier.padding(start = 4.dp).align(Alignment.CenterVertically))
                }

                // TODO: Add Date Range Pickers here
                Text("Date Range: (Placeholder)", style = MaterialTheme.typography.titleSmall)
                // For example:
                // OutlinedTextField(value = "Start Date", onValueChange = {}, label = { Text("Start Date") })
                // OutlinedTextField(value = "End Date", onValueChange = {}, label = { Text("End Date") })

                Button(onClick = {
                    tempFilterType = ""
                    // Clear date picker states
                    onEvent(SearchEvent.ClearFilters) // This will also dismiss the dialog from ViewModel if needed
                }) {
                    Text("Clear Filters")
                }

            }
        },
        confirmButton = {
            Button(onClick = {
                onEvent(SearchEvent.UpdateFilterType(tempFilterType.ifEmpty { null }))
                // onEvent(SearchEvent.UpdateFilterDateRange(startDate, endDate)) // Get values from date pickers
                onEvent(SearchEvent.ApplyFilters)
            }) {
                Text("Apply")
            }
        },
        dismissButton = {
            Button(onClick = { onEvent(SearchEvent.OnDismissFilterDialog) }) {
                Text("Cancel")
            }
        }
    )
}


@Preview(showBackground = true)
@Composable
fun SearchScreenPreview() {
    MaterialTheme { // Ensure a MaterialTheme is applied for previews
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
    MaterialTheme {
        FilterDialog(
            state = SearchScreenState(),
            onEvent = {}
        )
    }
}
