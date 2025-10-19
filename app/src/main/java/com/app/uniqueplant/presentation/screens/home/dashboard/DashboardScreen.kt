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
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.tooling.preview.Wallpapers
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.app.uniqueplant.R
import com.app.uniqueplant.domain.usecase.CurrencyFormaterUseCase
import com.app.uniqueplant.ui.components.cards.OptionsRow
import com.app.uniqueplant.ui.theme.UniquePlantTheme


private data class HomeHeroActionButton(
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
        modifier = Modifier
            .fillMaxSize()
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
        /*HomeOption(
            title = "Jobs",
            description = "Manage all jobs",
            icon = painterResource(id = R.drawable.ic_jobs_48),
            onClick = {
                onEvent(DashboardEvent.OnJobsClicked)
            }
        )*/
    )
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(
            space = 16.dp,
            alignment = Alignment.Top
        )
    ) {
        HeroSection(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            userInfo = state.userInfo,
            onEvent = onEvent,
        )

        OptionsRow(
            options = options,
            modifier = Modifier
                .padding(16.dp)
        )
    }
}


@Preview(
    showBackground = true,
    showSystemUi = true,
    wallpaper = Wallpapers.NONE,
    device = "spec:width=1080px,height=2340px,dpi=440", backgroundColor = 0xFF9C27B0
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
private fun HeroSection(
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
                onEvent(DashboardEvent.OnSynClicked)
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
private fun HeroText(
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
private fun HeroActionButtonRow(
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
private fun HeroActionButton(
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
            style = MaterialTheme.typography.labelLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}
