package com.app.uniqueplant.ui.components.input

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.uniqueplant.R
import com.app.uniqueplant.presentation.utilities.CurrencyFormater
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
    fun handleError(exception: Exception? = null) {
        displayText = "Error"
        firstOperand = 0.0
        operation = null
        clearOnNextInput = true
        errorState = true
        exception?.printStackTrace()
    }

    LaunchedEffect(displayText) {
        // Notify parent of display text change
        if (!errorState) {
            onValueChange(displayText)
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.SpaceBetween
    )
    {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(0.6f),
                    shape = MaterialTheme.shapes.medium
                )
                .padding(16.dp),
            text = CurrencyFormater.formatCalculatorCurrency(
                displayText
            ),
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Left,
            color = if (errorState) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
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
                    // Check if we already have 2 decimal places
                    val decimalIndex = displayText.indexOf('.')
                    if (decimalIndex != -1 && displayText.length - decimalIndex > 2) {
                        // Already have 2 decimal places, don't add more digits
                        return@NumberBoard
                    }

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
                        handleError(e)
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
                            "*" -> firstOperand * secondOperand
                            "÷" -> if (secondOperand != 0.0) firstOperand / secondOperand else Double.NaN
                            else -> secondOperand
                        }

                        if (result.isNaN() || result.isInfinite() || !isValueWithinLimits(result)) {
                            handleError()
                            return@NumberBoard
                        }

                        // Format the result with max 3 decimal places
                        displayText = if (result == result.toInt().toDouble()) {
                            // If it's a whole number, show as integer
                            result.toInt().toString()
                        } else {
                            // Round to 3 decimal places
                            "%.2f".format(result).trimEnd('0').trimEnd('.')
                        }

                        operation = null
                        clearOnNextInput = true
                    } catch (e: NumberFormatException) {
                        handleError(e)
                    } catch (e: Exception) {
                        handleError(e)
                    }
                }
            },
            onClearClick = clearDisplay,
            onDeleteLastChar = deleteLastChar,
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


@Preview(
    showBackground = true,
    backgroundColor = 0xFFFFFFFF,
    name = "Calculator Preview",
    showSystemUi = true
)
@Composable
fun CalculatorPreview() {
    UniquePlantTheme{
        Calculator(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        )
    }
}


@Composable
fun NumberBoard(
    onNumberClick: (Int) -> Unit,
    onOperatorClick: (String) -> Unit,
    onEqualsClick: () -> Unit,
    onClearClick: () -> Unit,
    onDeleteLastChar: () -> Unit,
    onDecimalClick: () -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .padding(4.dp)
            .height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.6f)
                .fillMaxHeight(),
        )
        {
            // Row 3: 1, 2, 3, -
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                CalcButton(
                    text = "1",
                    onClick = { onNumberClick(1) },
                    modifier = Modifier.weight(1f)
                )
                CalcButton(
                    text = "2",
                    onClick = { onNumberClick(2) },
                    modifier = Modifier.weight(1f)
                )
                CalcButton(
                    text = "3",
                    onClick = { onNumberClick(3) },
                    modifier = Modifier.weight(1f)
                )
            }
            // Row 2: 4, 5, 6, ×
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                CalcButton(
                    text = "4",
                    onClick = { onNumberClick(4) },
                    modifier = Modifier.weight(1f)
                )
                CalcButton(
                    text = "5",
                    onClick = { onNumberClick(5) },
                    modifier = Modifier.weight(1f)
                )
                CalcButton(
                    text = "6",
                    onClick = { onNumberClick(6) },
                    modifier = Modifier.weight(1f)
                )
            }
            // Row 1: 7, 8, 9, ÷
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                CalcButton(
                    text = "7",
                    onClick = { onNumberClick(7) },
                    modifier = Modifier.weight(1f)
                )
                CalcButton(
                    text = "8",
                    onClick = { onNumberClick(8) },
                    modifier = Modifier.weight(1f)
                )
                CalcButton(
                    text = "9",
                    onClick = { onNumberClick(9) },
                    modifier = Modifier.weight(1f)
                )
            }


            // Row 4: 0, ., =, +
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                CalcButton(
                    text = "00",
                    onClick = {
                        onNumberClick(0)
                        onNumberClick(0)
                    },
                    modifier = Modifier.weight(1f)
                )
                CalcButton(
                    text = "0",
                    onClick = { onNumberClick(0) },
                    modifier = Modifier.weight(1f)
                )

                EqualsButton(onClick = onEqualsClick, modifier = Modifier.weight(1f))
            }
        }


        Column(
            modifier = Modifier
                .fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally
        )
        {
            OperatorButton(text = "*", onClick = { onOperatorClick("*") })
            OperatorButton(text = "÷", onClick = { onOperatorClick("÷") })
            OperatorButton(text = "+", onClick = { onOperatorClick("+") })
            OperatorButton(text = "-", onClick = { onOperatorClick("-") })
        }

        Column(
            modifier = Modifier
                .fillMaxHeight(),
        )
        {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .width(IntrinsicSize.Min),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                ClearButton(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f),
                    onClick = onClearClick,
                )

                BackspaceButton(
                    onClick = onDeleteLastChar,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            SaveButton(
                onClick = onSaveClick,
            )
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
            modifier = Modifier.wrapContentSize(),
            onNumberClick = {},
            onOperatorClick = {},
            onEqualsClick = {},
            onClearClick = {},
            onDeleteLastChar = {},
            onDecimalClick = {},
            onSaveClick = {})
    }
}


@Composable
private fun CalcButton(
    text: String, onClick: () -> Unit, modifier: Modifier = Modifier
) {
    val textStyle = MaterialTheme.typography.titleMedium

    Button(
        onClick = onClick,
        shape = MaterialTheme.shapes.medium,
        modifier = modifier
            .heightIn(min = 36.dp, max = 56.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        )
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = text,
            style = textStyle,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
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
        shape = MaterialTheme.shapes.medium,
        modifier = modifier,
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
        shape = MaterialTheme.shapes.medium,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.inversePrimary,
            contentColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Text(text = "=", fontSize = 20.sp)
    }
}

@Composable
private fun ClearButton(
    onClick: () -> Unit, modifier: Modifier = Modifier
) {
    Button(
        shape = MaterialTheme.shapes.medium,
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.errorContainer,
            contentColor = MaterialTheme.colorScheme.onErrorContainer
        )
    ) {
        Text(
            text = "AC",
            fontSize = 18.sp
        )
    }
}


@Composable
private fun BackspaceButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {


    /*IconButton(
        modifier = Modifier
            .padding(horizontal = 8.dp),
        onClick = { deleteLastChar() },
    ) {
        Icon(
            imageVector = Icons.Default.Clear,
            contentDescription = "Delete/Clear",
            tint = MaterialTheme.colorScheme.primary
        )
    }*/
    Button(
        shape = MaterialTheme.shapes.medium,
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
            contentColor = MaterialTheme.colorScheme.onTertiaryContainer
        )
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_keyboard_backspace_24),
            contentDescription = "Backspace",
            tint = MaterialTheme.colorScheme.onTertiaryContainer
        )
    }
}

@Composable
private fun SaveButton(
    onClick: () -> Unit, modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        shape = MaterialTheme.shapes.medium,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
    ) {
        Text(
            text = "OK",
            fontSize = 18.sp
        )
    }
}