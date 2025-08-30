package com.app.uniqueplant.presentation.screens.analytics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.uniqueplant.domain.usecase.CurrencyFormaterUseCase

@Composable
fun AnalyticsScreen(
    state: AnalyticsScreenState,
    onEvent: (AnalyticsEvent) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        ListTable(
            rows = state.expenses,
            type = "Expenses",
            modifier = Modifier
                .fillMaxWidth()
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        ListTable(
            rows = state.incomes,
            type = "Incomes",
            modifier = Modifier
                .fillMaxWidth()
        )

        if (state.isLoading) {
            CircularProgressIndicator()
        }

        if (state.error != null) {
            Text(
                text = state.error,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
fun ListTable(
    rows: Map<String, Double>,
    type: String,
    modifier: Modifier = Modifier
) {
    Card (
        modifier = modifier,
        colors = CardDefaults.cardColors().copy(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(8.dp).padding(horizontal = 8.dp)
        ) {
            //Header Row
            val headingStyle = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Bold
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Category",
                    style = headingStyle
                )
                Text(
                    text = type,
                    style = headingStyle
                )
            }

            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
            )

            //Data Rows
            val dataRowStyle = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.W500
            )
            rows.forEach { row ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier.weight(1f),
                        text = row.key,
                        style = dataRowStyle
                    )
                    Text(
                        text = CurrencyFormaterUseCase.formatCurrency(row.value),
                        style = dataRowStyle
                    )
                }
                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth(),
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                )
            }

            //Footer Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = "Total",
                    style = headingStyle
                )
                Text(
                    text = CurrencyFormaterUseCase.formatCurrency(
                        rows.values.sum()
                    ),
                    style = headingStyle
                )
            }
        }

    }
}

@Preview(showBackground = true)
@Composable
fun AnalyticsScreenPreview() {
    AnalyticsScreen(
        state = AnalyticsScreenState(
            expenses = mapOf(
                "Expense 1" to 100.0,
                "Expense 2" to 200.0,
                "Expense 3" to 150.0
            ),
            incomes = mapOf(
                "Income 1" to 100.0,
                "Income 2" to 200.0,
                "Income 3" to 300.0
            )
        ),
        onEvent = {},
    )
}