package com.fiscal.compass.presentation.utils

/**
 * Immutable state container for calculator operations.
 * This class holds all the necessary state for a calculator instance
 * and can be reused across different calculator components.
 *
 * @param displayText The text currently displayed on the calculator
 * @param firstOperand The first operand for binary operations
 * @param operation The current operation being performed (null if no operation)
 * @param clearOnNextInput Whether to clear the display on next input
 * @param errorState Whether the calculator is in an error state
 */
data class CalculatorState(
    val displayText: String = "0",
    val firstOperand: Double = 0.0,
    val operation: String? = null,
    val clearOnNextInput: Boolean = false,
    val errorState: Boolean = false
) {
    companion object {
        /**
         * Creates a default calculator state
         */
        fun default() = CalculatorState()

        /**
         * Creates a calculator state with a specific initial value
         */
        fun withInitialValue(value: String) = CalculatorState(displayText = value)
    }
}
