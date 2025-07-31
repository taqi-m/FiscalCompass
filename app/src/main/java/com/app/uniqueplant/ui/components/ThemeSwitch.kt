package com.app.uniqueplant.ui.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.uniqueplant.R
import com.app.uniqueplant.ui.util.LocalThemeMode
import com.app.uniqueplant.ui.util.ThemeMode

/*

@Composable
fun rememberThemeState(): MutableState<Boolean> {
    val dataStore = DatastoreManager.getDataStore()
    return remember { ThemePreferenceState(dataStore) }
}

*/


@Composable
fun ThemeSwitch(
    modifier: Modifier = Modifier,
    onSwitchChange: (Boolean) -> Unit
){
    val currentThemeMode = LocalThemeMode.current
    val switchValue = when (currentThemeMode) {
        ThemeMode.FOLLOW_SYSTEM -> isSystemInDarkTheme()
        ThemeMode.DARK -> true
        ThemeMode.LIGHT -> false
    }
    var isInDarkMode by remember { mutableStateOf(switchValue) } // Default to light mode
    Card(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SwitchText(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp),
                text = "App Theme",
                isInDarkMode = isInDarkMode
            )
            Switch(
                modifier = Modifier.padding(16.dp),
                checked = !isInDarkMode,
                onCheckedChange = { isChecked ->
                    isInDarkMode = !isChecked
                    onSwitchChange(isChecked)
                },
                thumbContent = {
                    ThumbContent(
                        checked = !isInDarkMode
                    )
                }
            )
        }
    }
}

@Composable
fun ThumbContent(
    modifier: Modifier = Modifier,
    checked: Boolean = false
) {
    val iconResource = if (checked) {
        R.drawable.ic_light_mode_24
    } else {
        R.drawable.ic_nightlight_24
    }

    Icon(
        painter = painterResource(iconResource),
        contentDescription = null,
        modifier = Modifier.size(SwitchDefaults.IconSize),
    )
}

@Composable
fun SwitchText(
    modifier: Modifier = Modifier,
    text: String,
    isInDarkMode: Boolean
) {
    val subtitle = if (isInDarkMode) {
        "Dark Mode"
    } else {
        "Light Mode"
    }
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = "App Theme",
            style = MaterialTheme.typography.bodyLarge,
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}




@Preview
@Composable
fun ThemeSwitchPreview() {
    ThemeSwitch(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        onSwitchChange = {}
    )
}
