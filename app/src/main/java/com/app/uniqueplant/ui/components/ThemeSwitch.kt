package com.app.uniqueplant.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
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
import com.app.uniqueplant.ui.util.LocalDarkTheme
import com.app.uniqueplant.ui.util.PreferenceUtil.DarkThemePreference.Companion.FOLLOW_SYSTEM
import com.app.uniqueplant.ui.util.PreferenceUtil.DarkThemePreference.Companion.OFF
import com.app.uniqueplant.ui.util.PreferenceUtil.DarkThemePreference.Companion.ON


@Composable
fun ThemeSwitch(
    modifier: Modifier = Modifier,
    onSwitchChange: (Any) -> Unit
) {
    val currentThemeMode = LocalDarkTheme.current

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
                text = currentThemeMode.getDarkThemeDesc(),
                style = MaterialTheme.typography.bodyMedium
            )

        }
    }

    if (isDialogVisible){
        SingleChoiceDialog(
            itemToString = {
                when (it) {
                    FOLLOW_SYSTEM -> "Follow System"
                    ON -> "Dark"
                    OFF -> "Light"
                    else -> ""
                }
            },
            title = "Dark Mode",
            options = listOf(
                FOLLOW_SYSTEM,
                ON,
                OFF
            ),
            selectedOption = currentThemeMode,
            onOptionSelected = {
                onSwitchChange(it)
                isDialogVisible = false
            },
            onDismiss = { isDialogVisible = false },
        )
    }
}


@Composable
private fun <T> SingleChoiceDialog(
    title: String,
    options: List<T>,
    selectedOption: T,
    onOptionSelected: (T) -> Unit,
    onDismiss: () -> Unit,
    itemToString: (T) -> String = { it.toString() }
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
                        Text(itemToString(option), modifier = Modifier.padding(start = 8.dp))
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
fun ThemeSwitchPreview() {
    UniquePlantTheme {
        ThemeSwitch(
            modifier = Modifier
                .fillMaxWidth(),
            onSwitchChange = {}
        )
    }
}
