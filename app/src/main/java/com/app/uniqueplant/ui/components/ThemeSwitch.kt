package com.app.uniqueplant.ui.components

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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.uniqueplant.R
import com.app.uniqueplant.data.datasource.preferences.ThemePreferenceState


@Composable
fun rememberThemeState(): MutableState<Boolean> {
    val dataStore = DatastoreManager.getDataStore()
    return remember { ThemePreferenceState(dataStore) }
}



@Composable
fun ThemeSwitch(
    modifier: Modifier = Modifier,
    onSwitchChange: (Boolean) -> Unit
){
    var switchState by rememberThemeState()
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
                checked = switchState
            )
            Switch(
                modifier = Modifier.padding(16.dp),
                checked = switchState,
                onCheckedChange = { isChecked ->
                    switchState = isChecked
                    onSwitchChange(isChecked)
                },
                thumbContent = {
                    ThumbContent(
                        checked = switchState
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
        R.drawable.ic_nightlight_24
    } else {
        R.drawable.ic_light_mode_24 // Placeholder for unchecked state
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
    checked: Boolean
) {
    val subtitle = if (checked) {
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
