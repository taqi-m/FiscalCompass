package com.fiscal.compass.domain.model.base

import java.util.Date
import kotlin.random.Random

data class Category(
    val categoryId: Long = 0,
    val parentCategoryId : Long? = null,
    val name: String,
    val color: Int = 0xFF000000.toInt(),
    val isExpenseCategory: Boolean,
    val icon: String? = null,
    val description: String? = null,
    val expectedPersonType: String? = null,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)

typealias CategoryTree = Map<Category, List<Category>>

// Dummy data providers
object CategoryProvider {
    
    // Individual category providers
    fun provideExpenseCategory(id: Long = Random.nextLong(1, 1000)): Category {
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
    
    fun provideIncomeCategory(id: Long = Random.nextLong(1, 1000)): Category {
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
    
    fun provideRandomCategory(id: Long = Random.nextLong(1, 1000)): Category {
        return if (Random.nextBoolean()) {
            provideExpenseCategory(id)
        } else {
            provideIncomeCategory(id)
        }
    }
    
    fun provideSubCategory(parentId: Long, id: Long = Random.nextLong(1000, 2000)): Category {
        val subNames = listOf("Groceries", "Dining Out", "Gas", "Public Transport", "Movies", "Games")
        return Category(
            categoryId = id,
            parentCategoryId = parentId,
            name = subNames.random(),
            color = Random.nextInt(),
            isExpenseCategory = true,
            icon = "📝",
            description = "Subcategory item"
        )
    }
    
    // List providers
    fun provideExpenseCategoryList(count: Int = 5): List<Category> {
        return (1..count).map { provideExpenseCategory(it.toLong()) }
    }
    
    fun provideIncomeCategoryList(count: Int = 3): List<Category> {
        return (1..count).map { provideIncomeCategory(it.toLong()) }
    }
    
    fun provideMixedCategoryList(count: Int = 8): List<Category> {
        return (1..count).map { provideRandomCategory(it.toLong()) }
    }
    
    fun provideCompleteCategoryList(): List<Category> {
        return provideExpenseCategoryList(6) + provideIncomeCategoryList(4)
    }
    
    fun provideCategoriesWithSubcategories(parentCount: Int = 3, subCount: Int = 2): List<Category> {
        val parents = provideExpenseCategoryList(parentCount)
        val subcategories = parents.flatMap { parent ->
            (1..subCount).map { provideSubCategory(parent.categoryId) }
        }
        return parents + subcategories
    }
    
    // Category tree providers
    fun provideCategoryTree(): CategoryTree {
        val parents = provideExpenseCategoryList(4)
        return parents.associateWith { parent ->
            (1..Random.nextInt(1, 4)).map { provideSubCategory(parent.categoryId) }
        }
    }
    
    fun provideSimpleCategoryTree(): CategoryTree {
        val foodCategory = provideExpenseCategory(1).copy(name = "Food")
        val transportCategory = provideExpenseCategory(2).copy(name = "Transport")
        
        return mapOf(
            foodCategory to listOf(
                provideSubCategory(1).copy(name = "Groceries"),
                provideSubCategory(1).copy(name = "Restaurants")
            ),
            transportCategory to listOf(
                provideSubCategory(2).copy(name = "Gas"),
                provideSubCategory(2).copy(name = "Public Transit")
            )
        )
    }
    
    fun provideLargeCategoryTree(): CategoryTree {
        val parents = (1..6).map { id ->
            if (id <= 4) provideExpenseCategory(id.toLong()) 
            else provideIncomeCategory(id.toLong())
        }
        return parents.associateWith { parent ->
            (1..Random.nextInt(2, 6)).map { provideSubCategory(parent.categoryId) }
        }
    }
}