package com.fiscal.compass.presentation.navigation

// ── AddTransaction graph routes ───────────────────────────────────────────────

object AddTransactionGraph : MainScreens("add_transaction_graph")

object AddTransaction : MainScreens("add_transaction_screen?transactionId={transactionId}") {
    fun passTransactionId(transactionId: Long): String =
        "add_transaction_screen?transactionId=$transactionId"
}

/** Inline category selection — lives inside AddTransactionGraph, reads state from shared VM */
object AddTxCategorySelection : MainScreens("add_tx_category_selection")

/** Inline person selection — lives inside AddTransactionGraph, reads state from shared VM */
object AddTxPersonSelection : MainScreens("add_tx_person_selection")

/**
 * Amount entry screen inside AddTransactionGraph.
 * Transaction is passed through shared AddTransactionViewModel state — only editMode is a nav arg.
 */
object Amount : MainScreens("amount_screen/{edit}") {
    fun navigate(editMode: Boolean = false): String = "amount_screen/$editMode"
}

/**
 * Standalone edit-amount route used by TransactionDetailsScreen (outside AddTransactionGraph).
 * Retains the transaction JSON nav arg since it has no access to the shared AddTransactionViewModel.
 */
object EditAmount : MainScreens("edit_amount_screen/{transaction}/{edit}") {
    fun editTransaction(transaction: String): String = "edit_amount_screen/$transaction/true"
}

