package com.fiscal.compass.ui.components.cards

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.Wallpapers
import androidx.compose.ui.unit.dp
import com.fiscal.compass.ui.theme.FiscalCompassTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest

@Composable
fun <T> ExpandableChipCard(
    modifier: Modifier = Modifier,
    headerItem: T,
    showIcon: Boolean = false,
    trailingIcon: (@Composable () -> Unit)? = null,
    chips: List<T>,
    onCardClick: (() -> Unit)? = null,
    onCardLongClick: (() -> Unit)? = null,
    onChipClick: ((T) -> Unit)? = null,
    onChipLongClick: ((T) -> Unit)? = null,
    initiallyExpanded: Boolean = false,
    chipToLabel: (T) -> String
) {
    ChipCardItemsContainer(
        modifier = modifier,
        title = chipToLabel(headerItem),
        showIcon = showIcon,
        trailingIcon = trailingIcon,
        initiallyExpanded = initiallyExpanded,
        onClick = {
            onCardClick?.invoke()
        },
        onLongClick = {
            onCardLongClick?.invoke()
        }
    ) {
        ChipFlow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
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
    showIcon: Boolean = false,
    initiallyExpanded: Boolean = false,
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    chipsFlow: @Composable () -> Unit,
) {
    Card(
        modifier = modifier
            .clip(
                CardDefaults.shape
            )
            .combinedClickable(
            enabled = true,
            onClick = onClick ?: {},
            onLongClick = onLongClick ?: {},
        ),
        colors = CardDefaults.cardColors().copy(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        CardHeader(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 8.dp)
                .padding(end = 8.dp),
            title = title,
            showIcon = showIcon,
            trailingIcon = trailingIcon
        )

        HorizontalDivider(
            modifier = Modifier.padding(top = 4.dp),
            thickness = 1.dp
        )

        chipsFlow()
    }
}

@Composable
private fun CardHeader(
    modifier: Modifier = Modifier,
    showIcon: Boolean = false,
    icon: @Composable (() -> Unit)? = null,
    title: String,
    trailingIcon: @Composable (() -> Unit)? = null
){
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        if (showIcon){
            Surface(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .size(40.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = title.first().toString(),
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            }
        }
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.weight(1f).padding(start = if (showIcon) 0.dp else 8.dp),
        )
        trailingIcon?.let { it() }
    }
}


@Composable
fun <T> ChipFlow(
    modifier: Modifier = Modifier,
    placeholder: String = "No items available",
    chips: List<T>,
    onChipClick: ((T) -> Unit)? = null,
    onChipLongClick: ((T) -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    maxLines: Int = Int.MAX_VALUE,
    chipToLabel: (T) -> String
) {
    if (chips.isEmpty()){
        Text(
            text = placeholder,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            textAlign = TextAlign.Center
        )
        return
    }
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        maxLines = maxLines,
    ) {
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
    FiscalCompassTheme {
        ExpandableChipCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            headerItem = "Expandable Chip Card",
            trailingIcon = {
                FilledTonalIconButton (
                    modifier = Modifier
                        .height(32.dp)
                        .width(32.dp),
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