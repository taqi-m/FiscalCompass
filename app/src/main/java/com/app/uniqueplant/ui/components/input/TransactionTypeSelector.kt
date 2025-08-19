package com.app.uniqueplant.ui.components.input

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.uniqueplant.presentation.model.TransactionType

@Composable
fun TransactionTypeSelector(
    modifier: Modifier = Modifier,
    selectedOption: TransactionType,
    onOptionSelected: (TransactionType) -> Unit
) {
    OutlinedCard (
        modifier = modifier,
        shape = RoundedCornerShape(4.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        )
        {
            SingleCheckBox(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .fillMaxWidth()
                    .weight(1f),
                text = TransactionType.EXPENSE.name,
                isChecked = selectedOption.name == TransactionType.EXPENSE.name,
                onCheckedChange = { isChecked ->
                    if (isChecked) {
                        onOptionSelected(TransactionType.EXPENSE)
                    }
                }
            )
            VerticalDivider(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
                thickness = 2.dp
            )
            SingleCheckBox(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .fillMaxWidth()
                    .weight(1f),
                text = TransactionType.INCOME.name,
                isChecked = selectedOption.name == TransactionType.INCOME.name,
                onCheckedChange = { isChecked ->
                    if (isChecked) {
                        onOptionSelected(TransactionType.INCOME)
                    }
                }
            )
        }
    }
}


@Preview(
    showBackground = true
)
@Composable
fun SingleCheckBoxSelectionGroupPreview() {
    val options = listOf("Expense", "Income")
    TransactionTypeSelector(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        selectedOption = TransactionType.EXPENSE,
        onOptionSelected = {}
    )
}

@Composable
fun SingleCheckBox(
    modifier: Modifier = Modifier,
    text: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = modifier.clickable(
            interactionSource = null,
            indication = null,
            onClick = { onCheckedChange(!isChecked) }
        ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Checkbox(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
        )
        Text(
            text,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier
                .padding(start = 8.dp)
//                .clickable(onClick = { onCheckedChange(!isChecked) })
        )
    }
}