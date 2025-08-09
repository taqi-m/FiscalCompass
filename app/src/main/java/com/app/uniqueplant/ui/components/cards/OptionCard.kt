package com.app.uniqueplant.ui.components.cards

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.uniqueplant.R
import com.app.uniqueplant.presentation.admin.dashboard.HomeOption


@Composable
fun OptionCard(
    modifier: Modifier = Modifier,
    title: String,
    painter: Painter,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier,
        onClick = onClick
    ) {
        OptionCardContent(
            modifier = Modifier.padding(16.dp),
            title = title,
            painter = painter
        )
    }
}


@Composable
fun OptionsRow(
    modifier: Modifier = Modifier,
    options: List<HomeOption>
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        options.forEach { option ->
            OptionCard(
                modifier = Modifier
                    .padding(8.dp)
                    .weight(1f)
                    .aspectRatio(3f / 4f),
                title = option.title,
                painter = option.icon,
                onClick = option.onClick
            )
        }
    }
}


@Composable
fun OptionCardContent(
    modifier: Modifier = Modifier,
    title: String,
    painter: Painter
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Image(
            painter = painter,
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .aspectRatio(1f),
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
        )
        Text(
            text = title,
            maxLines = 1,
            style = MaterialTheme.typography.labelMedium
        )
    }
}