package com.app.uniqueplant.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import com.app.uniqueplant.data.model.Category

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomExposedDropDownMenu(
    modifier: Modifier = Modifier,
    label: String,
    options: List<Category>,
    selectedOption: Category,
    onOptionSelected: (Category) -> Unit
) {
//    val selectedOption = remember { mutableStateOf(selectedOption) }
    var expanded by remember { mutableStateOf(false) }
    val enabled = options.isNotEmpty() && selectedOption.name.isNotEmpty()
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedOption.name,
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

            options.forEachIndexed { index, category ->
                DropdownMenuItem(
                    text = { Text(text = category.name) },
                    onClick = {
                        onOptionSelected(category)
                        expanded = false
                    }
                )

                // Add divider after each item except the last one
                if (index < options.size - 1) {
                    androidx.compose.material3.HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        thickness = 0.5.dp
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showSystemUi = true, showBackground = true)
@Composable
fun CustomExposedDropDownMenuPreview() {
    val options = listOf(
        Category(
            categoryId = 1L,
            name = "Option 1",
            color = 0xFF6200EE.toInt(),
            isExpenseCategory = false,
        ),
        Category(
            categoryId = 2L,
            name = "Option 2",
            color = 0xFF03DAC5.toInt(),
            isExpenseCategory = false,
        ),
        Category(
            categoryId = 3L,
            name = "Option 3",
            color = 0xFFFF5722.toInt(),
            isExpenseCategory = false,
        )
    )
    CustomExposedDropDownMenu(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        label = "Select an option",
        options = options,
        selectedOption = options.first(),
        onOptionSelected = {
        }
    )
}

