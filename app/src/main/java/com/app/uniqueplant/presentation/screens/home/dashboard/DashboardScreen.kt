package com.app.uniqueplant.presentation.screens.home.dashboard

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.app.uniqueplant.presentation.utilities.CurrencyFormater
import com.app.uniqueplant.ui.theme.UniquePlantTheme


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun DashboardScreen(
    state: DashboardScreenState,
    onEvent: (DashboardEvent) -> Unit,
    appNavController: NavHostController,
) {
    onEvent(DashboardEvent.OnScreenLoad(appNavController))
    DashboardScreenContent(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState()),
        state = state,
        onEvent = onEvent,
    )
}


@Composable
private fun DashboardScreenContent(
    modifier: Modifier = Modifier,
    state: DashboardScreenState,
    onEvent: (DashboardEvent) -> Unit,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(
            space = 16.dp,
            alignment = Alignment.Top
        )
    ) {
        BalanceOverview(
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .wrapContentSize(Alignment.Center),
            userInfo = state.userInfo,
        )

        TopCategoriesSection(
            modifier = Modifier
                .fillMaxWidth()
        )
    }
}


@Composable
private fun BalanceOverview(
    modifier: Modifier = Modifier,
    userInfo: UserInfo,
) {
    Column(
        modifier = modifier
            .wrapContentHeight()
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.5f),
                shape = MaterialTheme.shapes.medium
            )
            .padding(24.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = "Total Balance",
            style = MaterialTheme.typography.titleMedium,
        )
        Text(
            text = CurrencyFormater.formatCurrency(userInfo.balance),
            style = MaterialTheme.typography.titleLarge,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun BalanceOverviewPreview() {
    val userInfo = UserInfo(name = "John Doe", balance = 12345.67)
    UniquePlantTheme {
        BalanceOverview(userInfo = userInfo)
    }
}


@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun TopCategoriesSection(
    modifier: Modifier = Modifier,
    topCategories: List<String> = listOf("Groceries", "Entertainment", "Utilities")
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
        Text(text = "Top Charts", style = MaterialTheme.typography.titleMedium)

        when {
            topCategories.isEmpty() ->
                EmptyPlaceholder()

            else ->
                NumberedList(items = topCategories)
        }
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
private fun NumberedList(
    items: List<String>
) {
    items.forEachIndexed { index, category ->
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val textStyle = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.W500
                )

                val hashtagPart = "#${index + 1}"
                val categoryPart = category

                Text(
                    text = buildAnnotatedString {
                        pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
                        append("$hashtagPart ")
                        pop()
                        append(" ")
                        append(categoryPart)
                    },
                    style = textStyle,
                    fontSize = textStyle.fontSize,
                    letterSpacing = TextUnit.Unspecified
                )
            }
            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
            )
        }
    }
}


@Preview(
    showBackground = true
)
@Composable
private fun TopCategoriesSectionPreview() {
    UniquePlantTheme {
        TopCategoriesSection(
            topCategories = listOf(
                "Groceries",
                "Entertainment",
                "Utilities"
            )
        )
    }
}

@Preview(
    showBackground = true,
    device = "spec:width=1080px,height=2340px,dpi=440",
    showSystemUi = false,
)
@Composable
fun DashboardScreenPreview() {
    val state = DashboardScreenState(
        userInfo = UserInfo()
    )
    UniquePlantTheme {
        DashboardScreen(
            appNavController = rememberNavController(),
            state = state,
            onEvent = {}
        )
    }
}