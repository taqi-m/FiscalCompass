package com.fiscal.compass.presentation.screens.search

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fiscal.compass.domain.model.Transaction
import com.fiscal.compass.domain.util.DateRange
import com.fiscal.compass.domain.util.DateTimeUtil
import com.fiscal.compass.domain.util.SearchCriteria
import com.fiscal.compass.domain.util.TransactionType
import com.fiscal.compass.ui.theme.FiscalCompassTheme
import java.util.Date

/**
 * Shared utility composable for displaying date headers in search results
 */
@Composable
fun DateHeader(modifier: Modifier = Modifier, date: String) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
        shape = MaterialTheme.shapes.small
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
fun SearchResultsScreenPreview() {
    FiscalCompassTheme {
        SearchResultsScreen(
            state = SearchResultsState(
                searchCriteria = SearchCriteria()
                    .withTransactionType(TransactionType.INCOME)
                    .withDateRange(DateRange(DateTimeUtil.getCurrentTimestamp(), null)),
                displayState = SearchResultsDisplayState.Content(
                    searchResults = mapOf(
                        Date(DateTimeUtil.getCurrentTimestamp()) to Transaction.sampleList()
                    )
                )
            ),
            onEvent = {},
            onNavigate = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SearchFiltersScreenPreviewRoot() {
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

