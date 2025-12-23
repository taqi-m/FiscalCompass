package com.fiscal.compass.di

import com.fiscal.compass.domain.service.ExpenseService
import com.fiscal.compass.domain.service.ExpenseServiceImpl
import com.fiscal.compass.domain.service.IncomeService
import com.fiscal.compass.domain.service.IncomeServiceImpl
import com.fiscal.compass.domain.service.TransactionService
import com.fiscal.compass.domain.service.TransactionServiceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ServiceModule {

    @Binds
    @Singleton
    abstract fun bindExpenseService(
        expenseServiceImpl: ExpenseServiceImpl
    ): ExpenseService

    @Binds
    @Singleton
    abstract fun bindIncomeService(
        incomeServiceImpl: IncomeServiceImpl
    ): IncomeService

    @Binds
    @Singleton
    abstract fun bindTransactionService(
        transactionServiceImpl: TransactionServiceImpl
    ): TransactionService
}