package com.fiscal.compass.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fiscal.compass.presentation.utilities.CurrencyFormater


@Composable
fun ListTable(
    data: Map<String, Double>,
    amountHeader: String,
    modifier: Modifier = Modifier
) {
    Card (
        modifier = modifier,
        colors = CardDefaults.cardColors().copy(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(8.dp).padding(horizontal = 8.dp)
        ) {
            //Header Row
            val headingStyle = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Bold
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Category",
                    style = headingStyle
                )
                Text(
                    text = amountHeader,
                    style = headingStyle
                )
            }

            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
            )

            //Data Rows
            val dataRowStyle = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.W500
            )
            data.forEach { row ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier.weight(1f),
                        text = row.key,
                        style = dataRowStyle
                    )
                    Text(
                        text = CurrencyFormater.formatCurrency(row.value),
                        style = dataRowStyle
                    )
                }
                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth(),
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                )
            }

            //Footer Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = "Total",
                    style = headingStyle
                )
                Text(
                    text = CurrencyFormater.formatCurrency(
                        data.values.sum()
                    ),
                    style = headingStyle
                )
            }
        }

    }
}

@Preview
@Composable
fun ListTablePreview() {
    val rows = mapOf(
        "Groceries" to 150.75,
        "Utilities" to 85.50,
        "Rent" to 1200.00,
        "Transport" to 50.00
    )
    ListTable(
        data = rows,
        amountHeader = "Amount"
    )
}
