package com.fiscal.compass.presentation.screens.search

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.fiscal.compass.R
import com.fiscal.compass.domain.model.Transaction
import com.fiscal.compass.domain.util.DateTimeUtil
import com.fiscal.compass.presentation.screens.search.navigation.SearchNavigation
import com.fiscal.compass.ui.components.cards.ChipFlow
import com.fiscal.compass.ui.components.cards.TransactionCard
import com.google.gson.Gson

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchResultsScreen(
    state: SearchResultsState,
    onEvent: (SearchEvent) -> Unit,
    onNavigate: (SearchNavigation) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Transaction History")
                },
                navigationIcon = {
                    IconButton(
                        onClick = { onNavigate(SearchNavigation.NavigateBack) }
                    ) {
                        Icon(
                            painterResource(id = R.drawable.ic_arrow_back_24),
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        onNavigate(SearchNavigation.NavigateToFilters)
                    }) {
                        Icon(
                            painterResource(R.drawable.ic_filter_list_24),
                            contentDescription = "Filter Transactions"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Show active filters chips if any filters are active
            if (state.searchCriteria.areAnyFiltersActive()) {
                val activeFilters = mutableListOf<String>()
                state.searchCriteria.transactionType?.let { activeFilters.add(it.name) }

                val selectedCategoryNames = state.searchCriteria.categories?.map { it.name } ?: emptyList()
                activeFilters.addAll(selectedCategoryNames)

                val selectedPersonNames = state.searchCriteria.persons?.map { it.name } ?: emptyList()
                activeFilters.addAll(selectedPersonNames)

                state.searchCriteria.dateRange?.startDate?.let {
                    activeFilters.add("From: ${DateTimeUtil.formatTimestampAsDate(it, "MMM dd, yyyy")}")
                }
                state.searchCriteria.dateRange?.endDate?.let {
                    activeFilters.add("To: ${DateTimeUtil.formatTimestampAsDate(it, "MMM dd, yyyy")}")
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

            // Handle display states using when expression
            when (val displayState = state.displayState) {
                is SearchResultsDisplayState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is SearchResultsDisplayState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = displayState.message,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }

                is SearchResultsDisplayState.Content -> {
                    SearchResultsContent(
                        searchResults = displayState.searchResults,
                        onTransactionSelected = { transaction ->
                            val transactionJson = Gson().toJson(transaction)
                            onNavigate(SearchNavigation.NavigateToTransactionDetail(transactionJson))
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchResultsContent(
    searchResults: Map<java.util.Date, List<Transaction>>,
    onTransactionSelected: (Transaction) -> Unit
) {
    if (searchResults.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No results found.",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            searchResults.forEach { (date, transactionsForDate) ->
                stickyHeader {
                    val formattedDate = DateTimeUtil.formatDate(date, "MMM dd, yyyy")
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

