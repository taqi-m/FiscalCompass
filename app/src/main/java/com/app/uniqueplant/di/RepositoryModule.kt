package com.app.uniqueplant.di

import com.app.uniqueplant.data.repositories.AppPreferenceRepositoryImpl
import com.app.uniqueplant.data.repositories.AuthRepositoryImpl
import com.app.uniqueplant.data.repositories.CategoryRepositoryImpl
import com.app.uniqueplant.data.repositories.ExpenseRepositoryImpl
import com.app.uniqueplant.data.repositories.IncomeRepositoryImpl
import com.app.uniqueplant.data.repositories.UserRepositoryImpl
import com.app.uniqueplant.domain.repository.PreferenceManager
import com.app.uniqueplant.data.local.preferences.PreferenceManagerImpl
import com.app.uniqueplant.data.repositories.PersonRepositoryImpl
import com.app.uniqueplant.domain.repository.AppPreferenceRepository
import com.app.uniqueplant.domain.repository.AuthRepository
import com.app.uniqueplant.domain.repository.CategoryRepository
import com.app.uniqueplant.domain.repository.ExpenseRepository
import com.app.uniqueplant.domain.repository.IncomeRepository
import com.app.uniqueplant.domain.repository.PersonRepository
import com.app.uniqueplant.domain.repository.UserRepository
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