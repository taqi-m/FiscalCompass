package com.app.uniqueplant.ui.components.input

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
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
//        isError = isError,
        singleLine = true,
        keyboardOptions = keyboardOptions,
        placeholder = {
            Text(
                text = placeholder ?: "Enter $label",
                style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
            )
        },
        /*supportingText = {
            if (isError && errorMessage != null) {
                Text(text = errorMessage)
            }
        }*/
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputTextField(
    modifier: Modifier = Modifier,
    label: String,
    placeholder: String? = null,
    value: String,
    leadingIcon: Any? = null, // Can be ImageVector or Painter
    onValueChange: (String) -> Unit,
    isError: Boolean = false,
    errorMessage: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default.copy(
        imeAction = ImeAction.Next
    )
) {
    TooltipBox(
        positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
        tooltip = {
            if (isError && !errorMessage.isNullOrEmpty()) {
                PlainTooltip {
                    Text(errorMessage)
                }
            }
        },
        state = rememberTooltipState()
    ) {
        val interactionSource = remember { MutableInteractionSource() }
        var isFocused by remember { mutableStateOf(false) }

        // Observe focus changes
        LaunchedEffect(interactionSource) {
            interactionSource.interactions.collect { interaction ->
                isFocused = interaction is FocusInteraction.Focus
            }
        }
        val outlineAndIconColor = when {
            isError -> MaterialTheme.colorScheme.error
            isFocused -> MaterialTheme.colorScheme.primary
            else -> MaterialTheme.colorScheme.outline
        }

        val textFieldShape = RoundedCornerShape(15)
        Row(
            modifier = modifier.then(
                Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        color = outlineAndIconColor,
                        shape = textFieldShape
                    )
                    .clip(
                        textFieldShape
                    )
            ),
            verticalAlignment = Alignment.CenterVertically
        )
        {
            leadingIcon?.let {
                when (it) {
                    is ImageVector -> Icon(
                        imageVector = it,
                        contentDescription = label,
                        modifier = Modifier.padding(start = 12.dp),
                        tint = outlineAndIconColor
                    )

                    is Painter -> Icon(
                        painter = it,
                        contentDescription = label,
                        modifier = Modifier.padding(start = 12.dp),
                        tint = outlineAndIconColor
                    )
                }
            }
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Transparent),
                value = value,
                interactionSource = interactionSource,
                onValueChange = onValueChange,
//            label = { Text(text = label) },
                singleLine = true,
                keyboardOptions = keyboardOptions,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    errorIndicatorColor = Color.Transparent
                ),
                placeholder = {
                    Text(text = placeholder ?: "Enter $label")
                },
                trailingIcon = {
                    if (isError) {
                        Icon(
                            painterResource(R.drawable.ic_error_24),
                            "Error",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun InputTextFieldPreview() {
    var text by remember { mutableStateOf("") }
    InputTextField(
        modifier = Modifier.padding(12.dp),
        label = "Username",
        placeholder = "Enter your username",
        value = text,
        onValueChange = { text = it },
        isError = true,
        errorMessage = "Invalid username format",
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = ImeAction.Done
        ),
        leadingIcon = Icons.Default.Person
    )
}


@Preview(showBackground = true)
@Composable
fun InputTextFieldWithoutErrorPreview() {
    var text by remember { mutableStateOf("") }
    InputTextField(
        modifier = Modifier.padding(12.dp),
        label = "Username",
        placeholder = "Enter your username",
        value = text,
        onValueChange = { text = it },
        isError = false,
        errorMessage = "Invalid username format",
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = ImeAction.Done
        ),
        leadingIcon = Icons.Default.Person
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
//            isError = isError,
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
            /*supportingText = {
                if (isError && errorMessage != null) {
                    Text(text = errorMessage)
                }
            }*/
        )
    }
}