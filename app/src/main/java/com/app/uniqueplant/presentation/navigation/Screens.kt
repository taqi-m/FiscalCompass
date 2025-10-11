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

    object TransactionDetail : MainScreens("transaction_detail_screen/{transaction}"){
        fun passTransaction(transaction: String): String {
            return "transaction_detail_screen/$transaction"
        }
    }
}

sealed class HomeBottomScreen(val route: String, val label: String, val iconResource: Int) {
    object Dashboard : HomeBottomScreen("dashboard", "Home", R.drawable.ic_home_24)
    object Transactions : HomeBottomScreen("transactions", "Records", R.drawable.ic_list_24)
    object Analytics : HomeBottomScreen("analytics", "Reports", R.drawable.ic_bar_chart_24)
    object Categories : HomeBottomScreen("categories", "Categories", R.drawable.ic_category_24)

}