package com.app.uniqueplant.ui.components.cards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.uniqueplant.ui.theme.UniquePlantTheme

@Composable
fun ExpandableChipCard(
    modifier: Modifier = Modifier,
    title: String,
    chips: List<String>,
    onChipClick: (String) -> Unit,
    onAddNewClicked: (String) -> Unit,
    initiallyExpanded: Boolean = false
) {
    ExpandableCard(
        modifier = modifier,
        title = title,
        initiallyExpanded = initiallyExpanded
    ) {
        FlowRowChips(
            chips = chips,
            onChipClick = onChipClick,
            onAddNewClicked = onAddNewClicked
        )
    }
}

@Preview
@Composable
fun ExpandableChipCardPreview() {
    val chips = listOf("Chip 1", "Chip 2", "Chip 3", "Chip 4", "Chip 5")
    UniquePlantTheme {
        ExpandableChipCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            title = "Expandable Chip Card",
            chips = chips,
            onChipClick = { chip ->
                // Handle chip click
            },
            initiallyExpanded = true,
            onAddNewClicked = { newChip ->
                // Handle add new chip click
            }
        )
    }
}


@Composable
fun ExpandableCard(
    modifier: Modifier = Modifier,
    title: String,
    trailingIcon: @Composable (() -> Unit)? = null,
    initiallyExpanded: Boolean = false,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors().copy(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                trailingIcon?.let { it() }
            }
            content()
        }
    }
}

@Preview
@Composable
fun FlowRowChipsPreview() {
    val chips = listOf("Overtime", "Salary", "Transport", "Chip 4", "Chip 5")
    FlowRowChips(
        chips = chips,
        onChipClick = {},
        onAddNewClicked = {}
    )
}

@Composable
fun FlowRowChips(
    chips: List<String>,
    onChipClick: (String) -> Unit,
    onAddNewClicked: (String) -> Unit,
) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start
    ) {
        chips.forEach { chip ->
            ExpandableCardChip (
                title = chip,
                onChipClick = onChipClick,
                modifier = Modifier
                    .padding(4.dp)
            )
        }
        ExpandableCardChip(
            title = "Add New",
            icon = {
                 Icon(Icons.Default.Add, contentDescription = "Add New")
            },
            onChipClick = { onAddNewClicked(it) },
            modifier = Modifier
                .padding(4.dp)
        )
    }
}

@Composable
fun ExpandableCardChip(
    modifier: Modifier = Modifier,
    title: String,
    icon: @Composable (() -> Unit)? = null,
    onChipClick: (String) -> Unit,

){
    FilterChip(
        modifier = modifier,
        selected = false,
        onClick = { onChipClick(title) },
        leadingIcon = icon,
        label = {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.W600
                )
            )
        }
    )
}