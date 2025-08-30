package com.app.uniqueplant.ui.components.input

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.uniqueplant.presentation.model.CategoryUi

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> CustomExposedDropDownMenu(
    modifier: Modifier = Modifier,
    label: String,
    options: List<T>,
    selectedOption: T?,
    onOptionSelected: (T) -> Unit,
    optionToString: (T) -> String = { it.toString() }
) {
    if (selectedOption == null) {
        return // If no option is selected, do not render the dropdown
    }
    var expanded by remember { mutableStateOf(false) }
    val enabled = options.isNotEmpty() && optionToString(selectedOption).isNotEmpty()
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = optionToString(selectedOption),
            onValueChange = {},
            readOnly = true,
            label = { Text(text = label) },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Filled.ArrowDropDown,
                    contentDescription = "Dropdown Arrow",
                    Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled)
                )
            },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
            modifier = Modifier
                .menuAnchor(MenuAnchorType.SecondaryEditable, enabled)
                .fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {

            options.forEachIndexed { index, option ->
                DropdownMenuItem(
                    text = { Text(text = optionToString(option)) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )

                // Add divider after each item except the last one
                if (index < options.size - 1) {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        thickness = 0.5.dp
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenericExposedDropDownMenu(
    modifier: Modifier = Modifier,
    label: String,
    options: List<String>,
    selectedOption: String?,
    onOptionSelected: (String) -> Unit
) {
    if (selectedOption == null) {
        return // If no option is selected, do not render the dropdown
    }
    var expanded by remember { mutableStateOf(false) }
    val enabled = options.isNotEmpty() && selectedOption.isNotEmpty()
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedOption,
            onValueChange = {},
            readOnly = true,
            label = { Text(text = label) },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Filled.ArrowDropDown,
                    contentDescription = "Dropdown Arrow",
                    Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled)
                )
            },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
            modifier = Modifier
                .menuAnchor(MenuAnchorType.SecondaryEditable, enabled)
                .fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(text = option) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showSystemUi = true, showBackground = true)
@Composable
fun CustomExposedDropDownMenuPreview() {

    val options = listOf(
        CategoryUi(
            categoryId = 1,
            isExpenseCategory = true,
            name = "Category 1",
            color = "#FF0000"
        ),
        CategoryUi(
            categoryId = 2,
            isExpenseCategory = false,
            name = "Category 2",
            color = "#00FF00"
        ),

    )
    CustomExposedDropDownMenu(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        label = "Select an option",
        options = options,
        selectedOption = options.first(),
        onOptionSelected = {
        },
        optionToString = { it.name }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showSystemUi = true, showBackground = true)
@Composable
fun GenericExposedDropDownMenuPreview() {
    val options = listOf("Option 1", "Option 2", "Option 3")
    var selectedOption by remember { mutableStateOf(options.firstOrNull()) }

    GenericExposedDropDownMenu(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        label = "Select an option",
        options = options,
        selectedOption = selectedOption,
        onOptionSelected = { selectedOption = it }
    )
}

