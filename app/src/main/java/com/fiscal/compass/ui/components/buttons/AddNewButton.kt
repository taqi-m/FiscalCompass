package com.fiscal.compass.ui.components.buttons

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.fiscal.compass.R

@Composable
fun AddNewButton(
    modifier: Modifier = Modifier, text: String, onClick: () -> Unit
) {
    Row(modifier = modifier
        .clip(RoundedCornerShape(4.dp))
        .clickable {
            onClick()
        }
        .padding(8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically) {
        Icon(
            modifier = Modifier.padding(end = 8.dp),
            painter = painterResource(id = R.drawable.ic_add_24),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        Text(
            text = text, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.primary
        )/*
        CustomChip(
                    modifier  = Modifier.fillMaxWidth(),
                    text = "Add Category",
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_add_circle_24),
                            contentDescription = null
                        )
                    },
                    onClick = onClick
                )
                */
    }
}