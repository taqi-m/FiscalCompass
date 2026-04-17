package com.fiscal.compass.presentation.screens.home.dashboard

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.Wallpapers
import androidx.compose.ui.unit.dp
import com.fiscal.compass.domain.model.Transaction
import com.fiscal.compass.presentation.utilities.CurrencyFormater
import com.fiscal.compass.ui.components.cards.TransactionCard
import com.fiscal.compass.ui.theme.FiscalCompassTheme


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun DashboardScreen(
    state: DashboardScreenState,
    onEvent: (DashboardEvent) -> Unit,
    onRecentTransactionClick: (Transaction) -> Unit,
) {
    LaunchedEffect(Unit) {
        onEvent(DashboardEvent.OnScreenLoad)
    }

    DashboardScreenContent(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState()),
        state = state,
        onRecentTransactionClick = onRecentTransactionClick,
    )
}


@Composable
private fun DashboardScreenContent(
    modifier: Modifier = Modifier,
    state: DashboardScreenState,
    onRecentTransactionClick: (Transaction) -> Unit,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(
            space = 16.dp,
            alignment = Alignment.Top
        )
    ) {
        GreetingSection(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            userName = state.overviewData.name
        )

        BalanceOverview(
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .wrapContentSize(Alignment.Center),
            overviewData = state.overviewData,
        )

        RecentTransactionsSection(
            modifier = Modifier.fillMaxWidth(),
            transactions = state.recentTransactions,
            onTransactionClick = onRecentTransactionClick,
        )
    }
}


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun BalanceOverview(
    modifier: Modifier = Modifier,
    overviewData: OverviewData,
) {
    val cardShape = MaterialTheme.shapes.largeIncreased
    val cardElevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp)
    val primary = MaterialTheme.colorScheme.primary
    val primaryContainer = MaterialTheme.colorScheme.primaryContainer
    val contentColor = contentColorFor(MaterialTheme.colorScheme.primary)

    ElevatedCard(
        modifier = modifier
            .wrapContentHeight()
            .fillMaxWidth(),
        shape = cardShape,
        elevation = cardElevation
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .drawWithCache {
                    val gradient = Brush.linearGradient(
                        colors = listOf(
                            primary,
                            primary.copy(alpha = 0.90f),
                            primaryContainer.copy(alpha = 1f),
                        ),
                        start = Offset.Zero,
                        end = Offset(size.width, size.height),
                    )
                    val glow = Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.14f),
                            Color.Transparent,
                        ),
                        center = Offset(size.width * 0.82f, size.height * 0.22f),
                        radius = size.minDimension * 0.9f,
                    )
                    val sparkline = Path().apply {
                        moveTo(size.width * 0.05f, size.height * 0.76f)
                        cubicTo(
                            size.width * 0.20f, size.height * 0.62f,
                            size.width * 0.30f, size.height * 0.80f,
                            size.width * 0.45f, size.height * 0.60f,
                        )
                        cubicTo(
                            size.width * 0.60f, size.height * 0.42f,
                            size.width * 0.72f, size.height * 0.56f,
                            size.width * 0.93f, size.height * 0.34f,
                        )
                    }
                    val lineWidth = 4.dp.toPx()
                    val pointRadius = 6.dp.toPx()

                    onDrawBehind {
                        drawRect(brush = gradient)
                        drawRect(brush = glow)
                        drawPath(
                            path = sparkline,
                            color = contentColor.copy(alpha = 0.24f),
                            style = Stroke(width = lineWidth, cap = StrokeCap.Round),
                        )
                        drawCircle(
                            color = contentColor.copy(alpha = 0.45f),
                            radius = pointRadius,
                            center = Offset(size.width * 0.93f, size.height * 0.34f),
                        )
                    }
                }
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = "TOTAL BALANCE",
                    style = MaterialTheme.typography.titleMediumEmphasized,
                    color = contentColor
                )
                Text(
                    text = CurrencyFormater.formatCurrency(overviewData.currentBalance),
                    style = MaterialTheme.typography.headlineLarge,
                    color = contentColor
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OverviewMini(
                        modifier = Modifier.weight(1f),
                        title = "Incomes",
                        amount = overviewData.income
                    )
                    OverviewMini(
                        modifier = Modifier.weight(1f),
                        title = "Expenses",
                        amount = overviewData.expenses
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun OverviewMini(
    modifier: Modifier = Modifier,
    title: String,
    amount: Double,
) {
    val cardShape = MaterialTheme.shapes.medium
    // Use a contrast-first token that stays readable over the tinted glass card in both themes.
    val contentColor = contentColorFor(MaterialTheme.colorScheme.primary)
    val borderColor = Color.White.copy(alpha = 0.35f)
    val titleStyle = MaterialTheme.typography.labelLargeEmphasized.copy(contentColor)
    val textStyle = MaterialTheme.typography.bodyMediumEmphasized.copy(contentColor)

    Column(
        modifier = modifier
            .wrapContentHeight()
            .fillMaxWidth()
            .clip(cardShape)
            .background(Color.White.copy(alpha = 0.20f))
            .border(
                width = 1.dp,
                color = borderColor,
                shape = cardShape,
            )
            .padding(16.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(text = title, style = titleStyle)
        Text(text = CurrencyFormater.formatCurrency(amount), style = textStyle)
    }
}


//@Preview(showBackground = true)
@Composable
private fun BalanceOverviewPreview() {
    val overviewData = OverviewData(name = "John Doe", currentBalance = 12345.67)
    FiscalCompassTheme {
        BalanceOverview(overviewData = overviewData)
    }
}


@Composable
private fun EmptyPlaceholder() {
    Text(
        text = "Start tracking your expenses/incomes to see where your money goes!",
        style = MaterialTheme.typography.bodyMedium,
    )
}

@Composable
private fun GreetingSection(
    modifier: Modifier = Modifier,
    userName: String = "User"
) {
    Text(
        modifier = modifier,
        text = "Hello, $userName!",
        style = MaterialTheme.typography.headlineMedium
    )
}

@Composable
private fun RecentTransactionsSection(
    modifier: Modifier = Modifier,
    transactions: List<Transaction> = emptyList(),
    onTransactionClick: (Transaction) -> Unit,
) {
    Column(
        modifier = modifier
            .wrapContentHeight()
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.5f),
                MaterialTheme.shapes.medium
            )
            .padding(24.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = "Recent Transactions", style = MaterialTheme.typography.titleMedium)

        when {
            transactions.isEmpty() ->
                EmptyPlaceholder()

            else -> {
                transactions.forEachIndexed { index, transaction ->
                    TransactionCard(
                        transaction = transaction,
                        onClicked = { onTransactionClick(transaction) },
                        onEditClicked = {},
                        onDeleteClicked = {},
                    )

                    if (index != transactions.lastIndex) {
                        HorizontalDivider(
                            modifier = Modifier.fillMaxWidth(),
                            thickness = 1.dp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                        )
                    }
                }
            }
        }
    }
}

//@Preview(showBackground = true)
@Composable
private fun RecentTransactionsSectionPreview() {
    val transactions = listOf(
        Transaction.sampleExpense(),
        Transaction.sampleIncome(),
        Transaction.sampleExpense()
    )
    FiscalCompassTheme {
        RecentTransactionsSection(
            transactions = transactions,
            onTransactionClick = {},
        )
    }
}

@Preview(
    showBackground = true,
    device = "spec:width=1080px,height=2340px,dpi=440",
    showSystemUi = false,
    wallpaper = Wallpapers.BLUE_DOMINATED_EXAMPLE,
)
@Composable
fun DashboardScreenPreview() {
    val state = DashboardScreenState(
        overviewData = OverviewData(
            name = "John Doe",
            currentBalance = 1075900.00,
            income = 1148300.00,
            expenses = 73400.00
        )
    )
    FiscalCompassTheme {
        DashboardScreen(
            state = state,
            onEvent = {},
            onRecentTransactionClick = {},
        )
    }
}