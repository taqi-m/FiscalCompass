package com.app.uniqueplant.ui.components.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.uniqueplant.R
import com.app.uniqueplant.presentation.screens.dashboard.HomeOption
import com.app.uniqueplant.ui.theme.UniquePlantTheme



@Composable
fun OptionsRow(
    modifier: Modifier = Modifier,
    options: List<HomeOption>
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        options.forEach { option ->
            OptionCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(84.dp),
                title = option.title,
                description = option.description,
                painter = option.icon,
                onClick = option.onClick
            )
        }

        // Add some space at the bottom to ensure proper padding when scrolling
        Spacer(modifier = Modifier.height(8.dp))
    }
}
@Composable
fun OptionCard(
    modifier: Modifier = Modifier,
    title: String,
    description: String,
    painter: Painter,
    onClick: () -> Unit
) {
    ElevatedCard (
        modifier = modifier,
        colors = CardDefaults.cardColors().copy(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        onClick = onClick
    ) {
        OptionCardContent(
            modifier = Modifier.padding(16.dp),
            title = title,
            description = description,
            painter = painter
        )
    }
}


@Preview
@Composable
fun OptionsRowPreview() {
    UniquePlantTheme {
        val options = listOf(
            HomeOption(
                title = "Option 1",
                description = "This is option 1",
                icon = painterResource(id = R.drawable.ic_category_02),
                onClick = {}
            ),
            HomeOption(
                title = "Option 2",
                description = "This is option 2",
                icon = painterResource(id = R.drawable.ic_person_02),
                onClick = {}
            )
        )
        OptionsRow(
            modifier = Modifier.padding(8.dp),
            options = options
        )
    }
}


@Composable
fun OptionCardContent(
    modifier: Modifier = Modifier,
    title: String,
    description: String,
    painter: Painter
) {
    Row(
        modifier = modifier
            .fillMaxSize()
            .then(
                Modifier.padding(horizontal = 8.dp)
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            painter = painter,
            contentDescription = null,
            modifier = Modifier
                .width(48.dp)
                .aspectRatio(1 / 1f)
                .clip(RoundedCornerShape(50))
                .background(MaterialTheme.colorScheme.onSurface.copy(0.1f))
                .padding(12.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Text(
                text = title,
                maxLines = 1,
                style = MaterialTheme.typography.labelLarge
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                maxLines = 1,
                style = MaterialTheme.typography.labelMedium
            )
        }
        Icon(
            painter = painterResource(R.drawable.ic_arrow_forward_ios_24),
            contentDescription = null
        )
    }
}