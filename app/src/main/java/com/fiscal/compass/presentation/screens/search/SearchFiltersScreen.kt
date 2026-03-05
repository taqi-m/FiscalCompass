package com.fiscal.compass.presentation.screens.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fiscal.compass.R
import com.fiscal.compass.domain.util.TransactionType
import com.fiscal.compass.presentation.screens.search.navigation.SearchNavigation
import com.fiscal.compass.ui.components.cards.ChipFlow
import com.fiscal.compass.ui.components.pickers.DatePicker
import com.fiscal.compass.ui.theme.FiscalCompassTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchFiltersScreen(
    state: SearchResultsState,
    onEvent: (SearchEvent) -> Unit,
    onNavigate: (SearchNavigation) -> Unit,
) {

    // Initialize tempSearchCriteria to match searchCriteria when entering the screen
    // Only if tempSearchCriteria is empty (first time or after clearing)
    LaunchedEffect(Unit) {
        if (!state.tempSearchCriteria.areAnyFiltersActive() && state.searchCriteria.areAnyFiltersActive()) {
            onEvent(SearchEvent.ResetTempFilters)
        }
    }

    var tempFilterType by remember {
        mutableStateOf(state.tempSearchCriteria.transactionType?.name ?: "")
    }

    // Update tempFilterType when state changes
    LaunchedEffect(state.tempSearchCriteria.transactionType) {
        tempFilterType = state.tempSearchCriteria.transactionType?.name ?: ""
    }

    // Use remember with key to ensure recomposition when criteria changes
    val selectedCategories = remember(state.tempSearchCriteria) {
        state.tempSearchCriteria.categories ?: emptyList()
    }
    val selectedPersons = remember(state.tempSearchCriteria) {
        state.tempSearchCriteria.persons ?: emptyList()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Filters")
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
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
                            onEvent(SearchEvent.UpdateTempFilterType(null))
                        },
                        shape = SegmentedButtonDefaults.itemShape(index = 0, count = 3)
                    ) {
                        Text("All")
                    }
                    SegmentedButton(
                        selected = tempFilterType == "INCOME",
                        onClick = {
                            tempFilterType = "INCOME"
                            onEvent(SearchEvent.UpdateTempFilterType(TransactionType.INCOME))
                        },
                        shape = SegmentedButtonDefaults.itemShape(index = 1, count = 3)
                    ) {
                        Text("Income")
                    }
                    SegmentedButton(
                        selected = tempFilterType == "EXPENSE",
                        onClick = {
                            tempFilterType = "EXPENSE"
                            onEvent(SearchEvent.UpdateTempFilterType(TransactionType.EXPENSE))
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
                        .clickable {
                            onNavigate(SearchNavigation.NavigateToCategorySelection)
                        },
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
                        .clickable {
                            onNavigate(SearchNavigation.NavigateToPersonSelection)
                        },
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
                    selectedDate = state.tempSearchCriteria.dateRange?.startDate,
                    onDateSelected = { date ->
                        onEvent(SearchEvent.TempStartDateSelected(date))
                    }
                )
                DatePicker(
                    modifier = Modifier.fillMaxWidth(),
                    label = "End Date",
                    selectedDate = state.tempSearchCriteria.dateRange?.endDate,
                    onDateSelected = { date ->
                        onEvent(SearchEvent.TempEndDateSelected(date))
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
                        onNavigate(SearchNavigation.NavigateBack)
                    }) {
                    Text("Apply")
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun SearchFiltersScreenPreview() {
    FiscalCompassTheme {
        Surface {
            SearchFiltersScreen(
                state = SearchResultsState(),
                onEvent = {},
                onNavigate = {}
            )
        }

    }
}
