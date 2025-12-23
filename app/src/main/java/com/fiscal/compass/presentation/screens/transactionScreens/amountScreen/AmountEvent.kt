package com.fiscal.compass.presentation.screens.transactionScreens.amountScreen

import com.fiscal.compass.domain.model.Transaction
import com.fiscal.compass.presentation.utils.AmountInputType

sealed class AmountEvent {
    data class LoadTransaction(val transaction: Transaction, val editMode: Boolean) : AmountEvent()
    data class OnAmountChange(val amount: Double, val inputType: AmountInputType = AmountInputType.TOTAL_AMOUNT) : AmountEvent()
    data class OnAmountPaidChange(val amountPaid: Double) : AmountEvent()
    object OnSaveClicked : AmountEvent()
}