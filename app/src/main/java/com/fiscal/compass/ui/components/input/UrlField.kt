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
 * A specialized text field for URL input with validation.
 * Validates URL format and provides appropriate keyboard type.
 */
@Composable
fun UrlField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    label: String = "URL",
    placeholder: String = "https://example.com",
    imeAction: ImeAction = ImeAction.Done,
    error: String? = null,
    required: Boolean = false,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    helperText: String? = null,
    requireHttps: Boolean = false
) {
    // URL validation
    val urlPattern = android.util.Patterns.WEB_URL
    val validationError = if (value.isNotEmpty()) {
        when {
            !urlPattern.matcher(value).matches() -> "Invalid URL format"
            requireHttps && !value.startsWith("https://") -> "URL must use HTTPS"
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
            onValueChange = { onValueChange(it.trim()) },
            placeholder = { Text(placeholder) },
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 500.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Uri,
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
fun UrlFieldPreview() {
    FiscalCompassTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            UrlField(
                value = "",
                onValueChange = {},
                modifier = Modifier.padding(bottom = 16.dp)
            )
            UrlField(
                value = "https://example.com",
                onValueChange = {},
                modifier = Modifier.padding(bottom = 16.dp)
            )
            UrlField(
                value = "http://example.com",
                onValueChange = {},
                requireHttps = true,
                helperText = "HTTPS is required for security"
            )
        }
    }
}

