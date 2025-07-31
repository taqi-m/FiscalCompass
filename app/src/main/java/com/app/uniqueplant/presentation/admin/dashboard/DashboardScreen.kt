package com.app.uniqueplant.presentation.admin.dashboard

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.uniqueplant.R
import com.app.uniqueplant.domain.usecase.CurrencyFormaterUseCase

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun DashboardScreen(
    state: DashboardScreenState,
    onEvent: (DashboardEvent) -> Unit,
) {
    HeroSection(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        userInfo = state.userInfo,
        onEvent = onEvent,
    )
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun DashboardScreenPreview() {
    val state = DashboardScreenState(
        userInfo = UserInfo()
    )
    DashboardScreen(
        state = state,
        onEvent = {}
    )
}


@Composable
fun HeroSection(
    modifier: Modifier = Modifier,
    userInfo: UserInfo,
    onEvent: (DashboardEvent) -> Unit,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.SpaceAround
    ) {
        HeroText(
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .wrapContentSize(Alignment.Center),
            userInfo = userInfo,
        )

        HeroActionButtonRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            onEvent = onEvent,
        )
    }
}

@Composable
fun HeroText(
    modifier: Modifier = Modifier,
    userInfo: UserInfo,
) {
    Column(
        modifier = modifier
            .wrapContentHeight()
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = buildAnnotatedString {
                withStyle(style = MaterialTheme.typography.bodyLarge.toSpanStyle()) {
                    append("Welcome, ")
                }
                withStyle(
                    style = MaterialTheme.typography.bodyLarge.toSpanStyle()
                        .copy(fontWeight = FontWeight.Bold)
                ) {
                    append(userInfo.name)
                }
            },
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = CurrencyFormaterUseCase.formatCurrency(userInfo.balance),
            style = MaterialTheme.typography.headlineLarge,
        )
    }
}


@Composable
fun HeroActionButtonRow(
    modifier: Modifier = Modifier,
    onEvent: (DashboardEvent) -> Unit,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        HeroActionButton(
            modifier = Modifier.weight(1f),
            icon = painterResource(id = R.drawable.ic_add_24),
            label = "Add Entry",
            onClick = {
                //onEvent(DashboardEvent.OnAddTransactionClick)
            }
        )
        HeroActionButton(
            modifier = Modifier.weight(1f),
            icon = painterResource(id = R.drawable.ic_list_24),
            label = "Transactions",
            onClick = {
                //onEvent(DashboardEvent.OnAddTransactionClick)
            }
        )
        HeroActionButton(
            modifier = Modifier.weight(1f),
            icon = painterResource(id = R.drawable.ic_more_horiz_48),
            label = "More",
            onClick = {
                //onEvent(DashboardEvent.OnAddTransactionClick)
            }
        )
    }
}


@Composable
fun HeroActionButton(
    modifier: Modifier = Modifier,
    icon: Painter,
    label: String,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .padding(8.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        FilledTonalIconButton(
            onClick = onClick
        ) {
            Icon(
                modifier = Modifier.padding(4.dp),
                painter = icon,
                contentDescription = "Action Button",
                tint = MaterialTheme.colorScheme.primary
            )
        }
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}


@Preview(
    showBackground = true
)
@Composable
fun HeroSectionPreview() {
    val userInfo = UserInfo(name = "John Doe", balance = 45028.0)
    HeroSection(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        userInfo = userInfo,
        onEvent = {}
    )
}
