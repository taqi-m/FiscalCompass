package com.fiscal.compass.domain.util

import java.util.Locale.getDefault

enum class TransactionType {
    INCOME,
    EXPENSE;

    companion object {

        fun getTransactionTypeList(): List<String> {
            return entries.map { it.name }
        }

        fun fromString(value: String): TransactionType {
            val caseInsensitiveValue = value.uppercase(getDefault())
            return when (caseInsensitiveValue) {
                "INCOME" -> INCOME
                "EXPENSE" -> EXPENSE
                else -> throw IllegalArgumentException("Invalid transaction type: $value")
            }
        }

        fun toString(transactionType: TransactionType): String {
            return when (transactionType) {
                INCOME -> "INCOME"
                EXPENSE -> "EXPENSE"
            }
        }
    }
}