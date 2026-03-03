package com.fiscal.compass.ui.components.input

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Icon
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
 * A specialized text field for phone number input with validation.
 * Restricts input to digits and optional formatting characters.
 */
@Composable
fun PhoneField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    label: String = "Phone Number",
    placeholder: String = "Enter phone number",
    imeAction: ImeAction = ImeAction.Next,
    error: String? = null,
    required: Boolean = false,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    showIcon: Boolean = true,
    helperText: String? = null,
    minLength: Int = 10,
    maxLength: Int = 15
) {
    // Phone number validation
    val validationError = if (value.isNotEmpty()) {
        val digitsOnly = value.filter { it.isDigit() }
        when {
            digitsOnly.length < minLength -> "Phone number must be at least $minLength digits"
            digitsOnly.length > maxLength -> "Phone number is too long"
            else -> null
        }
    } else null

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
                // Allow only digits and common phone formatting characters
                if (newValue.all { it.isDigit() || it in listOf('+', '-', '(', ')', ' ') }) {
                    onValueChange(newValue)
                }
            },
            placeholder = { Text(placeholder) },
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 500.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Phone,
                imeAction = imeAction
            ),
            leadingIcon = if (showIcon) {
                { Icon(Icons.Default.Phone, contentDescription = "Phone") }
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
fun PhoneFieldPreview() {
    FiscalCompassTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            PhoneField(
                value = "",
                onValueChange = {},
                modifier = Modifier.padding(bottom = 16.dp)
            )
            PhoneField(
                value = "+1 234 567 8900",
                onValueChange = {},
                modifier = Modifier.padding(bottom = 16.dp)
            )
            PhoneField(
                value = "123",
                onValueChange = {},
                helperText = "Include country code"
            )
        }
    }
}

