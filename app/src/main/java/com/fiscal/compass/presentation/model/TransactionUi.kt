package com.fiscal.compass.presentation.model

data class TransactionUi(
    val transactionId: Long,
    val formatedAmount: String,
    val formatedPaidAmount: String = "",
    val formatedRemainingAmount: String = "",
    val categoryId: Long,
    val personId: Long? = null,
    val formatedDate: String = "",
    val formatedTime: String,
    val description: String? = null,
    val isExpense: Boolean,
    val transactionType : String,
    val isFullyPaid: Boolean = false,
    val paymentProgressPercentage: Int = 0
) {
    companion object {
        val dummy = TransactionUi(
            transactionId = 1L,
            formatedAmount = "$100.00",
            formatedPaidAmount = "$60.00",
            formatedRemainingAmount = "$40.00",
            categoryId = 1L,
            personId = 1L,
            formatedDate = "06 Sep 2025",
            formatedTime = "10:00 AM",
            description = "Dummy transaction",
            isExpense = true,
            transactionType = "Expense",
            isFullyPaid = false,
            paymentProgressPercentage = 60
        )

        val dummyFullyPaid = TransactionUi(
            transactionId = 2L,
            formatedAmount = "$50.00",
            formatedPaidAmount = "$50.00",
            formatedRemainingAmount = "$0.00",
            categoryId = 2L,
            personId = null,
            formatedDate = "08 Sep 2025",
            formatedTime = "02:30 PM",
            description = "Fully paid income",
            isExpense = false,
            transactionType = "Income",
            isFullyPaid = true,
            paymentProgressPercentage = 100
        )

        val dummyUnpaid = TransactionUi(
            transactionId = 3L,
            formatedAmount = "$200.00",
            formatedPaidAmount = "$0.00",
            formatedRemainingAmount = "$200.00",
            categoryId = 3L,
            personId = 2L,
            formatedDate = "10 Sep 2025",
            formatedTime = "09:15 AM",
            description = "Unpaid expense",
            isExpense = true,
            transactionType = "Expense",
            isFullyPaid = false,
            paymentProgressPercentage = 0
        )

        val dummyList = listOf(
            dummy,
            dummyFullyPaid,
            dummyUnpaid
        )
    }
}

