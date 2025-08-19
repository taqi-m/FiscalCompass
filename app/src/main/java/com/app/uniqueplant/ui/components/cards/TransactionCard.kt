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
import com.app.uniqueplant.presentation.model.TransactionType
import com.app.uniqueplant.domain.usecase.CurrencyFormaterUseCase
import com.app.uniqueplant.presentation.model.TransactionUi
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TransactionCard(
    modifier: Modifier = Modifier,
    transaction: TransactionUi,
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
    transaction: TransactionUi,
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
            income = transaction.formatedAmount,
            time = transaction.formatedTime
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
    income: String,
    time: String
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = income,
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = time,
            style = MaterialTheme.typography.bodySmall
        )
    }
}


@Preview
@Composable
fun TransactionCardPreview() {
    TransactionCard(
        transaction = TransactionUi(
            transactionId = 1L,
            formatedAmount = CurrencyFormaterUseCase.formatCurrency(100.0),
            formatedDate = SimpleDateFormat("dd MM, yyyy", Locale.getDefault()).format(Date()),
            formatedTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()),
            description = "Sample Transaction",
            isExpense = true,
            transactionType = TransactionType.INCOME.name
        ),
        onEditClicked = {},
        onDeleteClicked = {},
    )
}


