package com.fiscal.compass.domain.util

import java.util.Locale.getDefault

enum class TransactionTypes {
    INCOME,
    EXPENSE;

    companion object {

        fun getTransactionTypeList(): List<String> {
            return entries.map { it.name }
        }

        fun fromString(value: String): TransactionTypes {
            val caseInsensitiveValue = value.uppercase(getDefault())
            return when (caseInsensitiveValue) {
                "INCOME" -> INCOME
                "EXPENSE" -> EXPENSE
                else -> throw IllegalArgumentException("Invalid transaction type: $value")
            }
        }

        fun toString(transactionType: TransactionTypes): String {
            return when (transactionType) {
                INCOME -> "INCOME"
                EXPENSE -> "EXPENSE"
            }
        }
    }
}