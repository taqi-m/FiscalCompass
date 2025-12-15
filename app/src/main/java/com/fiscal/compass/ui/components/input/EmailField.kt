package com.fiscal.compass.ui.components.input

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
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
 * A specialized text field for email input with built-in validation.
 * Automatically sets keyboard type to Email and validates email format.
 */
@Composable
fun EmailField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    label: String = "Email",
    placeholder: String = "Enter your email",
    imeAction: ImeAction = ImeAction.Next,
    error: String? = null,
    required: Boolean = false,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    showIcon: Boolean = true,
    helperText: String? = null
) {
    // Email validation
    val emailPattern = android.util.Patterns.EMAIL_ADDRESS
    val isValidEmail = value.isEmpty() || emailPattern.matcher(value).matches()
    val displayError = error ?: if (!isValidEmail && value.isNotEmpty()) "Invalid email address" else null

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
            onValueChange = { onValueChange(it.trim()) },
            placeholder = { Text(placeholder) },
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 500.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = imeAction
            ),
            leadingIcon = if (showIcon) {
                { Icon(Icons.Default.Email, contentDescription = "Email") }
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
fun EmailFieldPreview() {
    FiscalCompassTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            EmailField(
                value = "",
                onValueChange = {},
                modifier = Modifier.padding(bottom = 16.dp)
            )
            EmailField(
                value = "test@example.com",
                onValueChange = {},
                modifier = Modifier.padding(bottom = 16.dp)
            )
            EmailField(
                value = "invalid-email",
                onValueChange = {},
            )
        }
    }
}

