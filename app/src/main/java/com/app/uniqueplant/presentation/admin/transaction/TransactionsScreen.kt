package com.app.uniqueplant.presentation.admin.transaction

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.uniqueplant.data.model.Expense
import com.app.uniqueplant.data.model.Income
import com.app.uniqueplant.ui.components.ExpenseCard
import com.app.uniqueplant.ui.components.IncomeCard
import com.app.uniqueplant.ui.theme.UniquePlantTheme

@Composable
fun TransactionsScreen(
    viewModel: TransactionViewModel = hiltViewModel()
) {
    val transactions = viewModel.allTransactions.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TransactionHeading()
        for (transaction in transactions.value) {
            Log.d("TransactionsScreen", "Transaction: $transaction")
            when (transaction) {
                is Expense -> {
                    ExpenseCard(
                        expense = transaction.amount,
                        date = transaction.date,
                        onClick = { /* Handle click */ },
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }

                is Income -> {
                    IncomeCard(
                        income = transaction.amount,
                        date = transaction.date,
                        onClick = { /* Handle click */ },
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun TransactionHeading() {
    Text(
        text = "Transactions",
        style = MaterialTheme.typography.headlineLarge,
        modifier = Modifier.padding(8.dp)
    )
}

@Preview(
    showBackground = true
)
@Composable
fun TransactionHeadingPreview() {
    TransactionHeading()
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TransactionsScreenPreview() {
    UniquePlantTheme {
        TransactionsScreen()
    }
}