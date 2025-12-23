package com.fiscal.compass.presentation.utils

/**
 * Pure calculator engine that handles all calculator operations.
 * This class is framework-agnostic and contains no Compose dependencies,
 * making it easily testable and reusable.
 */
object CalculatorEngine {

    /**
     * Handles number input in the calculator
     * @param state Current calculator state
     * @param number Number that was input (0-9)
     * @return New calculator state after processing the number input
     */
    fun handleNumberInput(state: CalculatorState, number: Int): CalculatorState {
        return when {
            state.errorState -> {
                // Reset from error state and start with the new number
                CalculatorState(value = number.toDouble())
            }

            state.clearOnNextInput -> {
                // Start new number after operation or equals
                // Preserve operation and firstOperand if they exist (after operator selection)
                state.copy(
                    value = number.toDouble(),
                    clearOnNextInput = false
                )
            }

            else -> {
                // Convert current value to string for manipulation
                val currentValueStr = formatToTwoDecimals(state.value)

                // Check if we already have 2 decimal places
                val decimalIndex = currentValueStr.indexOf('.')
                if (decimalIndex != -1 && currentValueStr.length - decimalIndex > 2) {
                    // Already have 2 decimal places, don't add more digits
                    return state
                }

                // Always append number to the left of decimal point
                val newDisplayText = if (currentValueStr == "0" || currentValueStr == "0.0" || currentValueStr == "0.00") {
                    number.toString()
                } else if (currentValueStr.contains(".")) {
                    // Insert number before the decimal point
                    val decimalPosition = currentValueStr.indexOf('.')
                    currentValueStr.take(decimalPosition) + number + currentValueStr.substring(decimalPosition)
                } else {
                    currentValueStr + number
                }

                val newValue = newDisplayText.toDoubleOrNull() ?: state.value
                state.copy(value = roundToTwoDecimals(newValue))
            }
        }
    }

    /**
     * Handles operator input (+, -, *, ÷)
     * @param state Current calculator state
     * @param operator Operator that was input
     * @return New calculator state after processing the operator
     */
    fun handleOperatorInput(state: CalculatorState, operator: String): CalculatorState {
        return when {
            state.errorState -> {
                // Reset from error state
                CalculatorState()
            }

            else -> {
                try {
                    val value = roundToTwoDecimals(state.value)
                    if (isValueWithinLimits(value)) {
                        state.copy(
                            firstOperand = value,
                            operation = operator,
                            clearOnNextInput = true,
                            value = value
                        )
                    } else {
                        createErrorState()
                    }
                } catch (_: NumberFormatException) {
                    createErrorState()
                }
            }
        }
    }

    /**
     * Handles equals input (=)
     * @param state Current calculator state
     * @return New calculator state after processing equals
     */
    fun handleEqualsInput(state: CalculatorState): CalculatorState {
        return when {
            state.errorState -> {
                // Reset from error state
                CalculatorState()
            }

            state.operation == null -> {
                // No operation to perform, just clear on next input
                state.copy(clearOnNextInput = true)
            }

            else -> {
                try {
                    val secondOperand = roundToTwoDecimals(state.value)

                    if (!isValueWithinLimits(secondOperand)) {
                        return createErrorState()
                    }

                    val rawResult = when (state.operation) {
                        "+" -> state.firstOperand + secondOperand
                        "-" -> state.firstOperand - secondOperand
                        "*" -> state.firstOperand * secondOperand
                        "÷" -> if (secondOperand != 0.0) {
                            state.firstOperand / secondOperand
                        } else {
                            Double.NaN
                        }

                        else -> secondOperand
                    }

                    if (rawResult.isNaN() || rawResult.isInfinite() || !isValueWithinLimits(rawResult)) {
                        return createErrorState()
                    }

                    // Round result to 2 decimal places
                    val result = roundToTwoDecimals(rawResult)

                    state.copy(
                        value = result,
                        operation = null,
                        clearOnNextInput = true
                    )

                } catch (_: NumberFormatException) {
                    createErrorState()
                } catch (_: Exception) {
                    createErrorState()
                }
            }
        }
    }

    /**
     * Handles decimal point input (.)
     * @param state Current calculator state
     * @return New calculator state after processing decimal input
     */
    fun handleDecimalInput(state: CalculatorState): CalculatorState {
        return when {
            state.errorState -> {
                // Reset from error state and start with "0."
                CalculatorState(value = 0.0)
            }

            state.clearOnNextInput -> {
                // Start with "0." after operation or equals
                state.copy(
                    value = 0.0,
                    clearOnNextInput = false
                )
            }

            else -> {
                val currentStr = formatToTwoDecimals(state.value)
                // If already has decimal point, no change
                if (currentStr.contains(".")) {
                    state
                } else {
                    // Add decimal point to current number
                    val newValue = "$currentStr.".toDoubleOrNull() ?: state.value
                    state.copy(value = newValue)
                }
            }
        }
    }

    /**
     * Handles delete/backspace input
     * @param state Current calculator state
     * @return New calculator state after processing delete
     */
    fun handleDeleteInput(state: CalculatorState): CalculatorState {
        return when {
            state.errorState -> {
                // Reset from error state
                CalculatorState()
            }

            else -> {
                val currentStr = formatToTwoDecimals(state.value)
                
                if (currentStr.length <= 1 || currentStr == "0.0" || currentStr == "0.00") {
                    // Only one character left or at zero, replace with "0"
                    state.copy(value = 0.0)
                } else {
                    // Remove last character
                    val newStr = currentStr.dropLast(1)
                    val newValue = newStr.toDoubleOrNull() ?: 0.0
                    state.copy(value = roundToTwoDecimals(newValue))
                }
            }
        }
    }

    /**
     * Handles clear input (C)
     * @return New calculator state reset to default
     */
    fun handleClearInput(): CalculatorState {
        return CalculatorState()
    }

    /**
     * Checks if a value is within safe Double limits
     * @param value The value to check
     * @return true if the value is within limits, false otherwise
     */
    fun isValueWithinLimits(value: Double): Boolean {
        return value.isFinite() && value > Double.MIN_VALUE && value < Double.MAX_VALUE
    }

    /**
     * Creates an error state
     * @return CalculatorState representing an error condition
     */
    private fun createErrorState(): CalculatorState {
        return CalculatorState(
            value = Double.NaN,
            firstOperand = 0.0,
            operation = null,
            clearOnNextInput = true,
            errorState = true
        )
    }

    /**
     * Rounds a value to 2 decimal places
     * @param value The value to round
     * @return Value rounded to 2 decimal places
     */
    private fun roundToTwoDecimals(value: Double): Double {
        return kotlin.math.round(value * 100) / 100
    }

    /**
     * Formats a value to string with up to 2 decimal places
     * @param value The value to format
     * @return Formatted string representation
     */
    private fun formatToTwoDecimals(value: Double): String {
        val rounded = roundToTwoDecimals(value)
        // Format with 2 decimal places, then remove trailing zeros
        val formatted = "%.2f".format(rounded).trimEnd('0').trimEnd('.')
        return formatted
    }
}
