package com.app.uniqueplant.presentation.model

enum class TransactionType {
    INCOME,
    EXPENSE;

    companion object {
        fun fromString(value: String): TransactionType {
            return when (value.lowercase()) {
                "income" -> INCOME
                "expense" -> EXPENSE
                else -> throw IllegalArgumentException("Unknown transaction type: $value")
            }
        }
    }
}