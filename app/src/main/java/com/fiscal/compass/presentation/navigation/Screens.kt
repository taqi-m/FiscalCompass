package com.fiscal.compass.presentation.navigation

import com.fiscal.compass.R

/**
 * Sealed class for all top-level navigation routes.
 *
 * Route objects are split across focused files in the same package:
 *  - AddTransactionScreens.kt  — AddTransactionGraph, AddTransaction, Amount, EditAmount, inline selections
 *  - SearchScreens.kt          — SearchGraph, Search, SearchFilters, inline selections
 *  - Screens.kt (this file)    — Auth, Home, Settings and all other standalone routes
 */
sealed class MainScreens(val route: String) {

    // ── Auth / Home ───────────────────────────────────────────────────────────
    object Auth : MainScreens("auth_screen")
    object Initialization : MainScreens("initialization_screen")
    object Home : MainScreens("home_screen")
    object EmployeeHome : MainScreens("employee_screen")
    object AdminHome : MainScreens("admin_home_screen")

    // ── App-level standalone screens ─────────────────────────────────────────
    object Settings : MainScreens("settings_screen")
    object Sync : MainScreens("sync_screen")
    object Categories : MainScreens("categories_screen")
    object Person : MainScreens("person_screen")
    object Jobs : MainScreens("jobs_screen")
    object CreateUser : MainScreens("create_user_screen")

    object TransactionDetail : MainScreens("transaction_detail_screen/{transaction}") {
        fun passTransaction(transaction: String): String =
            "transaction_detail_screen/$transaction"
    }

    object AddPerson : MainScreens("add_person_screen/{selectedType}") {
        fun passSelectedType(selectedType: String): String =
            "add_person_screen/$selectedType"
    }

    object EditPerson : MainScreens("edit_person_screen/{personJson}") {
        fun passPersonJson(personJson: String): String =
            "edit_person_screen/$personJson"
    }
}

// ── Home bottom-nav tabs ──────────────────────────────────────────────────────

sealed class HomeBottomScreen(
    val route: String,
    val label: String,
    val unselectedIcon: Int,
    val selectedIcon: Int
) {
    object Dashboard : HomeBottomScreen(
        "dashboard", "Home",
        R.drawable.ic_outlined_dashboard_panel_24,
        R.drawable.ic_filled_dashboard_panel_24
    )
    object Analytics : HomeBottomScreen(
        "analytics", "Analysis",
        R.drawable.chart_outlined_24,
        R.drawable.chart_bold_24
    )
    object Categories : HomeBottomScreen(
        "categories", "Categories",
        R.drawable.ic_outlined_categories,
        R.drawable.ic_filled_categories
    )
    object People : HomeBottomScreen(
        "people", "People",
        R.drawable.ic_outlined_employees_24,
        R.drawable.ic_filled_employees_24
    )
    object Users : HomeBottomScreen(
        "users", "Users",
        R.drawable.ic_outlined_employees_24,
        R.drawable.ic_filled_employees_24
    )
}