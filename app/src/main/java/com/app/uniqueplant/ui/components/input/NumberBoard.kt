package com.app.uniqueplant.ui.components.input

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.uniqueplant.domain.usecase.CurrencyFormaterUseCase
import com.app.uniqueplant.ui.theme.UniquePlantTheme


@Composable
fun Calculator(
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit = {},
    onSaveClick: () -> Unit = {},
) {
    // Calculator state
    var displayText by remember { mutableStateOf("0") }
    var firstOperand by remember { mutableDoubleStateOf(0.0) }
    var operation by remember { mutableStateOf<String?>(null) }
    var clearOnNextInput by remember { mutableStateOf(false) }
    var errorState by remember { mutableStateOf(false) }

    val clearDisplay = {
        displayText = "0"
        firstOperand = 0.0
        operation = null
        clearOnNextInput = false
        errorState = false
    }


    // Functions for delete and clear operations
    val deleteLastChar = {
        if (errorState) {
            clearDisplay()
        } else {
            displayText = if (displayText.length <= 1) "0" else displayText.dropLast(1)
        }
    }

    // Check if value is within Double limits
    val isValueWithinLimits = { value: Double ->
        value.isFinite() && value > Double.MIN_VALUE && value < Double.MAX_VALUE
    }

    // Handle error and return to default state
    val handleError = {
        displayText = "Error"
        errorState = true
        clearOnNextInput = true
    }

    LaunchedEffect(displayText) {
        // Notify parent of display text change
        if (!errorState) {
            onValueChange(displayText)
        }
    }

    // Get screen height to adapt calculator size
    val configuration = LocalWindowInfo.current
    val screenHeight = configuration.containerSize.height

    // Calculate a reasonable height for the calculator based on screen size
    // Uses approximately 60% of screen height for the calculator
    val calculatorHeight = (screenHeight * 0.6).toInt().dp

    Box(
        modifier = modifier,
        contentAlignment = Alignment.BottomCenter
    ){
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Display with trailing icon
            OutlinedTextField(
                value = CurrencyFormaterUseCase.formatCurrency(displayText.toDoubleOrNull() ?: 0.0),
                onValueChange = { onValueChange(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp),
                readOnly = true,
                textStyle = LocalTextStyle.current.copy(
                    fontSize = 28.sp,
                    textAlign = TextAlign.End,
                    color = if (errorState) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                ),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors().copy(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.primary,
                    // Disable selection appearance
                    textSelectionColors = TextSelectionColors(
                        backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
                        handleColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    disabledTextColor = if (errorState)
                        MaterialTheme.colorScheme.error
                    else
                        OutlinedTextFieldDefaults.colors().focusedTextColor,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    disabledIndicatorColor = MaterialTheme.colorScheme.primary
                ),
                interactionSource = remember { MutableInteractionSource() },
                enabled = false,
                trailingIcon = {
                    IconButton(
                        modifier = Modifier
                            .padding(horizontal = 8.dp),
                        onClick = { deleteLastChar() },
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Delete/Clear",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                leadingIcon = {
                    if (operation != null) {
                        Text(
                            text = operation ?: "",
                            fontSize = 24.sp,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            )

            // Number pad
            NumberBoard(
                modifier = Modifier
                    .fillMaxWidth(),
                onNumberClick = { number ->
                    if (errorState) {
                        clearDisplay()
                        displayText = number.toString()
                    } else if (clearOnNextInput) {
                        displayText = number.toString()
                        clearOnNextInput = false
                    } else {
                        displayText =
                            if (displayText == "0") number.toString() else displayText + number
                    }
                },
                onOperatorClick = { op ->
                    if (errorState) {
                        clearDisplay()
                    } else {
                        try {
                            val value = displayText.toDouble()
                            if (isValueWithinLimits(value)) {
                                firstOperand = value
                                operation = op
                                clearOnNextInput = true
                            } else {
                                handleError()
                            }
                        } catch (e: NumberFormatException) {
                            handleError()
                        }
                    }
                },
                onEqualsClick = {
                    if (errorState) {
                        clearDisplay()
                    } else {
                        try {
                            val secondOperand = displayText.toDouble()

                            if (!isValueWithinLimits(secondOperand)) {
                                handleError()
                                return@NumberBoard
                            }

                            val result = when (operation) {
                                "+" -> firstOperand + secondOperand
                                "-" -> firstOperand - secondOperand
                                "×" -> firstOperand * secondOperand
                                "÷" -> if (secondOperand != 0.0) firstOperand / secondOperand else Double.NaN
                                else -> secondOperand
                            }

                            if (result.isNaN() || result.isInfinite() || !isValueWithinLimits(result)) {
                                handleError()
                                return@NumberBoard
                            }

                            // Format the result
                            displayText = if (result == result.toInt().toDouble()) {
                                result.toInt().toString()
                            } else {
                                result.toString()
                            }

                            operation = null
                            clearOnNextInput = true
                        } catch (e: NumberFormatException) {
                            handleError()
                        } catch (e: Exception) {
                            handleError()
                        }
                    }
                },
                onClearClick = clearDisplay,
                onDecimalClick = {
                    if (errorState) {
                        clearDisplay()
                        displayText = "0."
                    } else if (clearOnNextInput) {
                        displayText = "0."
                        clearOnNextInput = false
                    } else if (!displayText.contains(".")) {
                        displayText = "$displayText."
                    }
                },
                onSaveClick = {
                    if (!errorState) {
                        onSaveClick()
                    }
                },
            )
        }
    }
}


@Preview(
    showBackground = true, backgroundColor = 0xFFFFFFFF, name = "Calculator Preview", showSystemUi = true
)
@Composable
fun CalculatorPreview() {
    UniquePlantTheme {
        Calculator(
            modifier = Modifier.fillMaxSize().padding(16.dp)
        )
    }
}


@Composable
fun NumberBoard(
    onNumberClick: (Int) -> Unit,
    onOperatorClick: (String) -> Unit,
    onEqualsClick: () -> Unit,
    onClearClick: () -> Unit,
    onDecimalClick: () -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(4.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Row 1: 7, 8, 9, ÷
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CalcButton(text = "7", onClick = { onNumberClick(7) }, modifier = Modifier.weight(1f))
            CalcButton(text = "8", onClick = { onNumberClick(8) }, modifier = Modifier.weight(1f))
            CalcButton(text = "9", onClick = { onNumberClick(9) }, modifier = Modifier.weight(1f))
            OperatorButton(
                text = "÷", onClick = { onOperatorClick("÷") }, modifier = Modifier.weight(1f)
            )
        }

        // Row 2: 4, 5, 6, ×
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CalcButton(text = "4", onClick = { onNumberClick(4) }, modifier = Modifier.weight(1f))
            CalcButton(text = "5", onClick = { onNumberClick(5) }, modifier = Modifier.weight(1f))
            CalcButton(text = "6", onClick = { onNumberClick(6) }, modifier = Modifier.weight(1f))
            OperatorButton(
                text = "×", onClick = { onOperatorClick("×") }, modifier = Modifier.weight(1f)
            )
        }

        // Row 3: 1, 2, 3, -
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CalcButton(text = "1", onClick = { onNumberClick(1) }, modifier = Modifier.weight(1f))
            CalcButton(text = "2", onClick = { onNumberClick(2) }, modifier = Modifier.weight(1f))
            CalcButton(text = "3", onClick = { onNumberClick(3) }, modifier = Modifier.weight(1f))
            OperatorButton(
                text = "-", onClick = { onOperatorClick("-") }, modifier = Modifier.weight(1f)
            )
        }

        // Row 4: 0, ., =, +
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CalcButton(text = ".", onClick = { onDecimalClick() }, modifier = Modifier.weight(1f))
            CalcButton(text = "0", onClick = { onNumberClick(0) }, modifier = Modifier.weight(1f))

            EqualsButton(onClick = onEqualsClick, modifier = Modifier.weight(1f))
            OperatorButton(
                text = "+", onClick = { onOperatorClick("+") }, modifier = Modifier.weight(1f)
            )
        }

        // Row 5: Clear, Save
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ClearButton(onClick = onClearClick, modifier = Modifier.weight(1f))
            SaveButton(onClick = onSaveClick, modifier = Modifier.weight(1f))
        }
    }
}

@Preview(
    showBackground = true, backgroundColor = 0xFFFFFFFF, name = "NumberBoard Preview"
)
@Composable
fun NumberBoardPreview() {
    UniquePlantTheme {
        NumberBoard(
            onNumberClick = {},
            onOperatorClick = {},
            onEqualsClick = {},
            onClearClick = {},
            onDecimalClick = {},
            onSaveClick = {})
    }
}


@Composable
private fun CalcButton(
    text: String, onClick: () -> Unit, modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        shape = CircleShape,
        modifier = modifier
            .aspectRatio(1f)
            .clip(CircleShape),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        )
    ) {
        Text(
            text = text,
            fontSize = 20.sp,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun OperatorButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        shape = CircleShape,
        modifier = modifier.aspectRatio(1f),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Text(
            text = text,
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
        )
    }
}

@Composable
private fun EqualsButton(
    onClick: () -> Unit, modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        shape = CircleShape,
        modifier = modifier.aspectRatio(1f),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Text(text = "=", fontSize = 20.sp, color = MaterialTheme.colorScheme.onPrimary)
    }
}

@Composable
private fun ClearButton(
    onClick: () -> Unit, modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick, modifier = modifier.height(56.dp), colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Text(
            text = "Clear",
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onErrorContainer,
        )
    }
}

@Composable
private fun SaveButton(
    onClick: () -> Unit, modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick, modifier = modifier.height(56.dp), colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Text(
            text = "Save",
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
        )
    }
}