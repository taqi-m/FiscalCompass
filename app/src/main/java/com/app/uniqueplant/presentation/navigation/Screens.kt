package com.app.uniqueplant.presentation.navigation

import com.app.uniqueplant.R

sealed class MainScreens(val route: String) {
    object Auth : MainScreens("auth_screen")
    object Initialization : MainScreens("initialization_screen")
    object Home : MainScreens("home_screen")
    object EmployeeHome : MainScreens("employee_screen")
    object AdminHome : MainScreens("admin_home_screen")
    object AddTransaction : MainScreens("add_transaction_screen")
    object Settings : MainScreens("settings_screen")
    object Sync : MainScreens("sync_screen")
    object Categories : MainScreens("categories_screen")
    object Person : MainScreens("person_screen")
    object Jobs : MainScreens("jobs_screen")
    object Search : MainScreens("search_screen")

    object MultiSelection : MainScreens("multi_selection_screen/{allIds}/{title}/{navKey}") {
        fun passParameters(allIds: String, title: String, navKey: String): String {
            return "multi_selection_screen/$allIds/$title/$navKey"
        }
    }

    object PersonSelection : MainScreens("person_selection_screen/{preSelectedIds}") {
        fun passPersonIds(ids: String): String {
            return "person_selection_screen/$ids"
        }
    }

    object TransactionDetail : MainScreens("transaction_detail_screen/{transaction}"){
        fun passTransaction(transaction: String): String {
            return "transaction_detail_screen/$transaction"
        }
    }
}

sealed class HomeBottomScreen(val route: String, val label: String, val unselectedIcon: Int,val selectedIcon: Int) {
    object Dashboard : HomeBottomScreen("dashboard", "Home", R.drawable.ic_outlined_dashboard_panel_24, selectedIcon = R.drawable.ic_filled_dashboard_panel_24)
//    object Transactions : HomeBottomScreen("transactions", "Records", R.drawable.ic_compare_arrows_24)
    object Analytics : HomeBottomScreen("analytics", "Analysis", R.drawable.ic_outlined_data_report_24, selectedIcon = R.drawable.ic_filled_data_report_24)
    object Categories : HomeBottomScreen("categories", "Categories", R.drawable.ic_outlined_categories, selectedIcon = R.drawable.ic_filled_categories)
    object People : HomeBottomScreen("people", "People", R.drawable.ic_outlined_employees_24, selectedIcon = R.drawable.ic_filled_employees_24)

}