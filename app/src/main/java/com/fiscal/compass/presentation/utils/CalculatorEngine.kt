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
                CalculatorState(displayText = number.toString())
            }
            state.clearOnNextInput -> {
                // Start new number after operation or equals
                // Preserve operation and firstOperand if they exist (after operator selection)
                state.copy(
                    displayText = number.toString(),
                    clearOnNextInput = false
                )
            }
            else -> {
                // Check if we already have 2 decimal places
                val decimalIndex = state.displayText.indexOf('.')
                if (decimalIndex != -1 && state.displayText.length - decimalIndex > 2) {
                    // Already have 2 decimal places, don't add more digits
                    return state
                }

                // Append number to current display
                val newDisplayText = if (state.displayText == "0") {
                    number.toString()
                } else {
                    state.displayText + number
                }
                state.copy(displayText = newDisplayText)
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
                    val value = state.displayText.toDouble()
                    if (isValueWithinLimits(value)) {
                        state.copy(
                            firstOperand = value,
                            operation = operator,
                            clearOnNextInput = true
                        )
                    } else {
                        createErrorState()
                    }
                } catch (e: NumberFormatException) {
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
                    val secondOperand = state.displayText.toDouble()

                    if (!isValueWithinLimits(secondOperand)) {
                        return createErrorState()
                    }

                    val result = when (state.operation) {
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

                    if (result.isNaN() || result.isInfinite() || !isValueWithinLimits(result)) {
                        return createErrorState()
                    }

                    // Format the result
                    val formattedResult = formatResult(result)

                    state.copy(
                        displayText = formattedResult,
                        operation = null,
                        clearOnNextInput = true
                    )

                } catch (e: NumberFormatException) {
                    createErrorState()
                } catch (e: Exception) {
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
                CalculatorState(displayText = "0.")
            }
            state.clearOnNextInput -> {
                // Start with "0." after operation or equals
                state.copy(
                    displayText = "0.",
                    clearOnNextInput = false
                )
            }
            state.displayText.contains(".") -> {
                // Already has decimal point, no change
                state
            }
            else -> {
                // Add decimal point to current number
                state.copy(displayText = "${state.displayText}.")
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
            state.displayText.length <= 1 -> {
                // Only one character left, replace with "0"
                state.copy(displayText = "0")
            }
            else -> {
                // Remove last character
                state.copy(displayText = state.displayText.dropLast(1))
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
            displayText = "Error",
            firstOperand = 0.0,
            operation = null,
            clearOnNextInput = true,
            errorState = true
        )
    }

    /**
     * Formats a calculation result for display
     * @param result The result to format
     * @return Formatted string representation of the result
     */
    private fun formatResult(result: Double): String {
        return if (result == result.toInt().toDouble()) {
            // If it's a whole number, show as integer
            result.toInt().toString()
        } else {
            // Round to 2 decimal places and remove trailing zeros
            "%.2f".format(result).trimEnd('0').trimEnd('.')
        }
    }
}
