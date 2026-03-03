package com.fiscal.compass.ui.components.input

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fiscal.compass.ui.theme.FiscalCompassTheme

/**
 * A specialized text field for currency/amount input.
 * Restricts input to valid decimal numbers and provides formatting options.
 */
@Composable
fun AmountField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    label: String = "Amount",
    placeholder: String = "0.00",
    imeAction: ImeAction = ImeAction.Done,
    error: String? = null,
    required: Boolean = false,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    showIcon: Boolean = true,
    helperText: String? = null,
    currencySymbol: String = "$",
    maxValue: Double? = null,
    minValue: Double = 0.0,
    allowNegative: Boolean = false
) {
    // Amount validation
    val validationError = value.takeIf { it.isNotEmpty() }?.let {
        try {
            val amount = it.toDoubleOrNull()
            when {
                amount == null -> "Invalid amount"
                !allowNegative && amount < minValue -> "Amount must be at least $minValue"
                maxValue != null && amount > maxValue -> "Amount cannot exceed $maxValue"
                else -> null
            }
        } catch (_: Exception) {
            "Invalid amount"
        }
    }

    val displayError = error ?: validationError

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = if (required) "$label *" else label,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = 4.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = { newValue ->
                // Allow only valid decimal input
                val filtered = newValue.filter { it.isDigit() || it == '.' || (allowNegative && it == '-') }
                // Ensure only one decimal point
                if (filtered.count { it == '.' } <= 1 && filtered.count { it == '-' } <= 1) {
                    onValueChange(filtered)
                }
            },
            placeholder = { Text(placeholder) },
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 500.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal,
                imeAction = imeAction
            ),
            leadingIcon = if (showIcon) {
                {
                    Text(
                        text = currencySymbol,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(start = 12.dp)
                    )
                }
            } else null,
            isError = displayError != null,
            enabled = enabled,
            readOnly = readOnly
        )
        if (displayError != null) {
            Text(
                text = displayError,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Red,
                modifier = Modifier.padding(start = 4.dp)
            )
        } else if (helperText != null) {
            Text(
                text = helperText,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AmountFieldPreview() {
    FiscalCompassTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            AmountField(
                value = "",
                onValueChange = {},
                modifier = Modifier.padding(bottom = 16.dp)
            )
            AmountField(
                value = "1234.56",
                onValueChange = {},
                modifier = Modifier.padding(bottom = 16.dp),
                helperText = "Enter the transaction amount"
            )
            AmountField(
                value = "99999",
                onValueChange = {},
                maxValue = 10000.0,
                currencySymbol = "€"
            )
        }
    }
}

