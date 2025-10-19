package com.app.uniqueplant.presentation.screens.transactionScreens.viewTransactions

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
import com.app.uniqueplant.domain.usecase.CurrencyFormaterUseCase
import com.app.uniqueplant.presentation.model.TransactionUi
import com.app.uniqueplant.presentation.screens.category.UiState
import com.app.uniqueplant.ui.components.ErrorContainer
import com.app.uniqueplant.ui.components.LoadingContainer
import com.app.uniqueplant.ui.components.cards.TransactionCard
import com.app.uniqueplant.ui.components.input.MonthSelector
import com.app.uniqueplant.ui.theme.UniquePlantTheme
import java.text.SimpleDateFormat
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
                    currentBalance = CurrencyFormaterUseCase.formatCurrency(state.incoming - state.outgoing),
                    incoming = CurrencyFormaterUseCase.formatCurrency(state.incoming),
                    outgoing = CurrencyFormaterUseCase.formatCurrency(state.outgoing)
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

                        items(transactionsForDate.size) { index ->
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
                        }
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


@Composable
fun DateHeader(modifier: Modifier = Modifier, date: String) {
    Card(
        modifier = modifier, colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ), shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = date,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}


@Composable
fun TransactionHeading(
    modifier: Modifier = Modifier, currentBalance: String, incoming: String, outgoing: String
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors().copy(
                containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
            )
        ) {
            val textColor = MaterialTheme.colorScheme.onSecondaryContainer
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Total Balance",
                    style = MaterialTheme.typography.labelLarge,
                    color = textColor
                )
                Text(
                    text = currentBalance,
                    style = MaterialTheme.typography.headlineMedium,
                    color = textColor
                )
                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant
                )
                Row {
                    BalanceView(
                        label = "Incoming",
                        amount = incoming,
                        modifier = Modifier
                            .weight(1f)
                            .padding(8.dp),
                        textAlign = TextAlign.Start
                    )
                    BalanceView(
                        label = "Outgoing",
                        amount = outgoing,
                        modifier = Modifier
                            .weight(1f)
                            .padding(8.dp),
                        textAlign = TextAlign.End
                    )
                }
            }
        }
    }
}


@Composable
private fun BalanceView(
    label: String,
    amount: String,
    modifier: Modifier = Modifier,
    textAlign: TextAlign = TextAlign.Start
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = label,
            style = MaterialTheme.typography.labelLarge,
            textAlign = textAlign
        )
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = amount,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = textAlign
        )
    }
}


@Preview(showBackground = true, showSystemUi = false)
@Composable
fun TransactionsScreenPreview() {
    UniquePlantTheme {
        TransactionsScreen(
            state = TransactionScreenState(
                uiState = UiState.Error("Failed to load transactions"),
                transactions = mapOf(
                    java.util.Date() to TransactionUi.dummyList,
                    java.util.Date(System.currentTimeMillis() - 86400000L) to listOf(TransactionUi.dummy)
                )
            ),
            onEvent = {},
        ) {}
    }
}
