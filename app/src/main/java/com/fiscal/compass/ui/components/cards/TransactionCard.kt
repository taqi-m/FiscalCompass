package com.fiscal.compass.ui.components.cards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fiscal.compass.R
import com.fiscal.compass.domain.model.Transaction
import com.fiscal.compass.presentation.utilities.CurrencyFormater
import com.fiscal.compass.presentation.utilities.DateFormatter
import java.util.Date

@Composable
fun TransactionCard(
    modifier: Modifier = Modifier,
    transaction: Transaction,
    onClicked: () -> Unit,
    onEditClicked: () -> Unit,
    onDeleteClicked: () -> Unit
) {
    Card(
        modifier = modifier.then(
            Modifier
                .fillMaxWidth()
        ),
        colors = CardDefaults.cardColors().copy(
            containerColor = MaterialTheme.colorScheme.surfaceContainer.copy(0.8f),
        ),
        onClick = onClicked
    ) {
        TransactionCardContent(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .padding(16.dp),
            transaction = transaction,
            onEditClicked = onEditClicked,
            onDeleteClicked = onDeleteClicked
        )
    }
}


@Composable
fun TransactionCardContent(
    modifier: Modifier,
    transaction: Transaction,
    onEditClicked: () -> Unit,
    onDeleteClicked: () -> Unit
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier
                .rotate(
                    if (transaction.isExpense) -45f else 45f
                ),
            painter = painterResource(id = R.drawable.ic_arrow_24),
            tint = if (transaction.isExpense) {
                MaterialTheme.colorScheme.error
            } else {
                LocalContentColor.current
            },
            contentDescription = "Income Icon",
        )
        Spacer(modifier = Modifier.width(16.dp))
        TransactionCardText(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            amount = transaction.amount,
            date = transaction.date
        )
        /*TransactionCardOptions(
            onEditClicked = onEditClicked,
            onDeleteClicked = onDeleteClicked
        )*/
    }
}


@Composable
fun TransactionCardText(
    modifier: Modifier = Modifier,
    amount: Double,
    date: Date
) {
    val parsedAmount = CurrencyFormater.formatCurrency(amount)
    val formattedTime = DateFormatter.provideFormattedTime(date)
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = parsedAmount,
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = formattedTime,
            style = MaterialTheme.typography.bodySmall
        )
    }
}


@Preview
@Composable
fun TransactionCardPreview() {
    TransactionCard(
        transaction = Transaction.sampleExpense(),
        onClicked = {},
        onEditClicked = {},
        onDeleteClicked = {},
    )
}


