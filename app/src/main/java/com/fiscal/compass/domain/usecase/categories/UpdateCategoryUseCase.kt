package com.fiscal.compass.domain.usecase.categories

import com.fiscal.compass.domain.model.base.Category
import com.fiscal.compass.domain.repository.CategoryRepository
import javax.inject.Inject

class UpdateCategoryUseCase @Inject constructor (
    private val categoryRepository: CategoryRepository
){
    suspend fun invoke(category: Category): Result<String> {

        val categoryExists = categoryRepository.getCategoryByName(category.name)
        if (categoryExists != null && categoryExists.categoryId != category.categoryId) {
            return Result.failure(
                IllegalArgumentException("Category with name '${category.name}' already exists")
            )
        }

        val result =  categoryRepository.updateCategory(category)
        return if (result >= 0) {
            Result.success("Category updated successfully")
        } else {
            Result.failure(
                IllegalStateException("Unknown error occurred while updating the categoryId")
            )
        }
    }
}