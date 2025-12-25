package com.fiscal.compass.domain.service

import com.fiscal.compass.domain.model.base.Category
import com.fiscal.compass.domain.model.base.CategoryTree
import com.fiscal.compass.domain.repository.CategoryRepository
import com.fiscal.compass.presentation.model.TransactionType
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CategoryServiceImpl @Inject constructor(
    private val categoryRepository: CategoryRepository
) : CategoryService {

    override suspend fun addCategory(
        name: String,
        description: String,
        transactionType: TransactionType,
        expectedPersonType: String
    ): Result<String> {
        return try {
            // Check if the category already exists
            val existingCategory = categoryRepository.getCategoryByName(name)
            if (existingCategory != null) {
                return Result.failure(
                    IllegalArgumentException("Category with name $name already exists.")
                )
            }

            val isExpenseCategory = transactionType == TransactionType.EXPENSE

            // Create a new category
            val newCategory = Category(
                name = name,
                description = description,
                expectedPersonType = expectedPersonType,
                isExpenseCategory = isExpenseCategory,
                color = 0xFFFFFF,
            )

            // Add the new category to the repository
            val result = categoryRepository.insertCategory(newCategory)
            if (result >= 0) {
                Result.success("Category added successfully")
            } else {
                Result.failure(
                    IllegalStateException("Unknown error occurred while adding category")
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateCategory(category: Category): Result<String> {
        return try {
            val categoryExists = categoryRepository.getCategoryByName(category.name)
            if (categoryExists != null && categoryExists.categoryId != category.categoryId) {
                return Result.failure(
                    IllegalArgumentException("Category with name '${category.name}' already exists")
                )
            }

            val result = categoryRepository.updateCategory(category)
            if (result >= 0) {
                Result.success("Category updated successfully")
            } else {
                Result.failure(
                    IllegalStateException("Unknown error occurred while updating the category")
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteCategory(category: Category): Result<String> {
        return try {
            // Proceed to delete the category
            val result = categoryRepository.deleteCategory(category)
            if (result > 0) {
                Result.success("Category deleted successfully")
            } else {
                Result.failure(
                    IllegalStateException("Unknown error occurred while deleting the category")
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAllCategories(): List<Category> {
        return categoryRepository.getAllCategories()
    }

    override suspend fun getAllCategoryTreeFlow(): Flow<CategoryTree> {
        return categoryRepository.getAllCategoriesTreeFlow()
    }

    override suspend fun getExpenseCategoryTreeFlow(): Flow<CategoryTree> {
        return categoryRepository.getExpenseCategoriesTreeFLow()
    }

    override suspend fun getIncomeCategoryTreeFlow(): Flow<CategoryTree> {
        return categoryRepository.getIncomeCategoriesTreeFLow()
    }

    override suspend fun getIncomeCategoriesTree(): CategoryTree {
        return categoryRepository.getIncomeCategoriesTree()
    }

    override suspend fun getExpenseCategoriesTree(): CategoryTree {
        return categoryRepository.getExpenseCategoriesTree()
    }

    override suspend fun getExpenseCategoriesWithFlow(): Flow<List<Category>> {
        return categoryRepository.getExpenseCategoriesWithFlow()
    }

    override suspend fun getIncomeCategoriesWithFlow(): Flow<List<Category>> {
        return categoryRepository.getIncomeCategoriesWithFlow()
    }

    override suspend fun getExpenseCategories(): List<Category> {
        return categoryRepository.getExpenseCategories()
    }

    override suspend fun getIncomeCategories(): List<Category> {
        return categoryRepository.getIncomeCategories()
    }

    override suspend fun getCategoryById(id: Long): Category? {
        return categoryRepository.getCategoryById(id)
    }

    override suspend fun getCategoryByName(name: String): Category? {
        return categoryRepository.getCategoryByName(name)
    }
}
