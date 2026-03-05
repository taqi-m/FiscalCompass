package com.fiscal.compass.presentation.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.fiscal.compass.domain.model.base.Person
import com.fiscal.compass.domain.repository.AppPreferenceRepository
import com.fiscal.compass.domain.service.analytics.AnalyticsService
import com.fiscal.compass.presentation.screens.transactionScreens.navigation.AddTransactionNavGraph
import com.fiscal.compass.presentation.screens.search.navigation.SearchNavGraph
import com.fiscal.compass.presentation.screens.auth.AuthScreen
import com.fiscal.compass.presentation.screens.auth.AuthViewModel
import com.fiscal.compass.presentation.screens.category.CategoriesScreen
import com.fiscal.compass.presentation.screens.category.CategoriesViewModel
import com.fiscal.compass.presentation.screens.home.home.HomeScreen
import com.fiscal.compass.presentation.screens.home.home.HomeViewModel
import com.fiscal.compass.presentation.screens.jobs.JobsScreen
import com.fiscal.compass.presentation.screens.jobs.JobsViewModel
import com.fiscal.compass.presentation.screens.person.PersonNavigation
import com.fiscal.compass.presentation.screens.person.PersonScreen
import com.fiscal.compass.presentation.screens.person.PersonViewModel
import com.fiscal.compass.presentation.screens.person.addperson.AddPersonScreen
import com.fiscal.compass.presentation.screens.person.addperson.AddPersonViewModel
import com.fiscal.compass.presentation.screens.person.editperson.EditPersonEvent
import com.fiscal.compass.presentation.screens.person.editperson.EditPersonScreen
import com.fiscal.compass.presentation.screens.person.editperson.EditPersonViewModel
import com.fiscal.compass.presentation.screens.settings.SettingsScreen
import com.fiscal.compass.presentation.screens.settings.SettingsViewModel
import com.fiscal.compass.presentation.screens.sync.SyncScreen
import com.fiscal.compass.presentation.screens.sync.SyncViewModel
import com.fiscal.compass.presentation.screens.transactionScreens.transactionDetails.TransactionDetailsScreen
import com.fiscal.compass.presentation.screens.transactionScreens.transactionDetails.TransactionDetailsViewModel
import com.fiscal.compass.presentation.screens.users.createuser.CreateUserScreen
import com.fiscal.compass.presentation.screens.users.createuser.CreateUserViewModel
import com.google.gson.Gson

@Composable
fun AppNavigation(
    navController: NavHostController,
    prefs: AppPreferenceRepository,
    analyticsService: AnalyticsService
) {
    // Track screen views on navigation changes
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    DisposableEffect(navBackStackEntry) {
        navBackStackEntry?.destination?.route?.let { route ->
            val screenName = route.substringBefore("/").substringBefore("?")
            analyticsService.logScreenView(screenName)
        }
        onDispose { }
    }

    NavHost(
        navController = navController,
        startDestination =
            if (prefs.isUserLoggedIn()) {
                MainScreens.AdminHome.route
            } else {
                MainScreens.Auth.route
            }
    ) {

        composable(route = MainScreens.Auth.route) { backStackEntry ->
            val authViewModel: AuthViewModel = hiltViewModel(backStackEntry)
            val authState by authViewModel.state.collectAsState()
            val initializationStatus by authViewModel.initializationStatus.collectAsState()
            AuthScreen(
                appNavController = navController,
                state = authState,
                initializationStatus = initializationStatus,
                onEvent = authViewModel::onEvent,
            )
        }

        composable(route = MainScreens.Home.route) { backStackEntry ->
            val homeViewModel: HomeViewModel = hiltViewModel(backStackEntry)
            val homeState by homeViewModel.state.collectAsState()
            HomeScreen(
                appNavController = navController,
                state = homeState,
                onEvent = homeViewModel::onEvent,
                onSettingsClick = { navController.navigate(MainScreens.Settings.route) },
                onSearchClick = { navController.navigate(SearchGraph.route) },
                onSyncClick = { navController.navigate(MainScreens.Sync.route) }
            )
        }

        composable(route = MainScreens.EmployeeHome.route) {
            // User home screen content
        }

        composable(route = MainScreens.AdminHome.route) { backStackEntry ->
            val homeViewModel: HomeViewModel = hiltViewModel(backStackEntry)
            val homeState by homeViewModel.state.collectAsState()
            HomeScreen(
                appNavController = navController,
                state = homeState,
                onEvent = homeViewModel::onEvent,
                onSettingsClick = { navController.navigate(MainScreens.Settings.route) },
                onSearchClick = { navController.navigate(SearchGraph.route) },
                onSyncClick = { navController.navigate(MainScreens.Sync.route) }
            )
        }

        // ── AddTransaction nested graph (AddTransaction, Amount, EditAmount, inline selections)
        AddTransactionNavGraph(navController)

        composable(
            MainScreens.Settings.route,
            enterTransition = { navEnterFromLeft },
            exitTransition = { navExitToLeft },
            popEnterTransition = { navEnterFromLeft },
            popExitTransition = { navExitToLeft }
        ) { backStackEntry ->
            val settingsViewModel: SettingsViewModel = hiltViewModel(backStackEntry)
            val state by settingsViewModel.state.collectAsState()
            val context = LocalContext.current
            SettingsScreen(
                state = state,
                onEvent = settingsViewModel::onEvent,
                appNavController = navController,
                onLogout = { _ ->
                    navController.navigate(MainScreens.Auth.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onDownloadUpdate = { settingsViewModel.downloadUpdate(context) },
                onInstallUpdate = { settingsViewModel.installApk(context) }
            )
        }

        composable(
            MainScreens.Sync.route,
            enterTransition = { navEnterFromUp },
            exitTransition = { navExitToDown },
            popEnterTransition = { navEnterFromUp },
            popExitTransition = { navExitToDown }
        ) { backStackEntry ->
            val syncViewModel: SyncViewModel = hiltViewModel(backStackEntry)
            val state by syncViewModel.state.collectAsState()
            SyncScreen(
                state = state,
                onEvent = syncViewModel::onEvent,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            MainScreens.CreateUser.route,
            enterTransition = { navEnterFromRight },
            exitTransition = { navExitToRight },
            popEnterTransition = { navEnterFromRight },
            popExitTransition = { navExitToRight }
        ) { backStackEntry ->
            val createUserViewModel: CreateUserViewModel =
                hiltViewModel(backStackEntry)
            val state by createUserViewModel.state.collectAsState()
            val hasPermission by createUserViewModel.hasPermission.collectAsState()
            CreateUserScreen(
                state = state,
                hasPermission = hasPermission,
                onEvent = createUserViewModel::onEvent,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = MainScreens.Categories.route,
            enterTransition = { navEnterFromRight },
            exitTransition = { navExitToRight },
            popEnterTransition = { navEnterFromRight },
            popExitTransition = { navExitToRight }
        ) { backStackEntry ->
            val categoriesViewModel: CategoriesViewModel = hiltViewModel(backStackEntry)
            val state by categoriesViewModel.state.collectAsState()
            CategoriesScreen(
                state = state,
                onEvent = categoriesViewModel::onEvent
            )
        }

        composable(
            route = MainScreens.Person.route,
            enterTransition = { navEnterFromRight },
            exitTransition = { navExitToRight },
            popEnterTransition = { navEnterFromRight },
            popExitTransition = { navExitToRight }
        ) {
            val personViewModel: PersonViewModel = hiltViewModel()
            val state by personViewModel.state.collectAsState()

            val operationResult by navController.currentBackStackEntry
                ?.savedStateHandle
                ?.getStateFlow<String?>("operationResult", null)
                ?.collectAsState() ?: remember { mutableStateOf(null) }

            DisposableEffect(operationResult) {
                onDispose {
                    operationResult?.let {
                        navController.currentBackStackEntry
                            ?.savedStateHandle?.remove<String>("operationResult")
                    }
                }
            }

            PersonScreen(
                state = state,
                onEvent = personViewModel::onEvent,
                operationResultMessage = operationResult,
                onNavigate = { navigation ->
                    when (navigation) {
                        is PersonNavigation.NavigateBack ->
                            navController.navigateUp()
                        is PersonNavigation.NavigateToAddPerson -> {
                            val encodedType = Uri.encode(navigation.selectedType)
                            navController.navigate(MainScreens.AddPerson.passSelectedType(encodedType))
                        }
                        is PersonNavigation.NavigateToEditPerson -> {
                            val personJson = Gson().toJson(navigation.person)
                            val encodedPerson = Uri.encode(personJson)
                            navController.navigate(MainScreens.EditPerson.passPersonJson(encodedPerson))
                        }
                    }
                }
            )
        }

        composable(
            route = MainScreens.AddPerson.route,
            enterTransition = { navEnterFromRight },
            exitTransition = { navExitToRight },
            popEnterTransition = { navEnterFromRight },
            popExitTransition = { navExitToRight }
        ) {
            val addPersonViewModel: AddPersonViewModel = hiltViewModel()
            val state by addPersonViewModel.state.collectAsState()
            AddPersonScreen(
                state = state,
                onEvent = addPersonViewModel::onEvent,
                onNavigateBackWithResult = { result ->
                    result?.let {
                        navController.previousBackStackEntry
                            ?.savedStateHandle?.set("operationResult", it)
                    }
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = MainScreens.EditPerson.route,
            enterTransition = { navEnterFromRight },
            exitTransition = { navExitToRight },
            popEnterTransition = { navEnterFromRight },
            popExitTransition = { navExitToRight }
        ) { backStackEntry ->
            val editPersonViewModel: EditPersonViewModel = hiltViewModel()
            val state by editPersonViewModel.state.collectAsState()

            val personJson = remember {
                val encodedJson = backStackEntry.arguments?.getString("personJson")
                Uri.decode(encodedJson ?: "")
            }

            LaunchedEffect(personJson) {
                if (personJson.isNotEmpty()) {
                    val person = Gson().fromJson(personJson, Person::class.java)
                    editPersonViewModel.onEvent(EditPersonEvent.LoadPerson(person))
                }
            }

            EditPersonScreen(
                state = state,
                onEvent = editPersonViewModel::onEvent,
                onNavigateBackWithResult = { result ->
                    result?.let {
                        navController.previousBackStackEntry
                            ?.savedStateHandle?.set("operationResult", it)
                    }
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = MainScreens.Jobs.route,
            enterTransition = { navEnterFromRight },
            exitTransition = { navExitToRight },
            popEnterTransition = { navEnterFromRight },
            popExitTransition = { navExitToRight }
        ) {
            val jobsViewModel: JobsViewModel = hiltViewModel()
            val state by jobsViewModel.state.collectAsState()
            JobsScreen(
                state = state,
                onEvent = jobsViewModel::onEvent
            )
        }

        composable(
            route = MainScreens.TransactionDetail.route,
            enterTransition = { navFadeIn },
            exitTransition = { navFadeOut }
        ) {
            val transactionDetailsViewModel: TransactionDetailsViewModel = hiltViewModel()
            val state by transactionDetailsViewModel.state.collectAsState()
            TransactionDetailsScreen(
                appNavController = navController,
                state = state,
                onEvent = transactionDetailsViewModel::onEvent
            )
        }

        // ── Search nested graph (Search, SearchFilters, inline category/person selections)
        SearchNavGraph(navController)
    }
}
