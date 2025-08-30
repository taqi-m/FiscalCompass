package com.app.uniqueplant.presentation.screens.dashboard

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.app.uniqueplant.R
import com.app.uniqueplant.domain.usecase.CurrencyFormaterUseCase
import com.app.uniqueplant.ui.components.cards.OptionsRow
import com.app.uniqueplant.ui.theme.UniquePlantTheme


data class HomeHeroActionButton(
    val icon: Painter,
    val label: String,
    val onClick: () -> Unit
)

data class HomeOption(
    val title: String,
    val description: String,
    val icon: Painter,
    val onClick: () -> Unit
)




@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun DashboardScreen(
    state: DashboardScreenState,
    onEvent: (DashboardEvent) -> Unit,
    appNavController: NavHostController,
) {

    onEvent(DashboardEvent.OnScreenLoad(appNavController))
    DashboardScreenContent(
        modifier = Modifier.fillMaxSize(),
        state = state,
        onEvent = onEvent,
    )
}


@Composable
fun DashboardScreenContent(
    modifier: Modifier = Modifier,
    state: DashboardScreenState,
    onEvent: (DashboardEvent) -> Unit,
) {

    val options = listOf(
        HomeOption(
            title = "Categories",
            description = "Manage your categories",
            icon = painterResource(id = R.drawable.ic_category_02),
            onClick = {
                onEvent(DashboardEvent.OnCategoriesClicked)
            }
        ),
        HomeOption(
            title = "People",
            description = "Manage your people",
            icon = painterResource(id = R.drawable.ic_person_02),
            onClick = {
                onEvent(DashboardEvent.OnPersonsClicked)
            }
        ),
        HomeOption(
            title = "Jobs",
            description = "Manage all jobs",
            icon = painterResource(id = R.drawable.ic_jobs_48),
            onClick = {
                onEvent(DashboardEvent.OnJobsClicked)
            }
        )
    )
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(
            space = 16.dp,
            alignment = Alignment.Top
        )
    ) {
        val cornerSize = 8

        // Fixed Hero Section at the top
        HeroSection(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            userInfo = state.userInfo,
            onEvent = onEvent,
        )

        // Scrollable Options Section that fills remaining height
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f) // Fill remaining space
                .clip(
                    RoundedCornerShape(topStartPercent = cornerSize, topEndPercent = cornerSize)
                )
                .background(
                    color = Color(0xFF808080)
                        .compositeOver(MaterialTheme.colorScheme.surface).copy(alpha = 0.125f)
                )
        ) {
            OptionsRow(
                options = options,
                modifier = Modifier
                    .fillMaxHeight()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            )
        }
    }
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
    UniquePlantTheme {
        Scaffold {
            DashboardScreenContent(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it),
                state = state,
                onEvent = {}
            )
        }
    }
}


@Composable
fun HeroSection(
    modifier: Modifier = Modifier,
    userInfo: UserInfo,
    onEvent: (DashboardEvent) -> Unit,
) {

    val heroOptions = listOf(
        HomeHeroActionButton(
            icon = painterResource(id = R.drawable.ic_add_24),
            label = "Add Entry",
            onClick = {
                onEvent(DashboardEvent.OnAddTransactionClicked)
            }
        ),
        HomeHeroActionButton(
            icon = painterResource(id = R.drawable.ic_cloud_sync_24),
            label = "Sync",
            onClick = {
                //onEvent(DashboardEvent.OnAddTransactionClick)
            }
        ),
        HomeHeroActionButton(
            icon = painterResource(id = R.drawable.ic_more_horiz_48),
            label = "More",
            onClick = {
                //onEvent(DashboardEvent.OnAddTransactionClick)
            }
        )
    )


    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
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
                .padding(8.dp),
            actionButtons = heroOptions
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
    actionButtons: List<HomeHeroActionButton> = emptyList()
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (actionButton in actionButtons) {
            HeroActionButton(
                modifier = Modifier.weight(1f),
                icon = actionButton.icon,
                label = actionButton.label,
                onClick = actionButton.onClick
            )
        }
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
