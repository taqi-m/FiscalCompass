package com.fiscal.compass.ui.components.input

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fiscal.compass.R
import com.fiscal.compass.presentation.utilities.CurrencyFormater
import com.fiscal.compass.presentation.utils.AmountInputType
import com.fiscal.compass.presentation.utils.CalculatorEngine
import com.fiscal.compass.presentation.utils.CalculatorState
import com.fiscal.compass.ui.theme.FiscalCompassTheme


@Composable
fun Calculator(
    modifier: Modifier = Modifier,
    initialValue: String = "0",
    label: String = "",
    inputType: AmountInputType = AmountInputType.TOTAL_AMOUNT,
    onValueChange: (String, AmountInputType) -> Unit = { _, _ -> },
    onSaveClick: () -> Unit = {},
) {
    // Calculator state using the new CalculatorState class
    // Use inputType as key to reset state only when switching between amount types
    var calculatorState by remember(inputType) {
        mutableStateOf(CalculatorState.withInitialValue(initialValue))
    }

    // Track the last inputType to detect field switches
    var lastInputType by remember { mutableStateOf(inputType) }

    // Update display when switching between input types, but preserve calculator operations
    LaunchedEffect(inputType) {
        // Only reset when explicitly switching between input types
        if (lastInputType != inputType) {
            calculatorState = CalculatorState.withInitialValue(initialValue)
        }
        lastInputType = inputType
    }

    // Initialize the calculator state with initial value only on first composition
    LaunchedEffect(Unit) {
        if (calculatorState.displayText == "0" && initialValue != "0") {
            calculatorState = CalculatorState.withInitialValue(initialValue)
        }
    }

    // Notify parent of display text change
    LaunchedEffect(calculatorState.displayText) {
        if (!calculatorState.errorState) {
            onValueChange(calculatorState.displayText, inputType)
        }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Bottom
    ) {
        // Label display
        if (label.isNotEmpty()) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }

        // Calculation display - shows when operation is in progress
        if (calculatorState.operation != null && !calculatorState.errorState) {
            CalculationDisplay(
                firstOperand = calculatorState.firstOperand,
                operation = calculatorState.operation,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }

        // Number pad
        NumberBoard(
            modifier = Modifier.fillMaxWidth(),
            onNumberClick = { number ->
                calculatorState = CalculatorEngine.handleNumberInput(calculatorState, number)
            },
            onOperatorClick = { operator ->
                calculatorState = CalculatorEngine.handleOperatorInput(calculatorState, operator)
            },
            onEqualsClick = {
                calculatorState = CalculatorEngine.handleEqualsInput(calculatorState)
            },
            onClearClick = {
                calculatorState = CalculatorEngine.handleClearInput()
            },
            onDeleteLastChar = {
                calculatorState = CalculatorEngine.handleDeleteInput(calculatorState)
            },
            onDecimalClick = {
                calculatorState = CalculatorEngine.handleDecimalInput(calculatorState)
            },
            onSaveClick = {
                if (!calculatorState.errorState) {
                    onSaveClick()
                }
            }
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
    FiscalCompassTheme{
        Calculator(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            label = "Total Amount",
            inputType = AmountInputType.TOTAL_AMOUNT
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
    FiscalCompassTheme {
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

/**
 * Displays the current calculation in progress
 * Shows the first operand and the selected operator
 */
@Composable
private fun CalculationDisplay(
    firstOperand: Double,
    operation: String?,
    modifier: Modifier = Modifier
) {
    androidx.compose.material3.Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.small,
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Calculating",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = CurrencyFormater.formatCurrency(firstOperand),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                    )
                    if (operation != null) {
                        androidx.compose.material3.Surface(
                            shape = androidx.compose.foundation.shape.CircleShape,
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        ) {
                            Text(
                                text = operation,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            Icon(
                painter = painterResource(R.drawable.ic_keyboard_backspace_24),
                contentDescription = "Calculation in progress",
                tint = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.5f),
                modifier = Modifier
                    .graphicsLayer {
                        rotationZ = 180f
                    }
            )
        }
    }
}


@Preview(
    showBackground = true, backgroundColor = 0xFFFFFFFF, name = "CalculationDisplay Preview"
)
@Composable
fun CalculationDisplayPreview() {
    FiscalCompassTheme {
        CalculationDisplay(
            firstOperand = 1234.56,
            operation = "+",
            modifier = Modifier.wrapContentSize()
        )
    }
}
