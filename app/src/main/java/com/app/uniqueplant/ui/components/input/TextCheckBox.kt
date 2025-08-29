package com.app.uniqueplant.ui.components.input

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Checkbox
import androidx.compose.ui.Alignment

@Composable
fun TextCheckBox(
    modifier: Modifier = Modifier,
    text: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row (
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ){
        Checkbox(
            checked = isChecked,
            onCheckedChange = onCheckedChange
        )
        Text(
            text = text,
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp)
        )
    }
}

@Preview
@Composable
fun TextCheckBoxPreview() {
    TextCheckBox(
        text = "Sample Text",
        isChecked = true,
        onCheckedChange = {}
    )
}

