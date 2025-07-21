package com.app.uniqueplant.di

import android.content.Context
import androidx.room.Room
import com.app.uniqueplant.data.datasource.local.AppDatabase
import com.app.uniqueplant.data.datasource.local.dao.CategoryDao
import com.app.uniqueplant.data.datasource.local.dao.ExpenseDao
import com.app.uniqueplant.data.datasource.local.dao.IncomeDao
import com.app.uniqueplant.data.datasource.local.dao.UserDao
import com.app.uniqueplant.data.datasource.preferences.SharedPreferencesRepository
import com.app.uniqueplant.data.repository.CategoryRepositoryImpl
import com.app.uniqueplant.data.repository.ExpenseRepositoryImpl
import com.app.uniqueplant.data.repository.IncomeRepositoryImpl
import com.app.uniqueplant.data.repository.UserRepositoryImpl
import com.app.uniqueplant.domain.repository.CategoryRepository
import com.app.uniqueplant.domain.repository.ExpenseRepository
import com.app.uniqueplant.domain.repository.IncomeRepository
import com.app.uniqueplant.domain.repository.UserRepository
import com.app.uniqueplant.domain.usecase.AddExpenseUseCase
import com.app.uniqueplant.domain.usecase.AddIncomeUseCase
import com.app.uniqueplant.domain.usecase.GetCategoriesUseCase
import com.app.uniqueplant.domain.usecase.LoadDefaultsUseCase
import com.app.uniqueplant.domain.usecase.auth.SessionUseCase
import com.app.uniqueplant.domain.usecase.transaction.LoadTransactionsUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room
            .databaseBuilder(
                context,
                AppDatabase::class.java,
                "unique-plant-database"
            )
            .fallbackToDestructiveMigration(true)
            .build()
    }

    @Provides
    fun provideCategoryDao(database: AppDatabase): CategoryDao {
        return database.categoryDao()
    }

    @Provides
    fun provideExpenseDao(database: AppDatabase): ExpenseDao {
        return database.expenseDao()
    }

    @Provides
    fun provideIncomeDao(database: AppDatabase): IncomeDao {
        return database.incomeDao()
    }

    @Provides
    fun provideUserDao(database: AppDatabase): UserDao {
        return database.userDao()
    }

    @Provides
    fun provideCategoryRepository(
        categoryDao: CategoryDao,
        prefRepository: SharedPreferencesRepository
    ): CategoryRepository {
        return CategoryRepositoryImpl(categoryDao)
    }

    @Provides
    fun provideExpenseRepository(
        expenseDao: ExpenseDao,
    ): ExpenseRepository {
        return ExpenseRepositoryImpl(expenseDao)
    }

    @Provides
    fun provideIncomeRepository(
        incomeDao: IncomeDao,
    ): IncomeRepository {
        return IncomeRepositoryImpl(incomeDao)
    }

    @Provides
    fun provideUserRepository(
        userDao: UserDao,
        prefRepository: SharedPreferencesRepository
    ): UserRepository {
        return UserRepositoryImpl(userDao, prefRepository)
    }

    @Provides
    fun provideLoadDefaultsUseCase(
        categoryRepository: CategoryRepository,
        preferencesRepository: SharedPreferencesRepository
    ): LoadDefaultsUseCase {
        return LoadDefaultsUseCase(categoryRepository, preferencesRepository)
    }

    @Provides
    fun provideLoadTransactionsUseCase(
        incomeRepository: IncomeRepository,
        expenseRepository: ExpenseRepository
    ): LoadTransactionsUseCase {
        return LoadTransactionsUseCase(incomeRepository, expenseRepository)
    }

    @Provides
    fun provideGetCategoriesUseCase(
        categoryRepository: CategoryRepository
    ): GetCategoriesUseCase {
        return GetCategoriesUseCase(categoryRepository)
    }

    @Provides
    fun provideAddIncomeUseCase(
        sessionUseCase: SessionUseCase,
        incomeRepository: IncomeRepository
    ): AddIncomeUseCase {
        return AddIncomeUseCase(sessionUseCase, incomeRepository)
    }

    @Provides
    fun provideAddExpenseUseCase(
        sessionUseCase: SessionUseCase,
        expenseRepository: ExpenseRepository
    ): AddExpenseUseCase {
        return AddExpenseUseCase(sessionUseCase, expenseRepository)
    }

    @Provides
    fun getMonthlyReportUseCase(
        incomesRepository: IncomeRepository,
        expensesRepository: ExpenseRepository,
        categoryRepository: CategoryRepository
    ): com.app.uniqueplant.domain.usecase.analytics.GetMonthlyReportUseCase {
        return com.app.uniqueplant.domain.usecase.analytics.GetMonthlyReportUseCase(
            incomesRepository, expensesRepository, categoryRepository
        )
    }
}




















