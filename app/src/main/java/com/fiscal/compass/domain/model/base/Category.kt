package com.fiscal.compass.domain.model.base

import java.util.Date
import java.util.UUID
import kotlin.random.Random

data class Category(
    val categoryId: String,
    val name: String,
    val color: Int = 0xFF000000.toInt(),
    val isExpenseCategory: Boolean,
    val icon: String? = null,
    val description: String? = null,
    val expectedPersonType: String? = null,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)

// Dummy data providers
object CategoryProvider {
    
    // Individual category providers
    fun provideExpenseCategory(id: String = UUID.randomUUID().toString()): Category {
        val expenseNames = listOf("Food", "Transportation", "Entertainment", "Shopping", "Bills", "Healthcare")
        val colors = listOf(0xFFE57373, 0xFF81C784, 0xFF64B5F6, 0xFFFFB74D, 0xFFBA68C8, 0xFF4DB6AC)
        val icons = listOf("🍔", "🚗", "🎬", "🛒", "💡", "🏥")
        
        val index = Random.nextInt(expenseNames.size)
        return Category(
            categoryId = id,
            name = expenseNames[index],
            color = colors[index].toInt(),
            isExpenseCategory = true,
            icon = icons[index],
            description = "Expense category for ${expenseNames[index].lowercase()}"
        )
    }
    
    fun provideIncomeCategory(id: String = UUID.randomUUID().toString()): Category {
        val incomeNames = listOf("Salary", "Freelance", "Investment", "Business", "Bonus", "Gift")
        val colors = listOf(0xFF4CAF50, 0xFF2196F3, 0xFF9C27B0, 0xFFFF9800, 0xFF795548, 0xFFE91E63)
        val icons = listOf("💰", "💻", "📈", "🏢", "🎁", "💝")
        
        val index = Random.nextInt(incomeNames.size)
        return Category(
            categoryId = id,
            name = incomeNames[index],
            color = colors[index].toInt(),
            isExpenseCategory = false,
            icon = icons[index],
            description = "Income category for ${incomeNames[index].lowercase()}"
        )
    }
    
    fun provideRandomCategory(id: String = UUID.randomUUID().toString()): Category {
        return if (Random.nextBoolean()) {
            provideExpenseCategory(id)
        } else {
            provideIncomeCategory(id)
        }
    }
    
    // List providers
    fun provideExpenseCategoryList(count: Int = 5): List<Category> {
        return (1..count).map { provideExpenseCategory() }
    }
    
    fun provideIncomeCategoryList(count: Int = 3): List<Category> {
        return (1..count).map { provideIncomeCategory() }
    }
    
    fun provideMixedCategoryList(count: Int = 8): List<Category> {
        return (1..count).map { provideRandomCategory() }
    }
    
    fun provideCompleteCategoryList(): List<Category> {
        return provideExpenseCategoryList(6) + provideIncomeCategoryList(4)
    }
}