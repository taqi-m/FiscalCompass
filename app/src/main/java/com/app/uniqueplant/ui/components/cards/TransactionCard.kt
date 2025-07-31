package com.app.uniqueplant.ui.components.cards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import com.app.uniqueplant.R
import com.app.uniqueplant.domain.model.Transaction
import com.app.uniqueplant.domain.model.TransactionType
import com.app.uniqueplant.domain.usecase.CurrencyFormaterUseCase
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun TransactionCard(
    modifier: Modifier = Modifier,
    transaction: Transaction,
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
        )
    ) {
        TransactionCardContent(
            transaction = transaction,
            onEditClicked = onEditClicked,
            onDeleteClicked = onDeleteClicked
        )
    }
}


@Composable
fun TransactionCardContent(
    transaction: Transaction,
    onEditClicked: () -> Unit,
    onDeleteClicked: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier
                .rotate(
                    if (transaction.type == TransactionType.INCOME) 45f else -45f
                )
                .padding(all = 16.dp),
            painter = painterResource(id = R.drawable.ic_arrow_24),
            tint = if (transaction.type == TransactionType.EXPENSE) {
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
            income = transaction.amount,
            date = transaction.date
        )
        TransactionCardOptions(
            onEditClicked = onEditClicked,
            onDeleteClicked = onDeleteClicked
        )
    }
}


@Composable
fun TransactionCardText(
    modifier: Modifier = Modifier,
    income: Double,
    date: Date
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(
            space = 4.dp,
            alignment = Alignment.CenterVertically
        )
    ) {
        Text(
            text = CurrencyFormaterUseCase.formatCurrency(amount = income),
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = SimpleDateFormat("h:mm a", Locale.getDefault()).format(date),
            style = MaterialTheme.typography.bodySmall
        )
    }
}


@Preview
@Composable
fun TransactionCardPreview() {
    TransactionCard(
        transaction = Transaction(
            id = 1L,
            amount = 100.0,
            description = "Sample Transaction",
            date = Calendar.getInstance().apply {
                set(2023, Calendar.OCTOBER, 1, 12, 0)
            }.time,
            categoryId = 0L,
            userId = "user123",
            type = TransactionType.EXPENSE,
            createdAt = Date(),
            updatedAt = Date()
        ),
        onEditClicked = {},
        onDeleteClicked = {},
    )
}


