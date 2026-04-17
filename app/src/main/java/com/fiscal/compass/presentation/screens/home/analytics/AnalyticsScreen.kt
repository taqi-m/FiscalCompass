package com.fiscal.compass.presentation.screens.home.analytics

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fiscal.compass.R
import com.fiscal.compass.ui.components.ListTable
import com.fiscal.compass.ui.theme.FiscalCompassTheme

@Composable
fun AnalyticsScreen(
    state: AnalyticsScreenState,
    onEvent: (AnalyticsEvent) -> Unit,
) {
    LaunchedEffect(Unit) {
        onEvent(AnalyticsEvent.LoadAnalytics)
    }

    AnalyticsScreenContent(
        state = state,
    )
}

@Composable
private fun AnalyticsScreenContent(
    state: AnalyticsScreenState,
) {
    when (val displayState = state.displayState) {
        null, AnalyticsDisplayState.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
        }

        is AnalyticsDisplayState.Error -> {
            Column (
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Image(
                    modifier = Modifier.size(200.dp),
                    painter = painterResource(id = R.drawable.ph_analytics_empty),
                    contentDescription = null
                )
                Text(
                    text = displayState.message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(top = 16.dp),
                )
            }
        }

        is AnalyticsDisplayState.Content -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top,
            ) {
                ListTable(
                    data = displayState.expenses,
                    amountHeader = "Expenses",
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(modifier = Modifier.height(16.dp))

                ListTable(
                    data = displayState.incomes,
                    amountHeader = "Incomes",
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AnalyticsScreenLoadingPreview() {
    AnalyticsScreen(
        state = AnalyticsScreenState(displayState = AnalyticsDisplayState.Loading),
        onEvent = {},
    )
}

@Preview(showBackground = true)
@Composable
fun AnalyticsScreenErrorPreview() {
    FiscalCompassTheme {
        AnalyticsScreen(
            state = AnalyticsScreenState(
                displayState = AnalyticsDisplayState.Error("Unable to load analytics"),
            ),
            onEvent = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AnalyticsScreenContentPreview() {
    AnalyticsScreen(
        state = AnalyticsScreenState(
            displayState = AnalyticsDisplayState.Content(
                expenses = mapOf(
                    "Expense 1" to 100.0,
                    "Expense 2" to 200.0,
                    "Expense 3" to 150.0,
                ),
                incomes = mapOf(
                    "Income 1" to 100.0,
                    "Income 2" to 200.0,
                    "Income 3" to 300.0,
                ),
                totalIncomes = 600.0,
                totalExpenses = 450.0,
                totalProfit = 150.0,
            ),
        ),
        onEvent = {},
    )
}