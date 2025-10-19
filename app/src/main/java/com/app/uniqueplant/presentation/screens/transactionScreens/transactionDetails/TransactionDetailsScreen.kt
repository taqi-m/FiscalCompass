package com.app.uniqueplant.presentation.screens.transactionScreens.transactionDetails

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.uniqueplant.presentation.model.CategoryUi
import com.app.uniqueplant.presentation.model.PersonUi
import com.app.uniqueplant.presentation.model.TransactionUi
import com.app.uniqueplant.presentation.screens.category.UiState
import com.app.uniqueplant.ui.theme.UniquePlantTheme

@Composable
fun TransactionDetailsScreen(
    state: TransactionDetailsScreenState,
    onEvent: (TransactionDetailsEvent) -> Unit,
    transactionUi: TransactionUi,
    onBackPressed: () -> Unit,
) {
    LaunchedEffect(true) {
        onEvent(
            TransactionDetailsEvent.OnScreenLoad(
                transactionUi.transactionId,
                transactionUi.isExpense
            )
        )
    }
    Scaffold(
        topBar = {
            TransactionDetailsTopBar(
                onBackClick = {
                    onBackPressed()
                }
            )
        }
    ) { paddingValues ->
        DetailsContent(
            modifier = Modifier.background(MaterialTheme.colorScheme.background).padding(paddingValues),
            state = state,
            uiState = state.uiState,
            onEvent = onEvent
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TransactionDetailsTopBar(onBackClick: () -> Unit) {
    TopAppBar(
        title = { Text(text = "Transaction Details") },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
        }
    )
}


@Composable
private fun DetailsContent(
    modifier: Modifier = Modifier,
    state: TransactionDetailsScreenState,
    uiState: UiState,
    onEvent: (TransactionDetailsEvent) -> Unit,
) {
    when (uiState) {
        is UiState.Idle, is UiState.Loading -> {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
            }
        }

        is UiState.Success -> {
            Card(
                modifier = modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp)
                    .padding(bottom = 8.dp),
                colors = CardDefaults.cardColors().copy(
                    containerColor = CardDefaults.cardColors().containerColor.copy(alpha = 0.5f)
                )
            ) {
                TransactionCardContent(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    state = state
                )
            }
        }

        is UiState.Error -> {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "An error occurred while loading the transaction details.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun TransactionCardContent(
    modifier: Modifier = Modifier,
    state: TransactionDetailsScreenState
) {
    HeaderContent(
        transactionUi = state.transaction
    )
    RowContent(
        modifier = modifier,
        transaction = state.transaction,
        category = state.category,
        personUi = state.person
    )
}

@Composable
private fun HeaderContent(
    transactionUi: TransactionUi?
) {
    if (transactionUi == null) {
        return
    }
    var amount = transactionUi.formatedAmount
    if (transactionUi.isExpense){
        amount = "- $amount"
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = amount,
            style = MaterialTheme.typography.headlineMedium,
            color = if (transactionUi.isExpense) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onPrimaryContainer
        )
        Spacer(
            modifier = Modifier.padding(vertical = 8.dp)
        )
        Text(
            text = "${transactionUi.formatedDate},  ${transactionUi.formatedTime}",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Composable
private fun RowContent(
    modifier: Modifier = Modifier,
    transaction: TransactionUi?,
    category: CategoryUi?,
    personUi: PersonUi?
) {
    if (transaction == null) {
        return
    }
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        CardRow(
            label = "Category",
            value = category?.name ?: "N/A"
        )
        CardRowDivider()
        CardRow(
            label = "Person",
            value = personUi?.name ?: "N/A"
        )
        CardRowDivider()
        CardColumn(
            label = "Description",
            value = if (transaction.description.isNullOrEmpty()) "N/A" else transaction.description
        )
    }
}

@Composable
private fun CardRow(
    label: String,
    value: String
) {
    Card(
        shape = RoundedCornerShape(15),
        colors = CardDefaults.cardColors().copy(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        ),
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(text = label, style = MaterialTheme.typography.bodyLarge)
            Text(text = value, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun CardColumn(
    label: String,
    value: String
){
    Card(
        shape = RoundedCornerShape(15),
        colors = CardDefaults.cardColors().copy(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        ),
        modifier = Modifier
            .fillMaxWidth()
    ){
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            Text(text = label, style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.padding(4.dp))
            Text(text = value, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun CardRowDivider() {
    HorizontalDivider(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
        thickness = 2.dp
    )
}

@Preview(showBackground = true)
@Composable
fun TransactionDetailsScreenPreview() {
    UniquePlantTheme {
        TransactionDetailsScreen(
            state = TransactionDetailsScreenState(
                uiState = UiState.Success("Transaction Loaded"),
                transaction = TransactionUi.dummy,
                category = CategoryUi.dummy,
                person = PersonUi.dummy

            ),
            onEvent = {},
            onBackPressed = {},
            transactionUi = TransactionUi.dummy,
        )
    }
}