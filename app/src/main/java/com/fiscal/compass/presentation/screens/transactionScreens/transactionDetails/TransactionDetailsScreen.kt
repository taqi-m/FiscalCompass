package com.fiscal.compass.presentation.screens.transactionScreens.transactionDetails

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import com.fiscal.compass.ui.components.buttons.CardTextButton
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
                    onDeleteClick = {

                    },
                    onUpdateClick = {
                        val encodedJsonTransaction = Uri.encode(transactionJson)
                        appNavController.navigate(
                            MainScreens.Amount.editTransaction(
                                encodedJsonTransaction
                            )
                        )
                    }
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
    onDeleteClick: () -> Unit,
    onUpdateClick: () -> Unit
) {
    TransactionCardContent(
        modifier = modifier
            .fillMaxSize(),
        state = state,
        onDeleteClick = onDeleteClick,
        onUpdateClick = onUpdateClick

    )
}

@Composable
private fun TransactionCardContent(
    modifier: Modifier = Modifier,
    state: TransactionDetailsScreenState,
    onDeleteClick: () -> Unit,
    onUpdateClick: () -> Unit
) {
    Column(
        modifier = modifier.padding(horizontal = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HeaderContent(
            transactionUi = state.transaction
        )
        RowContent(
            transaction = state.transaction,
            category = state.category,
            personUi = state.person
        )
        if (state.canEdit) {
            DeleteAndUpdateCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
                    .padding(vertical = 8.dp),
                onDeleteClick = onDeleteClick,
                onUpdateClick = onUpdateClick
            )
        }
        CardRowDivider()
        Text(
            text = "${state.transaction?.formatedDate},  ${state.transaction?.formatedTime}",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onPrimaryContainer
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
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .padding(16.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Total",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
        )
        Spacer(
            modifier = Modifier.padding(vertical = 2.dp)
        )
        Text(
            text = amount,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        CardRowDivider()
        Text(
            text = "Paid",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
        )
        Spacer(
            modifier = Modifier.padding(vertical = 2.dp)
        )
        Text(
            text = transactionUi.formatedPaidAmount,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSecondaryContainer
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
            label = category?.name ?: "N/A",
            description = category?.description
        )
        CardRowDivider()
        CardRow(
            label = personUi?.name ?: "N/A",
            description = personUi?.contact
        )
        CardRowDivider()
        CardRow(
            label = "Note",
            description = if (transaction.description.isNullOrEmpty()) "N/A" else transaction.description,
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun CardRow(
    label: String,
    description: String? = ""
) {

    Column(
        modifier = Modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Card(
            shape = RoundedCornerShape(15),
            colors = CardDefaults.cardColors().copy(
                containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(0.35f),
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
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelLarge
                )
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


@Composable
fun DeleteAndUpdateCard(
    modifier: Modifier = Modifier,
    onDeleteClick: () -> Unit,
    onUpdateClick: () -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        CardTextButton(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f),
            cardColors = CardDefaults.cardColors().copy(
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer
            ),
            icon = painterResource(R.drawable.ic_delete_24),
            text = "Delete",
            onClick = onDeleteClick
        )
        CardTextButton(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f),
            cardColors = CardDefaults.cardColors().copy(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                contentColor = MaterialTheme.colorScheme.onTertiaryContainer
            ),
            icon = painterResource(R.drawable.ic_edit_24),
            text = "Edit",
            onClick = onUpdateClick
        )
    }
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
                person = PersonUi.dummy,
                canEdit = true,
            ),
            onEvent = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DeleteAndUpdateCardPreview() {
    DeleteAndUpdateCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .padding(8.dp),
        onDeleteClick = {},
        onUpdateClick = {}
    )
}
