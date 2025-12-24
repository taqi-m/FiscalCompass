package com.fiscal.compass.presentation.screens.transactionScreens.amountScreen

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fiscal.compass.R
import com.fiscal.compass.presentation.screens.category.UiState
import com.fiscal.compass.presentation.utilities.CurrencyFormater
import com.fiscal.compass.presentation.utils.AmountInputType
import com.fiscal.compass.ui.components.input.Calculator
import com.fiscal.compass.ui.theme.FiscalCompassTheme


// Enum for field selection
private enum class AmountField {
    TOTAL_AMOUNT,
    AMOUNT_PAID
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AmountScreen(
    onEvent: (AmountEvent) -> Unit,
    state: AmountScreenState,
    onBack: () -> Unit,
    onSuccess: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val uiState = state.uiState

    LaunchedEffect(uiState) {
        val message: String? = when (uiState) {
            is UiState.Error -> uiState.message
            is UiState.Success -> {
                onSuccess()
                null
            }

            else -> null
        }
        if (uiState !is UiState.Success) {
            message?.let { msg ->
                if (msg.isNotEmpty()) {
                    snackbarHostState.showSnackbar(
                        message = msg,
                        duration = SnackbarDuration.Short,
                        actionLabel = "Dismiss"
                    )
                }
            }
        }
    }
    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState
            )
        },
        topBar = {
            TopAppBar(
                title = { Text(text = "Enter amount") },
                navigationIcon = {
                    IconButton(
                        onClick = onBack
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_arrow_back_24),
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) {
    AmountScreenContent(
        modifier = Modifier.fillMaxSize().padding(it),
        onEvent = onEvent,
        state = state
    )
    }
}


@Composable
fun AmountScreenContent(
    modifier: Modifier = Modifier,
    onEvent: (AmountEvent) -> Unit,
    state: AmountScreenState
){
    // Force activeField to AMOUNT_PAID when in edit mode, otherwise allow switching
    var activeField by rememberSaveable { mutableStateOf(AmountField.TOTAL_AMOUNT) }
    
    // Force AMOUNT_PAID when in edit mode
    LaunchedEffect(state.editMode) {
        if (state.editMode) {
            activeField = AmountField.AMOUNT_PAID
        }
    }

    // Calculate remaining amount and progress
    val totalAmount = state.totalAmount
    val paidAmount = state.paidAmount
    val remainingAmount = (totalAmount - paidAmount).coerceAtLeast(0.0)
    val targetProgress = if (totalAmount > 0) {
        ((paidAmount / totalAmount) * 100).coerceIn(0.0, 100.0).toFloat()
    } else {
        0f
    }
    val progressPercentage by animateFloatAsState(
        targetValue = targetProgress,
        label = "progressPercentage"
    )

    // Get the current value to pass to Calculator based on active field
    val currentDisplayValue = when (activeField) {
        AmountField.TOTAL_AMOUNT -> totalAmount
        AmountField.AMOUNT_PAID -> paidAmount
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp)
            .padding(bottom = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Payment Progress Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Payment Progress",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${targetProgress.toInt()}%",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                        color = when {
                            targetProgress >= 100 -> MaterialTheme.colorScheme.primary
                            targetProgress > 0 -> MaterialTheme.colorScheme.tertiary
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }

                // Progress indicator
                LinearProgressIndicator(
                    progress = { (progressPercentage / 100) },
                    modifier = Modifier.fillMaxWidth(),
                    color = when {
                        progressPercentage >= 100 -> MaterialTheme.colorScheme.primary
                        progressPercentage > 50 -> MaterialTheme.colorScheme.tertiary
                        else -> MaterialTheme.colorScheme.secondary
                    },
                )

                // Remaining amount display
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Remaining:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = CurrencyFormater.formatCurrency(remainingAmount),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                        color = if (remainingAmount > 0)
                            MaterialTheme.colorScheme.error
                        else
                            MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        // Field selector tabs with auto-fill button
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Total Amount Card
            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.editMode,
                onClick = {
                    if (state.editMode) return@OutlinedCard
                    activeField = AmountField.TOTAL_AMOUNT
                          },
                shape = MaterialTheme.shapes.small,
                border = BorderStroke(
                    width = if (activeField == AmountField.TOTAL_AMOUNT) 2.dp else 1.dp,
                    color = if (activeField == AmountField.TOTAL_AMOUNT)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.outline
                )
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Total Amount",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (activeField == AmountField.TOTAL_AMOUNT)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = CurrencyFormater.formatCalculatorCurrency(state.totalAmount.toString()),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                        color = if (activeField == AmountField.TOTAL_AMOUNT)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // Amount Paid Card
            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
                onClick = { activeField = AmountField.AMOUNT_PAID },
                shape = MaterialTheme.shapes.small,
                border = BorderStroke(
                    width = if (activeField == AmountField.AMOUNT_PAID) 2.dp else 1.dp,
                    color = if (activeField == AmountField.AMOUNT_PAID)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.outline
                )
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Amount Paid",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (activeField == AmountField.AMOUNT_PAID)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = CurrencyFormater.formatCalculatorCurrency(state.paidAmount.toString()),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                        color = if (activeField == AmountField.AMOUNT_PAID)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        // Auto-fill button for full payment
        if (totalAmount > 0 && paidAmount < totalAmount) {
            Button(
                onClick = {
                    onEvent(AmountEvent.OnAmountPaidChange(state.totalAmount))
                    activeField = AmountField.AMOUNT_PAID
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(8.dp),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                )
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_check_24),
                    contentDescription = "Mark as Fully Paid",
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = "Mark as Fully Paid",
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }

        // Calculator with direct value binding - receives initialValue from state
        // No TextField inside Calculator - values come from parent state
        Calculator(
            modifier = Modifier.fillMaxSize(),
            initialValue = currentDisplayValue,
            label = when (activeField) {
                AmountField.TOTAL_AMOUNT -> "Total Amount"
                AmountField.AMOUNT_PAID -> "Amount Paid"
            },
            inputType = when (activeField) {
                AmountField.TOTAL_AMOUNT -> AmountInputType.TOTAL_AMOUNT
                AmountField.AMOUNT_PAID -> AmountInputType.AMOUNT_PAID
            },
            onValueChange = { value, inputType ->
                // Update the appropriate field based on inputType
                // Only trigger event if value actually changed to prevent Calculator initialization from resetting values
                val currentValue = when (inputType) {
                    AmountInputType.TOTAL_AMOUNT -> state.totalAmount
                    AmountInputType.AMOUNT_PAID -> state.paidAmount
                }
                
                // Only update if value is different from current state
                if (value != currentValue) {
                    when (inputType) {
                        AmountInputType.TOTAL_AMOUNT -> onEvent(
                            AmountEvent.OnAmountChange(
                                value,
                                inputType
                            )
                        )

                        AmountInputType.AMOUNT_PAID -> onEvent(
                            AmountEvent.OnAmountPaidChange(
                                value
                            )
                        )
                    }
                }
            },
            onSaveClick = {
                onEvent(AmountEvent.OnSaveClicked)
            },
        )
    }
}


@Preview(showSystemUi = true, showBackground = true)
//@Preview(showSystemUi = true, showBackground = true, name = "Calculator Screen - Nexus 7", device = Devices.NEXUS_7)
@Composable
fun AmountScreenPreview() {
    FiscalCompassTheme {
        AmountScreen(
            onEvent = {},
            state = AmountScreenState(
                editMode = true,
                totalAmount = 1000.0,
                paidAmount = 600.0,
            ),
            onBack = {},
            onSuccess = {}
        )
    }
}