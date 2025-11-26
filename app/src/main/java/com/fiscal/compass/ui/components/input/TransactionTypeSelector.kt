package com.fiscal.compass.ui.components.input

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fiscal.compass.presentation.model.TransactionType

@Composable
fun TransactionTypeSelector(
    modifier: Modifier = Modifier,
    selectedOption: TransactionType,
    onOptionSelected: (TransactionType) -> Unit
) {
    Row (
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SingleCheckBox(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            text = TransactionType.EXPENSE.name,
            isChecked = selectedOption.name == TransactionType.EXPENSE.name,
            onCheckedChange = { isChecked ->
                if (isChecked) {
                    onOptionSelected(TransactionType.EXPENSE)
                }
            }
        )
        SingleCheckBox(
            modifier = Modifier
                .fillMaxSize()
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


@Preview(
    showBackground = true
)
@Composable
fun SingleCheckBoxSelectionGroupPreview() {
    listOf("Expense", "Income")
    TransactionTypeSelector(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(8.dp),
        selectedOption = TransactionType.EXPENSE,
        onOptionSelected = {}
    )
}

@Preview(
    showBackground = true
)
@Composable
fun SingleCheckBoxPreview() {
    SingleCheckBox(
        text = "Sample Checkbox",
        isChecked = true,
        onCheckedChange = {}
    )
}

@Composable
fun SingleCheckBox(
    modifier: Modifier = Modifier,
    text: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {

    @OptIn(ExperimentalMaterial3Api::class)
    FilterChip(
        selected = isChecked,
        onClick = { onCheckedChange(!isChecked) },
        label = {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = text,
                style = MaterialTheme.typography.labelLarge,
                textAlign = TextAlign.Center
            )
        },
        modifier = modifier
    )
}