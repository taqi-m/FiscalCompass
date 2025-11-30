package com.fiscal.compass.presentation.model

data class ExpenseUi(
    val expenseId: Long = 0,
    val formatedAmount: String = "",
    val formatedAmountPaid: String = "",
    val formatedRemainingAmount: String = "",
    val formatedDate: String = "",
    val formatedTime: String,
    val description: String? = null,
    val isFullyPaid: Boolean = false,
    val paymentProgressPercentage: Int = 0
) {
    companion object {
        val dummy = ExpenseUi(
            expenseId = 1L,
            formatedAmount = "$750.00",
            formatedAmountPaid = "$250.00",
            formatedRemainingAmount = "$500.00",
            formatedDate = "18 Nov, 2025",
            formatedTime = "11:30",
            description = "Office Equipment",
            isFullyPaid = false,
            paymentProgressPercentage = 33
        )

        val dummyFullyPaid = ExpenseUi(
            expenseId = 2L,
            formatedAmount = "$300.00",
            formatedAmountPaid = "$300.00",
            formatedRemainingAmount = "$0.00",
            formatedDate = "22 Nov, 2025",
            formatedTime = "15:45",
            description = "Monthly Subscription",
            isFullyPaid = true,
            paymentProgressPercentage = 100
        )

        val dummyUnpaid = ExpenseUi(
            expenseId = 3L,
            formatedAmount = "$1,200.00",
            formatedAmountPaid = "$0.00",
            formatedRemainingAmount = "$1,200.00",
            formatedDate = "28 Nov, 2025",
            formatedTime = "10:00",
            description = "Rent Payment",
            isFullyPaid = false,
            paymentProgressPercentage = 0
        )

        val dummyList = listOf(dummy, dummyFullyPaid, dummyUnpaid)
    }
}

