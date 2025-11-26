package com.fiscal.compass.di

import com.fiscal.compass.data.repositories.AppPreferenceRepositoryImpl
import com.fiscal.compass.data.repositories.AuthRepositoryImpl
import com.fiscal.compass.data.repositories.CategoryRepositoryImpl
import com.fiscal.compass.data.repositories.ExpenseRepositoryImpl
import com.fiscal.compass.data.repositories.IncomeRepositoryImpl
import com.fiscal.compass.data.repositories.UserRepositoryImpl
import com.fiscal.compass.domain.repository.PreferenceManager
import com.fiscal.compass.data.local.preferences.PreferenceManagerImpl
import com.fiscal.compass.data.repositories.PersonRepositoryImpl
import com.fiscal.compass.domain.repository.AppPreferenceRepository
import com.fiscal.compass.domain.repository.AuthRepository
import com.fiscal.compass.domain.repository.CategoryRepository
import com.fiscal.compass.domain.repository.ExpenseRepository
import com.fiscal.compass.domain.repository.IncomeRepository
import com.fiscal.compass.domain.repository.PersonRepository
import com.fiscal.compass.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        userRepositoryImpl: UserRepositoryImpl
    ): UserRepository

    @Binds
    @Singleton
    abstract fun bindCategoryRepository(
        categoryRepositoryImpl: CategoryRepositoryImpl
    ): CategoryRepository

    @Binds
    @Singleton
    abstract fun bindIncomeRepository(
        incomeRepositoryImpl: IncomeRepositoryImpl
    ): IncomeRepository

    @Binds
    @Singleton
    abstract fun bindExpenseRepository(
        expenseRepositoryImpl: ExpenseRepositoryImpl
    ): ExpenseRepository

    @Binds
    @Singleton
    abstract fun bindPersonRepository(
        personRepositoryImpl: PersonRepositoryImpl
    ): PersonRepository

    @Binds
    @Singleton
    abstract fun bindPreferenceManager(
        preferenceManagerImpl: PreferenceManagerImpl
    ): PreferenceManager

    @Binds
    @Singleton
    abstract fun bindAppPreferencesRepository(
        appPreferencesRepositoryImpl: AppPreferenceRepositoryImpl
    ): AppPreferenceRepository
}