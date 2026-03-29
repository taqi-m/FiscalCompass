package com.fiscal.compass.presentation.screens.home.dashboard

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fiscal.compass.domain.model.Transaction
import com.fiscal.compass.presentation.utilities.CurrencyFormater
import com.fiscal.compass.ui.components.cards.TransactionCard
import com.fiscal.compass.ui.theme.FiscalCompassTheme


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun DashboardScreen(
    state: DashboardScreenState,
    onEvent: (DashboardEvent) -> Unit,
    onRecentTransactionClick: (Transaction) -> Unit,
) {
    LaunchedEffect(Unit) {
        onEvent(DashboardEvent.OnScreenLoad)
    }

    DashboardScreenContent(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState()),
        state = state,
        onRecentTransactionClick = onRecentTransactionClick,
    )
}


@Composable
private fun DashboardScreenContent(
    modifier: Modifier = Modifier,
    state: DashboardScreenState,
    onRecentTransactionClick: (Transaction) -> Unit,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(
            space = 16.dp,
            alignment = Alignment.Top
        )
    ) {
        GreetingSection(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            userName = state.userInfo.name
        )

        BalanceOverview(
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .wrapContentSize(Alignment.Center),
            userInfo = state.userInfo,
        )

        RecentTransactionsSection(
            modifier = Modifier.fillMaxWidth(),
            transactions = state.recentTransactions,
            onTransactionClick = onRecentTransactionClick,
        )
    }
}


@Composable
private fun BalanceOverview(
    modifier: Modifier = Modifier,
    userInfo: UserInfo,
) {
    Column(
        modifier = modifier
            .wrapContentHeight()
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = MaterialTheme.shapes.medium
            )
            .padding(24.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = "Total Balance",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Text(
            text = CurrencyFormater.formatCurrency(userInfo.balance),
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}


//@Preview(showBackground = true)
@Composable
private fun BalanceOverviewPreview() {
    val userInfo = UserInfo(name = "John Doe", balance = 12345.67)
    FiscalCompassTheme {
        BalanceOverview(userInfo = userInfo)
    }
}


@Composable
private fun EmptyPlaceholder() {
    Text(
        text = "Start tracking your expenses/incomes to see where your money goes!",
        style = MaterialTheme.typography.bodyMedium,
    )
}

@Composable
private fun GreetingSection(
    modifier: Modifier = Modifier,
    userName: String = "User"
) {
    Text(
        modifier = modifier,
        text = "Hello, $userName!",
        style = MaterialTheme.typography.headlineMedium
    )
}

@Composable
private fun RecentTransactionsSection(
    modifier: Modifier = Modifier,
    transactions: List<Transaction> = emptyList(),
    onTransactionClick: (Transaction) -> Unit,
) {
    Column(
        modifier = modifier
            .wrapContentHeight()
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.5f),
                MaterialTheme.shapes.medium
            )
            .padding(24.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = "Recent Transactions", style = MaterialTheme.typography.titleMedium)

        when {
            transactions.isEmpty() ->
                EmptyPlaceholder()

            else -> {
                transactions.forEachIndexed { index, transaction ->
                    TransactionCard(
                        transaction = transaction,
                        onClicked = { onTransactionClick(transaction) },
                        onEditClicked = {},
                        onDeleteClicked = {},
                    )

                    if (index != transactions.lastIndex) {
                        HorizontalDivider(
                            modifier = Modifier.fillMaxWidth(),
                            thickness = 1.dp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                        )
                    }
                }
            }
        }
    }
}

//@Preview(showBackground = true)
@Composable
private fun RecentTransactionsSectionPreview() {
    val transactions = listOf(
        Transaction.sampleExpense(),
        Transaction.sampleIncome(),
        Transaction.sampleExpense()
    )
    FiscalCompassTheme {
        RecentTransactionsSection(
            transactions = transactions,
            onTransactionClick = {},
        )
    }
}

@Preview(
    showBackground = true,
    device = "spec:width=1080px,height=2340px,dpi=440",
    showSystemUi = false,
)
@Composable
fun DashboardScreenPreview() {
    val state = DashboardScreenState(
        userInfo = UserInfo(
            name = "John Doe",
            balance = 12345.67,
        )
    )
    FiscalCompassTheme {
        DashboardScreen(
            state = state,
            onEvent = {},
            onRecentTransactionClick = {},
        )
    }
}