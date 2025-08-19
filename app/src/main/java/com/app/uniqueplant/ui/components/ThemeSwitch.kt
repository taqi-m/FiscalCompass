package com.app.uniqueplant.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.uniqueplant.ui.theme.UniquePlantTheme
import com.app.uniqueplant.ui.util.LocalThemeMode
import com.app.uniqueplant.ui.util.ThemeMode


@Composable
fun ThemeSwitch(
    modifier: Modifier = Modifier,
    onSwitchChange: (ThemeMode) -> Unit
) {
    val currentThemeMode = LocalThemeMode.current
    val switchValue = when (currentThemeMode) {
        ThemeMode.FOLLOW_SYSTEM -> isSystemInDarkTheme()
        ThemeMode.DARK -> true
        ThemeMode.LIGHT -> false
    }

    var isDialogVisible by remember { mutableStateOf(false) }

    Card(
        modifier = modifier,
        onClick = {
            isDialogVisible = !isDialogVisible
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .padding(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Dark Mode",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = when(currentThemeMode) {
                    ThemeMode.FOLLOW_SYSTEM -> "Follow system"
                    ThemeMode.DARK -> "Dark"
                    ThemeMode.LIGHT -> "Light"
                },
                style = MaterialTheme.typography.bodyMedium

            )

        }
    }

    if (isDialogVisible){
        SingleChoiceDialog(
            title = "Dark Mode",
            options = listOf("Follow System", "Dark", "Light"),
            selectedOption = when (currentThemeMode) {
                ThemeMode.FOLLOW_SYSTEM -> "Follow System"
                ThemeMode.DARK -> "Dark"
                ThemeMode.LIGHT -> "Light"
            },
            onOptionSelected = { option ->
                when (option) {
                    "Follow System" -> onSwitchChange(ThemeMode.FOLLOW_SYSTEM)
                    "Dark" -> onSwitchChange(ThemeMode.DARK)
                    "Light" -> onSwitchChange(ThemeMode.LIGHT)
                }
                isDialogVisible = false
            },
            onDismiss = { isDialogVisible = false },
        )
    }
}


@Composable
fun SingleChoiceDialog(
    title: String = "Select an Option",
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column {
                options.forEach { option ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOptionSelected(option) },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (option == selectedOption),
                            onClick = { onOptionSelected(option) }
                        )
                        Text(option, modifier = Modifier.padding(start = 8.dp))
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton (onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Preview
@Composable
fun SingleChoiceDialogPreview() {
    val options = listOf("Option 1", "Option 2", "Option 3")
    var selectedOption by remember { mutableStateOf(options[0]) }

    SingleChoiceDialog(
        options = options,
        selectedOption = selectedOption,
        onOptionSelected = { selectedOption = it },
        onDismiss = {}
    )
}



@Preview
@Composable
fun ThemeSwitchPreview() {
    UniquePlantTheme {
        ThemeSwitch(
            modifier = Modifier
                .fillMaxWidth(),
            onSwitchChange = {}
        )
    }
}
