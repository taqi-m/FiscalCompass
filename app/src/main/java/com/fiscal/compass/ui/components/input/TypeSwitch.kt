package com.fiscal.compass.ui.components.input

import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun TypeSwitch(
    modifier: Modifier = Modifier,
    shape: CornerBasedShape = MaterialTheme.shapes.extraLarge,
    typeOptions: List<String>,
    selectedTypeIndex: Int,
    onTypeSelected: (Int) -> Unit,
) {

    SingleChoiceSegmentedButtonRow(
        modifier = modifier,
    ) {
        typeOptions.forEachIndexed { index, label ->
            SegmentedButton(
                shape = SegmentedButtonDefaults.itemShape(
                    index = index,
                    count = typeOptions.size,
                    baseShape = shape
                ),
                onClick = {
                    onTypeSelected(index)
                },
                selected = index == selectedTypeIndex,
                label = {
                    Text(label)
                }
            )
        }
    }
}

@Preview
@Composable
fun TypeSwitchPreview() {
    TypeSwitch(
        typeOptions = listOf("Income", "Expense"),
        selectedTypeIndex = 0,
        onTypeSelected = {}
    )
}