package com.fiscal.compass.presentation.model

data class IncomeUi(
    val incomeId: Long = 0,
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
        val dummy = IncomeUi(
            incomeId = 1L,
            formatedAmount = "$1,000.00",
            formatedAmountPaid = "$600.00",
            formatedRemainingAmount = "$400.00",
            formatedDate = "15 Nov, 2025",
            formatedTime = "10:30",
            description = "Freelance Project Payment",
            isFullyPaid = false,
            paymentProgressPercentage = 60
        )

        val dummyFullyPaid = IncomeUi(
            incomeId = 2L,
            formatedAmount = "$500.00",
            formatedAmountPaid = "$500.00",
            formatedRemainingAmount = "$0.00",
            formatedDate = "20 Nov, 2025",
            formatedTime = "14:00",
            description = "Consulting Fee",
            isFullyPaid = true,
            paymentProgressPercentage = 100
        )

        val dummyUnpaid = IncomeUi(
            incomeId = 3L,
            formatedAmount = "$2,500.00",
            formatedAmountPaid = "$0.00",
            formatedRemainingAmount = "$2,500.00",
            formatedDate = "25 Nov, 2025",
            formatedTime = "09:00",
            description = "Client Invoice #123",
            isFullyPaid = false,
            paymentProgressPercentage = 0
        )

        val dummyList = listOf(dummy, dummyFullyPaid, dummyUnpaid)
    }
}

