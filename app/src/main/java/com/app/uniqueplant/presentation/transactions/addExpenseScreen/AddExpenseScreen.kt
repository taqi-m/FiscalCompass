package com.app.uniqueplant.presentation.transactions.addExpenseScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.uniqueplant.R
import com.app.uniqueplant.data.model.Category
import com.app.uniqueplant.domain.model.InputField
import com.app.uniqueplant.ui.components.CustomExposedDropDownMenu
import com.app.uniqueplant.ui.components.DataEntryTextField
import com.app.uniqueplant.ui.components.DatePickerDialog
import com.app.uniqueplant.ui.components.ReadOnlyDataEntryTextField
import com.app.uniqueplant.ui.theme.UniquePlantTheme
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.GregorianCalendar
import java.util.Locale


@Composable
fun AddExpenseScreen(
    state: AddExpenseState,
    onEvent: (AddExpenseEvent) -> Unit,
) {
    Scaffold {
        AddExpenseContent(
            modifier = Modifier.padding(it),
            state = state,
            onEvent = onEvent
        )
    }
}

@Composable
fun AddExpenseContent(
    modifier: Modifier = Modifier,
    state: AddExpenseState,
    onEvent: (AddExpenseEvent) -> Unit,
) {
    var isCategoriesEmpty by remember { mutableStateOf(state.categories.isEmpty()) }
    LaunchedEffect(state.categories) {
        if (state.categories.isEmpty()) {
            isCategoriesEmpty = false
        }
    }


    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        )
        {

            Text(
                text = "Add Expense",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )


            DataEntryTextField(
                modifier = Modifier.fillMaxWidth(),
                label = "Amount",
                value = state.amount.value,
                onValueChange = { onEvent(AddExpenseEvent.OnAmountChange(it)) },
                isError = state.amount.error.isNotEmpty(),
                errorMessage = state.amount.error.ifEmpty { null },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Number
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            DataEntryTextField(
                modifier = Modifier.fillMaxWidth(),
                label = "Description",
                value = state.description.value,
                onValueChange = { onEvent(AddExpenseEvent.OnDescriptionChange(it)) },
                isError = state.description.error.isNotEmpty(),
                errorMessage = state.description.error.ifEmpty { null },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Text
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            ReadOnlyDataEntryTextField(
                modifier = Modifier.fillMaxWidth(),
                label = "Date",
                value = state.date.let { SimpleDateFormat("dd-MM-yyyy", Locale.US).format(it) },
//                isError = state.amount.error.isNotEmpty(),
//                errorMessage = state.amount.error.ifEmpty { null },
                trailingIcon = {
                    IconButton(
                        onClick = { onEvent(AddExpenseEvent.OnDialogDismiss) },
                        modifier = Modifier
                            .padding(end = 8.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_calendar_24),
                            contentDescription = "Select Date",
                            modifier = Modifier.padding(8.dp),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (!isCategoriesEmpty) {
                CustomExposedDropDownMenu(
                    modifier = Modifier.fillMaxWidth(),
                    label = "Select Category",
                    options = state.categories, // Replace with actual categories
                    onOptionSelected = { selected ->
                        onEvent(AddExpenseEvent.OnCategorySelected(selected.categoryId))
                    }
                )
            }

            OutlinedButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                shape = RoundedCornerShape(25),
                onClick = { onEvent(AddExpenseEvent.OnSaveClicked) }
            ) {
                Text(text = "Save")
            }
        }

        if (state.isDialogOpen) {
            DatePickerDialog(
                modifier = Modifier.padding(16.dp),
                onDismissRequest = {
                    onEvent(AddExpenseEvent.OnDialogDismiss)
                },
                onDateSelected = { selectedDate ->
                    onEvent(AddExpenseEvent.OnDialogDismiss)
                    onEvent(AddExpenseEvent.DateSelected(selectedDate))
                },
            )
        }
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun AddExpenseScreenPreview() {
    UniquePlantTheme {
        AddExpenseScreen(
            state = AddExpenseState(
                amount = InputField(
                    value = "5000",
                    error = ""
                ),
                description = InputField(
                    value = "Monthly Salary",
                    error = "Put a short description here"
                ),
                date = Date(
                    GregorianCalendar(
                        Calendar.YEAR,
                        Calendar.MONTH,
                        Calendar.DAY_OF_MONTH
                    ).timeInMillis
                ),
                categoryId = 1L,
                accountId = 1L,
                isLoading = false,
                isSuccess = false,
                categories = listOf(
                    Category(
                        categoryId = 1L,
                        name = "Salary",
                        color = 0xFF6200EE.toInt(),
                        isExpenseCategory = false
                    ),
                    Category(
                        categoryId = 2L,
                        name = "Investment",
                        color = 0xFF03DAC5.toInt(),
                        isExpenseCategory = false
                    )
                )
            ),
            onEvent = {}
        )
    }
}
