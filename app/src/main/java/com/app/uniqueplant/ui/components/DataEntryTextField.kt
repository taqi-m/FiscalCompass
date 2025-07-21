package com.app.uniqueplant.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
    trailingIcon: @Composable () -> Unit = {}
) {
    OutlinedTextField(
        modifier = modifier,
        value = value,
        onValueChange = {},
        label = { Text(text = label) },
        readOnly = true,
        isError = isError,
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = ImeAction.Next
        ),
        supportingText = {
            if (isError && errorMessage != null) {
                Text(text = errorMessage)
            }
        },
        trailingIcon = {
            trailingIcon()
        }
    )
    Modifier
        .padding(8.dp)
//        .then(Modifier.clickable { onClick() })
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
        trailingIcon = {
            Icon(
                painter = painterResource(id = R.drawable.ic_calendar_24),
                contentDescription = "Select Date",
                modifier = Modifier.padding(8.dp),
                tint = MaterialTheme.colorScheme.onSurface,
            )
        }
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