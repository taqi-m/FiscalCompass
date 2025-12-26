package com.fiscal.compass.ui.components.input

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun <T> SingleSelectionChipGroup(
    modifier: Modifier = Modifier,
    items: List<T>,
    chipToLabel: (T) -> String,
    onSelectionChanged: ((T) -> Unit)? = null,
    maxLines: Int = Int.MAX_VALUE,
) {

    if (items.isEmpty()){
        return@SingleSelectionChipGroup
    }

    var selectedItem by remember { mutableStateOf(items.first()) }

    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        maxLines = maxLines,
    ) {
        items.forEach { chip ->
            SelectableChip (
                modifier = Modifier
                    .padding(4.dp),
                label = chipToLabel(chip),
                isSelected = chip == selectedItem,
                onChipClick = {
                    selectedItem = chip
                    onSelectionChanged?.invoke(chip)
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FilterChipGroupExamplePreview() {
    SingleSelectionChipGroup(
        items = listOf("Chip 1", "Chip 2", "Chip 3"),
        chipToLabel = { it }
    )
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SelectableChip(
    modifier: Modifier = Modifier,
    label: String,
    isSelected: Boolean = false,
    onChipClick: () -> Unit,
){
    FilterChip(
        onClick = onChipClick,
        modifier = modifier,
        selected = isSelected,
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

