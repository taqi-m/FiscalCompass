package com.fiscal.compass.presentation.screens.transactionScreens.transactionDetails

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.fiscal.compass.R
import com.fiscal.compass.domain.model.Transaction
import com.fiscal.compass.presentation.model.CategoryUi
import com.fiscal.compass.presentation.model.PersonUi
import com.fiscal.compass.presentation.model.TransactionUi
import com.fiscal.compass.presentation.navigation.MainScreens
import com.fiscal.compass.presentation.screens.category.UiState
import com.fiscal.compass.presentation.screens.transactionScreens.amountScreen.AmountEvent
import com.fiscal.compass.ui.theme.FiscalCompassTheme
import com.google.gson.Gson

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionDetailsScreen(
    appNavController: NavHostController,
    state: TransactionDetailsScreenState,
    onEvent: (TransactionDetailsEvent) -> Unit,
) {
    val uiState = state.uiState

    val transactionJson = remember {
        val encodedJsonTransaction = appNavController.currentBackStackEntry
            ?.arguments?.getString("transaction")
        Uri.decode(encodedJsonTransaction ?: "")
    }

    // Load transaction once when the screen is first composed
    LaunchedEffect(transactionJson) {
        val transaction = Gson().fromJson(transactionJson, Transaction::class.java)
        Log.d("DetailsScreen", "Transaction: $transaction")
        onEvent(TransactionDetailsEvent.LoadTransaction(transaction))
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Transaction Details") },
                navigationIcon = {
                    IconButton(onClick = {
                        appNavController.popBackStack()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    if (state.canEdit) {
                        IconButton(
                            onClick = {
                                val encodedJsonTransaction = Uri.encode(transactionJson)
                                appNavController.navigate(
                                    MainScreens.Amount.editTransaction(
                                        encodedJsonTransaction
                                    )
                                )
                            }) {
                            Icon(
                                painter = painterResource(R.drawable.ic_edit_24),
                                contentDescription = "Edit"
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->

        when (uiState) {
            is UiState.Loading -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                }
            }

            is UiState.Idle, is UiState.Success -> {
                DetailsContent(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.background)
                        .padding(paddingValues),
                    state = state,
                )
            }

            is UiState.Error -> {
                Column(
                    modifier = Modifier
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
}


@Composable
private fun DetailsContent(
    modifier: Modifier = Modifier,
    state: TransactionDetailsScreenState,
) {
    TransactionCardContent(
        modifier = modifier
            .fillMaxSize(),
        state = state
    )
}

@Composable
private fun TransactionCardContent(
    modifier: Modifier = Modifier,
    state: TransactionDetailsScreenState
) {
    Column(
        modifier = modifier.padding(horizontal = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        HeaderContent(
            transactionUi = state.transaction
        )
        RowContent(
            transaction = state.transaction,
            category = state.category,
            personUi = state.person
        )
    }
}

@Composable
private fun HeaderContent(
    transactionUi: TransactionUi?
) {
    if (transactionUi == null) {
        return
    }
    var amount = transactionUi.formatedAmount
    if (transactionUi.isExpense) {
        amount = "- $amount"
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.small)
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
            modifier = Modifier.padding(vertical = 4.dp)
        )
        Text(
            text = transactionUi.formatedPaidAmount,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onPrimaryContainer
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
            value = category?.name ?: "N/A",
            description = category?.description
        )
        CardRowDivider()
        CardRow(
            label = "Person",
            value = personUi?.name ?: "N/A",
            description = personUi?.contact
        )
        CardRowDivider()
        CardRow(
            label = "Note",
            value = "",
            description = if (transaction.description.isNullOrEmpty()) "N/A" else transaction.description,
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun CardRow(
    label: String,
    value: String,
    description: String? = ""
) {

    Column(
        modifier = Modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
        )
        Card(
            shape = RoundedCornerShape(15),
            colors = CardDefaults.cardColors().copy(
                containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(0.5f),
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
            ),
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                value?.let {
                    if (value.isNotEmpty())
                        Text(text = value, style = MaterialTheme.typography.bodyLarge)
                }
                description?.let {
                    if (description.isNotEmpty())
                        Text(text = it, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@Composable
private fun CardColumn(
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
    FiscalCompassTheme {
        TransactionDetailsScreen(
            appNavController = rememberNavController(),
            state = TransactionDetailsScreenState(
                uiState = UiState.Success("Transaction Loaded"),
                transaction = TransactionUi.dummy,
                category = CategoryUi.dummy,
                person = PersonUi.dummy

            ),
            onEvent = {},
        )
    }
}