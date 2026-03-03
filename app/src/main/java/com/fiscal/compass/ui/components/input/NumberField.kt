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
 * A specialized text field for numeric input (integers only).
 * Restricts input to whole numbers and provides range validation.
 */
@Composable
fun NumberField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    label: String = "Number",
    placeholder: String = "0",
    imeAction: ImeAction = ImeAction.Next,
    error: String? = null,
    required: Boolean = false,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    showIcon: Boolean = false,
    helperText: String? = null,
    minValue: Int? = null,
    maxValue: Int? = null,
    allowNegative: Boolean = true
) {
    // Number validation
    val validationError = value.takeIf { it.isNotEmpty() && it != "-" }?.let {
        try {
            val number = it.toIntOrNull()
            when {
                number == null -> "Invalid number"
                minValue != null && number < minValue -> "Minimum value is $minValue"
                maxValue != null && number > maxValue -> "Maximum value is $maxValue"
                else -> null
            }
        } catch (_: Exception) {
            "Invalid number"
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
                // Allow only digits and optionally minus sign
                val filtered = if (allowNegative) {
                    newValue.filter { it.isDigit() || it == '-' }
                } else {
                    newValue.filter { it.isDigit() }
                }
                // Ensure minus only at start
                if (filtered.count { it == '-' } <= 1 && (!filtered.contains('-') || filtered.startsWith('-'))) {
                    onValueChange(filtered)
                }
            },
            placeholder = { Text(placeholder) },
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 500.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = imeAction
            ),
            leadingIcon = null,
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
fun NumberFieldPreview() {
    FiscalCompassTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            NumberField(
                value = "",
                onValueChange = {},
                modifier = Modifier.padding(bottom = 16.dp)
            )
            NumberField(
                value = "42",
                onValueChange = {},
                label = "Age",
                minValue = 0,
                maxValue = 120,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            NumberField(
                value = "150",
                onValueChange = {},
                label = "Quantity",
                maxValue = 100,
                allowNegative = false
            )
        }
    }
}

