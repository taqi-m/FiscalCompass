package com.fiscal.compass.ui.components.input

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fiscal.compass.R
import com.fiscal.compass.ui.theme.FiscalCompassTheme

/**
 * A specialized text field for password input with visibility toggle.
 * Includes optional strength validation and custom validation rules.
 */
@Composable
fun PasswordField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    label: String = "Password",
    placeholder: String = "Enter your password",
    imeAction: ImeAction = ImeAction.Done,
    error: String? = null,
    required: Boolean = false,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    showIcon: Boolean = true,
    showToggle: Boolean = true,
    helperText: String? = null,
    validateStrength: Boolean = false,
    minLength: Int = 6
) {
    var passwordVisible by remember { mutableStateOf(false) }

    // Password strength validation
    val strengthError = if (validateStrength && value.isNotEmpty()) {
        when {
            value.length < minLength -> "Password must be at least $minLength characters"
            !value.any { it.isDigit() } -> "Password must contain at least one number"
            !value.any { it.isUpperCase() } -> "Password must contain at least one uppercase letter"
            !value.any { it.isLowerCase() } -> "Password must contain at least one lowercase letter"
            else -> null
        }
    } else null

    val displayError = error ?: strengthError

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
            onValueChange = onValueChange,
            placeholder = { Text(placeholder) },
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 500.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = imeAction
            ),
            leadingIcon = if (showIcon) {
                { Icon(Icons.Default.Lock, contentDescription = "Password") }
            } else null,
            trailingIcon = if (showToggle && value.isNotEmpty()) {
                {
                    val icon: Int = if(passwordVisible) R.drawable.ic_visibility_off_24 else R.drawable.ic_visibility_24
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            painter = painterResource(icon),
                            contentDescription = "Toggle password visibility"
                        )
                    }
                }
            } else null,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
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
fun PasswordFieldPreview() {
    FiscalCompassTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            PasswordField(
                value = "",
                onValueChange = {},
                modifier = Modifier.padding(bottom = 16.dp)
            )
            PasswordField(
                value = "mypassword",
                onValueChange = {},
                validateStrength = true,
                helperText = "Must contain uppercase, lowercase, and numbers",
                modifier = Modifier.padding(bottom = 16.dp)
            )
            PasswordField(
                value = "weak",
                onValueChange = {},
                validateStrength = true,
                minLength = 8
            )
        }
    }
}

