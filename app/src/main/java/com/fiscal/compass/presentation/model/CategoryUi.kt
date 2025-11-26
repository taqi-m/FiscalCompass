package com.fiscal.compass.presentation.model

data class CategoryUi(
    val categoryId: Long = 0,
    val parentId: Long? = null,
    val isExpenseCategory: Boolean,
    val name: String = "",
    val description: String? = null,
    val icon: String? = null,
    val color: String
){
    companion object {
        val dummy = CategoryUi(
            categoryId = 1L,
            parentId = null,
            isExpenseCategory = true,
            name = "Food",
            description = "Expenses for food and groceries",
            icon = "ic_food",
            color = "#FF5733"
        )

        val dummyList = listOf(
            dummy,
            dummy.copy(categoryId = 2L, name = "Salary", isExpenseCategory = false, icon = "ic_salary", color = "#33FF57"),
            dummy.copy(categoryId = 3L, name = "Transport", isExpenseCategory = true, icon = "ic_transport", color = "#3357FF"),
            dummy.copy(categoryId = 4L, name = "Entertainment", isExpenseCategory = true, icon = "ic_entertainment", color = "#F333FF"),
            dummy.copy(categoryId = 5L, name = "Investment", isExpenseCategory = false, icon = "ic_investment", color = "#33FFF5")
        )

        val dummyGroup = mapOf(
            dummy to listOf(
                dummy.copy(categoryId = 6L, parentId = 1L, name = "Dining Out", isExpenseCategory = true, icon = "ic_dining", color = "#FFBD33"),
                dummy.copy(categoryId = 7L, parentId = 1L, name = "Groceries", isExpenseCategory = true, icon = "ic_groceries", color = "#FF33A8")
            ),
            dummyList[1] to listOf(
                dummy.copy(categoryId = 8L, parentId = 2L, name = "Monthly Salary", isExpenseCategory = false, icon = "ic_monthly_salary", color = "#33FFBD"),
                dummy.copy(categoryId = 9L, parentId = 2L, name = "Bonus", isExpenseCategory = false, icon = "ic_bonus", color = "#3380FF")
            )
        )
    }
}

typealias GroupedCategoryUi = Map<CategoryUi, List<CategoryUi>>