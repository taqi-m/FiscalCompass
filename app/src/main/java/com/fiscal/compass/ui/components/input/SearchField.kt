package com.fiscal.compass.ui.components.input

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fiscal.compass.ui.theme.FiscalCompassTheme

/**
 * A specialized text field for search input with clear button.
 * Commonly used in search bars and filter interfaces.
 */
@Composable
fun SearchField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "Search...",
    imeAction: ImeAction = ImeAction.Search,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    showLeadingIcon: Boolean = true,
    onClear: (() -> Unit)? = null,
    onSearch: (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder) },
        modifier = modifier
            .fillMaxWidth()
            .widthIn(max = 500.dp),
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = imeAction
        ),
        leadingIcon = if (showLeadingIcon) {
            { Icon(Icons.Default.Search, contentDescription = "Search") }
        } else null,
        trailingIcon = if (value.isNotEmpty()) {
            {
                IconButton(onClick = {
                    onClear?.invoke() ?: onValueChange("")
                }) {
                    Icon(Icons.Default.Clear, contentDescription = "Clear")
                }
            }
        } else null,
        enabled = enabled,
        readOnly = readOnly
    )
}

@Preview(showBackground = true)
@Composable
fun SearchFieldPreview() {
    FiscalCompassTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SearchField(
                value = "",
                onValueChange = {}
            )
            SearchField(
                value = "Search query",
                onValueChange = {},
                placeholder = "Search transactions..."
            )
        }
    }
}

