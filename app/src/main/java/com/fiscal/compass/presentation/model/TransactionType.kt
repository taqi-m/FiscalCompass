package com.fiscal.compass.presentation.model

enum class TransactionType {
    EXPENSE,
    INCOME;

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