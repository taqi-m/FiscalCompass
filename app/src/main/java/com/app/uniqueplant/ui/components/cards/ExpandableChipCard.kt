package com.app.uniqueplant.ui.components.cards

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.Wallpapers
import androidx.compose.ui.unit.dp
import com.app.uniqueplant.ui.theme.UniquePlantTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest

@Composable
fun <T> ExpandableChipCard(
    modifier: Modifier = Modifier,
    title: String,
    trailingIcon: (@Composable () -> Unit)? = null,
    chips: List<T>,
    onChipClick: ((T) -> Unit)? = null,
    onChipLongClick: ((T) -> Unit)? = null,
    initiallyExpanded: Boolean = false,
    chipToLabel: (T) -> String
) {
    ChipCardItemsContainer(
        modifier = modifier,
        title = title,
        trailingIcon = trailingIcon,
        initiallyExpanded = initiallyExpanded,
    ) {
        ChipCardItemsContainer(
            chips = chips,
            onChipClick = onChipClick,
            onChipLongClick = onChipLongClick,
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
    content: @Composable () -> Unit,
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors().copy(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
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
    onChipClick: ((T) -> Unit)? = null,
    onChipLongClick: ((T) -> Unit)? = null,
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
                onChipLongClick = onChipLongClick,
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <T> ChipCardItem(
    modifier: Modifier = Modifier,
    option: T,
    label: String,
    icon: @Composable (() -> Unit)? = null,
    onChipClick: ((T) -> Unit)? = null,
    onChipLongClick: ((T) -> Unit)? = null

){
    val context = LocalContext.current

    val interactionSource = remember { MutableInteractionSource() }

    val viewConfiguration = LocalViewConfiguration.current


    LaunchedEffect(interactionSource) {
        var isLongClick = false

        interactionSource.interactions.collectLatest { interaction ->
            when (interaction) {
                is PressInteraction.Press -> {
                    isLongClick = false
                    delay(viewConfiguration.longPressTimeoutMillis)
                    isLongClick = true
                    onChipLongClick?.invoke(option)
                }

                is PressInteraction.Release -> {
                    if (isLongClick.not()) {
                        onChipClick?.invoke(option)
                    }

                }

            }
        }
    }
    FilterChip(
        onClick = { /* This is overridden by combinedClickable */ },
        modifier = modifier,
        interactionSource = interactionSource,
        selected = false,
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

@Preview(wallpaper = Wallpapers.RED_DOMINATED_EXAMPLE, showBackground = true)
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
            onChipLongClick = { chip ->
                // Handle chip long click
            },
            initiallyExpanded = true,
            chipToLabel = { it }
        )
    }
}