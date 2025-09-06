package com.app.uniqueplant.presentation.model

data class TransactionUi(
    val transactionId: Long,
    val formatedAmount: String,
    val categoryId: Long,
    val personId: Long? = null,
    val formatedDate: String = "",
    val formatedTime: String,
    val description: String? = null,
    val isExpense: Boolean,
    val transactionType : String,
) {
    companion object {
        val dummy = TransactionUi(
            transactionId = 1L,
            formatedAmount = "$100.00",
            categoryId = 1L,
            personId = 1L,
            formatedDate = "06 Sep 2025",
            formatedTime = "10:00 AM",
            description = "Dummy transaction",
            isExpense = true,
            transactionType = "Expense"
        )

        val dummyList = listOf(
            dummy,
            dummy.copy(transactionId = 2L, formatedAmount = "$50.00", isExpense = false, transactionType = "Income", description = "Another dummy transaction")
        )
    }
}