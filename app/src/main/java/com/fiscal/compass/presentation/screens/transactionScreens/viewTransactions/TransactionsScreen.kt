package com.fiscal.compass.presentation.screens.transactionScreens.viewTransactions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fiscal.compass.presentation.utilities.CurrencyFormater
import com.fiscal.compass.presentation.model.TransactionUi
import com.fiscal.compass.presentation.screens.category.UiState
import com.fiscal.compass.presentation.screens.search.DateHeader
import com.fiscal.compass.presentation.screens.search.TransactionHeading
import com.fiscal.compass.ui.components.ErrorContainer
import com.fiscal.compass.ui.components.LoadingContainer
import com.fiscal.compass.ui.components.cards.TransactionCard
import com.fiscal.compass.ui.components.input.MonthSelector
import com.fiscal.compass.ui.theme.FiscalCompassTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TransactionsScreen(
    state: TransactionScreenState,
    onEvent: (TransactionEvent) -> Unit,
    onNavigateClicked: (TransactionUi) -> Unit,
) {
    val transactions = state.transactions
    val uiState = state.uiState

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {


        LazyColumn(
            state = rememberLazyListState(),
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            item {
                MonthSelector(
                    currentDate = state.currentDate,
                    onPreviousMonth = {
                        onEvent(TransactionEvent.OnPreviousMonth)
                    },
                    onNextMonth = {
                        onEvent(TransactionEvent.OnNextMonth)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }
            item {
                TransactionHeading(
                    modifier = Modifier
                        .fillMaxWidth(),
                    currentBalance = CurrencyFormater.formatCurrency(state.incoming - state.outgoing),
                    incoming = CurrencyFormater.formatCurrency(state.incoming),
                    outgoing = CurrencyFormater.formatCurrency(state.outgoing)
                )
            }


            when (state.uiState) {
                is UiState.Loading -> {
                    item {
                        LoadingContainer("Loading transactions...")
                    }
                }

                is UiState.Error -> {
                    item {
                        ErrorContainer(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 32.dp),
                            message = state.uiState.message,
                            actionLabel = "Retry",
                            onAction = {
                                onEvent(TransactionEvent.OnPreviousMonth)
                            })
                    }
                }

                else -> {
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

                        //TODO: Refactor to use TransactionCard composable
                        /*items(transactionsForDate.size) { index ->
                            TransactionCard(
                                transaction = transactionsForDate[index],
                                onClicked = {
                                    onNavigateClicked(transactionsForDate[index])
                                },
                                onEditClicked = {
                                    onEvent(
                                        TransactionEvent.OnTransactionDialogToggle(
                                            TransactionDialogToggle.Edit(transactionsForDate[index])
                                        )
                                    )
                                },
                                onDeleteClicked = {
                                    onEvent(
                                        TransactionEvent.OnTransactionDialogToggle(
                                            TransactionDialogToggle.Delete(transactionsForDate[index])
                                        )
                                    )
                                },
                            )
                        }*/
                    }
                }
            }

            // Add some padding at the bottom to avoid content being hidden behind navigation bars
            item {
                Spacer(modifier = Modifier.padding(WindowInsets.navigationBars.asPaddingValues()))
            }
        }

    }

    when (state.currentDialog) {
        TransactionScreenDialog.EditTransaction -> {
        }

        TransactionScreenDialog.DeleteTransaction -> {

        }

        else -> {}
    }
}








@Preview(showBackground = true, showSystemUi = false)
@Composable
fun TransactionsScreenPreview() {
    FiscalCompassTheme {
        TransactionsScreen(
            state = TransactionScreenState(
                incoming = 5000.0,
                outgoing = 2000.0,
                uiState = UiState.Idle,
                transactions = mapOf(
                    Date() to TransactionUi.dummyList,
                    Date(System.currentTimeMillis() - 86400000L) to listOf(TransactionUi.dummy)
                )
            ),
            onEvent = {},
        ) {}
    }
}
