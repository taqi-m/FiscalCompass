package com.app.uniqueplant.ui.components.input

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.uniqueplant.R

@Composable
fun DataEntryTextField(
    modifier: Modifier = Modifier,
    label: String,
    placeholder: String? = null,
    value: String,
    onValueChange: (String) -> Unit,
    isError: Boolean = false,
    errorMessage: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default.copy(
        imeAction = ImeAction.Next
    )
) {
    OutlinedTextField(
        modifier = modifier,
        value = value,
        onValueChange = onValueChange,
        label = { Text(text = label) },
        isError = isError,
        singleLine = true,
        keyboardOptions = keyboardOptions,
        placeholder = {
            Text(
                text = placeholder ?: "Enter $label",
                style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
            )
        },
        supportingText = {
            if (isError && errorMessage != null) {
                Text(text = errorMessage)
            }
        }
    )
}

@Composable
fun ReadOnlyDataEntryTextField(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    isError: Boolean = false,
    errorMessage: String? = null,
    onClick: () -> Unit = {},
) {
    // Create a box with the clickable modifier to capture clicks
    Box(
        modifier = modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = onClick
        )
    ) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = value,
            onValueChange = {},
            label = { Text(text = label) },
            readOnly = true,
            enabled = false,
            isError = isError,
            colors = OutlinedTextFieldDefaults.colors(
                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                disabledContainerColor = MaterialTheme.colorScheme.surface,
                disabledBorderColor = MaterialTheme.colorScheme.outline,
                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            ),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next
            ),
            supportingText = {
                if (isError && errorMessage != null) {
                    Text(text = errorMessage)
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ReadOnlyDataEntryTextFieldPreview() {
    ReadOnlyDataEntryTextField(
        modifier = Modifier.padding(8.dp),
        label = "Start Date",
        value = "01/01/2023",
        isError = false,
        errorMessage = null,
    )
}

@Preview(showBackground = true)
@Composable
fun DataEntryTextFieldPreview() {
    var value by remember { mutableStateOf("") }
    DataEntryTextField(
        modifier = Modifier.padding(8.dp),
        label = "Label",
        placeholder = "Enter value",
        value = value,
        onValueChange = {
            value = it
        }
    )
}

@Preview(showBackground = true)
@Composable
fun DataEntryTextFieldWithErrorPreview() {
    DataEntryTextField(
        modifier = Modifier.padding(8.dp),
        label = "Label",
        value = "Value",
        onValueChange = {},
        isError = true,
        errorMessage = "Error message"
    )
}