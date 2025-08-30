package com.app.uniqueplant.ui.components.cards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.uniqueplant.ui.theme.UniquePlantTheme

@Composable
fun <T> ExpandableChipCard(
    modifier: Modifier = Modifier,
    title: String,
    trailingIcon: @Composable () -> Unit = {},
    chips: List<T>,
    onChipClick: (T) -> Unit,
    initiallyExpanded: Boolean = false,
    chipToLabel: (T) -> String
) {
    ChipCardItemsContainer(
        modifier = modifier,
        title = title,
        trailingIcon = trailingIcon,
        initiallyExpanded = initiallyExpanded
    ) {
        ChipCardItemsContainer(
            chips = chips,
            onChipClick = onChipClick,
            chipToLabel = chipToLabel
        )
    }
}




@Composable
fun ChipCardItemsContainer(
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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
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


@Composable
fun <T> ChipCardItemsContainer(
    chips: List<T>,
    onChipClick: (T) -> Unit,
    chipToLabel: (T) -> String
) {
    if (chips.isEmpty()){
        Text(
            text = "No items available",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            textAlign = TextAlign.Center
        )
        return
    }
    FlowRow(
        modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start
    )
    {
        chips.forEach { chip ->
            ChipCardItem (
                option = chip,
                onChipClick = onChipClick,
                modifier = Modifier
                    .padding(4.dp),
                label = chipToLabel(chip)
            )
        }
        /*ChipCardItem(
            // For "Add New", the option type might be tricky.
            // We can cast it, but a more robust solution might involve a sealed class for chip types.
            option = "Add New",
            icon = {
                 Icon(Icons.Default.Add, contentDescription = "Add New")
            },
            onChipClick = { onAddNewClicked() },
            modifier = Modifier
                .padding(4.dp),
            label = "Add"
        )*/
    }
}

@Composable
fun <T> ChipCardItem(
    modifier: Modifier = Modifier,
    option: T,
    label: String,
    icon: @Composable (() -> Unit)? = null,
    onChipClick: (T) -> Unit,

){
    FilterChip(
        modifier = modifier,
        selected = false,
        onClick = { onChipClick(option) },
        leadingIcon = icon,
        label = {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.W600
                )
            )
        }
    )
}


@Preview
@Composable
fun FlowRowChipsPreview() {
    val chips = listOf("Overtime", "Salary", "Transport", "Chip 4", "Chip 5")
    ChipCardItemsContainer<String>(
        chips = chips,
        onChipClick = {},
        chipToLabel = { it }
    )
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
            trailingIcon = {
                FilledTonalIconButton (
                    modifier = Modifier.height(32.dp).width(32.dp),
                    shape = RoundedCornerShape(15),
                    onClick = { /* Handle filter icon click */ }
                ) {
                    Icon(
                        modifier = Modifier.height(24.dp),
                        imageVector = Icons.Default.Add,
                        contentDescription = "Filter"
                    )
                }
            },
            chips = chips,
            onChipClick = { chip ->
                // Handle chip click
            },
            initiallyExpanded = true,
            chipToLabel = { it }
        )
    }
}