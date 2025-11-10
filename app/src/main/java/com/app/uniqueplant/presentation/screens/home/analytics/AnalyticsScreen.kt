package com.app.uniqueplant.presentation.screens.home.analytics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.uniqueplant.R
import com.app.uniqueplant.ui.components.ListTable

@Composable
fun AnalyticsScreen(
    state: AnalyticsScreenState,
    onEvent: (AnalyticsEvent) -> Unit,
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_save_24),
                    contentDescription = "Add Transaction"
                )
            }
        }
    ) {
        AnalyticsScreenContent(
            state = state,
            onEvent = onEvent
        )
    }
}

@Composable
private fun AnalyticsScreenContent(
    state: AnalyticsScreenState,
    onEvent: (AnalyticsEvent) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        /*CardWithIcon(
            modifier = Modifier.fillMaxWidth(),
            icon = R.drawable.ic_calendar_month_56,
            label = "General Report",
            description = "View general report",
            onClick = {}
        )*/
        ListTable(
            data = state.expenses,
            amountHeader = "Expenses",
            modifier = Modifier
                .fillMaxWidth()
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        ListTable(
            data = state.incomes,
            amountHeader = "Incomes",
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