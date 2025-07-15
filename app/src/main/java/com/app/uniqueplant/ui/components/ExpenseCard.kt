package com.app.uniqueplant.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.uniqueplant.R
import com.app.uniqueplant.domain.usecase.CurrencyFormaterUseCase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Placeholder for ExpenseCard



@Composable
fun ExpenseCard(
    modifier: Modifier = Modifier,
    expense: Double,
    date: Date,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.then(
            Modifier
                .fillMaxWidth()
        ),
        onClick = onClick,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_arrow_24), // Replace with your income icon
                contentDescription = "Income Icon",
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier
                    .rotate(-45f)
                    .padding(all = 16.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(
                    space = 4.dp,
                    alignment = Alignment.CenterVertically
                )
            ) {
                Text(
                    text = CurrencyFormaterUseCase.formatCurrency(amount = expense, isExpense = true),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.error
                )
                Text(
                    text = SimpleDateFormat("EEE - dd MMM, yyyy", Locale.getDefault()).format(date),
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Icon(
                painter = painterResource(id = R.drawable.ic_more_vert_24), // Replace with your income icon
                contentDescription = "Income Icon",
                modifier = Modifier
                    .padding(all = 16.dp)
            )
        }
    }
}

@Preview
@Composable
fun ExpenseCardPreview() {
    ExpenseCard(
        expense = 1200.50,
        date = Date(),
        onClick = {}
    )
}
