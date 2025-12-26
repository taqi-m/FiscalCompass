package com.fiscal.compass.ui.components.buttons

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fiscal.compass.R

@Composable
fun CardTextButton(
    modifier: Modifier = Modifier,
    cardColors: CardColors = CardDefaults.cardColors(),
    icon: Painter,
    text: String,
    onClick: () -> Unit
) {
    Card (
        modifier = modifier,
        colors = cardColors,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxHeight()
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ){
            Icon(
                painter = icon,
                contentDescription = text
            )
            Text(
                modifier = Modifier.fillMaxWidth().weight(1f),
                text = text,
                style = MaterialTheme.typography.labelLarge,
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun CardTextButtonPreview(){
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        CardTextButton(
            modifier = Modifier.weight(1f).height(90.dp),
            icon = painterResource(R.drawable.ic_delete_24),
            text = "Delete",
            onClick = {}
        )
        CardTextButton(
            modifier = Modifier.weight(1f).height(90.dp),
            icon = painterResource(R.drawable.ic_edit_24),
            text = "Edit",
            onClick = {}
        )
    }
}