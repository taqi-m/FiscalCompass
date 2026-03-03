package com.fiscal.compass.domain.service

import android.util.Log
import com.fiscal.compass.data.mappers.toTransaction
import com.fiscal.compass.domain.model.rbac.Permission
import com.fiscal.compass.domain.model.Transaction
import com.fiscal.compass.domain.model.base.Expense
import com.fiscal.compass.domain.model.base.Income
import com.fiscal.compass.domain.repository.CategoryRepository
import com.fiscal.compass.domain.repository.ExpenseRepository
import com.fiscal.compass.domain.repository.IncomeRepository
import com.fiscal.compass.domain.repository.PersonRepository
import com.fiscal.compass.domain.repository.UserRepository
import com.fiscal.compass.domain.usecase.auth.SessionUseCase
import com.fiscal.compass.domain.usecase.rbac.CheckPermissionUseCase
import com.fiscal.compass.domain.util.SearchCriteria
import com.fiscal.compass.domain.util.TransactionType
import com.fiscal.compass.domain.validation.PaymentValidation
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class TransactionServiceImpl @Inject constructor(
    private val sessionUseCase: SessionUseCase,
    private val expenseRepository: ExpenseRepository,
    private val incomeRepository: IncomeRepository,
    private val categoryRepository: CategoryRepository,
    private val personRepository: PersonRepository,
    private val userRepository: UserRepository,
    private val checkPermissionUseCase: CheckPermissionUseCase
) : TransactionService {

    override suspend fun getTransactionById(transactionId: String, isExpense: Boolean): Transaction {
        if (isExpense) {
            val expenseFull = expenseRepository.getSingleFulExpenseById(transactionId)

            if (expenseFull == null) {
                throw IllegalArgumentException("Transaction not found")
            }

            val transaction = expenseFull.expense.toTransaction()
            return transaction.copy(
                categoryId = expenseFull.expense.categoryId,
                category = expenseFull.category,
                personId = expenseFull.expense.personId,
                person = expenseFull.person
            )
        } else {
            val incomeFull = incomeRepository.getSingleFullIncomeById(transactionId)

            if (incomeFull == null) {
                throw IllegalArgumentException("Transaction not found")
            }

            val transaction = incomeFull.income.toTransaction()
            return transaction.copy(
                categoryId = incomeFull.income.categoryId,
                category = incomeFull.category,
                personId = incomeFull.income.personId,
                person = incomeFull.person
            )
        }
    }

    override suspend fun addTransaction(transaction: Transaction): Result<Long> {
        return try {
            val amount = transaction.amount
            val amountPaid = transaction.amountPaid
            if (amount <= 0) {
                return Result.failure(IllegalArgumentException("Amount must be greater than zero"))
            }

            PaymentValidation.validatePaymentAmount(amount, amountPaid).getOrElse {
                return Result.failure(it)
            }

            val uid = sessionUseCase.getCurrentUser()?.uid
                ?: return Result.failure(IllegalStateException("User is not logged in"))

            val isExpense = transaction.transactionType.equals(TransactionType.EXPENSE.name, ignoreCase = true)

            val category = categoryRepository.getCategoryById(transaction.categoryId)
                ?: return Result.failure(IllegalArgumentException("Invalid categoryId ID"))

            if (category.isExpenseCategory != isExpense) {
                return Result.failure(IllegalArgumentException("Category type does not match transaction type"))
            }

            if (!isExpense) {
                val newIncomeId = incomeRepository.getNextIncomeId()
                val newIncome = Income(
                    incomeId = newIncomeId,
                    amount = amount,
                    amountPaid = amountPaid,
                    description = transaction.description ?: "",
                    date = transaction.date,
                    categoryId = transaction.categoryId,
                    personId = transaction.personId,
                    userId = uid
                )
                Result.success(incomeRepository.addIncome(newIncome))
            } else {
                val newExpenseId = expenseRepository.getNextExpenseId()
                val newExpense = Expense(
                    expenseId = newExpenseId,
                    amount = amount,
                    amountPaid = amountPaid,
                    description = transaction.description ?: "",
                    date = transaction.date,
                    categoryId = transaction.categoryId,
                    personId = transaction.personId,
                    userId = uid
                )
                Result.success(expenseRepository.addExpense(newExpense))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateTransaction(transaction: Transaction): Result<Unit> {
        return try {
            val amount = transaction.amount
            val amountPaid = transaction.amountPaid
            if (amount <= 0) {
                return Result.failure(IllegalArgumentException("Amount must be greater than zero"))
            }

            PaymentValidation.validatePaymentAmount(amount, amountPaid).getOrElse {
                return Result.failure(it)
            }

            val uid = sessionUseCase.getCurrentUser()?.uid
                ?: return Result.failure(IllegalStateException("User is not logged in"))

            val isExpense = transaction.transactionType.equals(TransactionType.EXPENSE.name, ignoreCase = true)

            val category = categoryRepository.getCategoryById(transaction.categoryId)
                ?: return Result.failure(IllegalArgumentException("Invalid category ID"))

            if (category.isExpenseCategory != isExpense) {
                return Result.failure(IllegalArgumentException("Category type does not match transaction type"))
            }



            if (!isExpense) {
                // Fetch existing income to preserve original userId
                val existingIncome = incomeRepository.getSingleFullIncomeById(transaction.transactionId)
                    ?: return Result.failure(IllegalArgumentException("Income not found"))

                val updatedIncome = Income(
                    incomeId = transaction.transactionId,
                    amount = amount,
                    amountPaid = amountPaid,
                    description = transaction.description ?: "",
                    date = transaction.date,
                    categoryId = transaction.categoryId,
                    personId = transaction.personId,
                    userId = existingIncome.income.userId, // Preserve original userId
                    source = existingIncome.income.source,
                    isRecurring = existingIncome.income.isRecurring,
                    recurringFrequency = existingIncome.income.recurringFrequency,
                    isTaxable = existingIncome.income.isTaxable,
                    createdAt = existingIncome.income.createdAt,
                    updatedAt = Date(System.currentTimeMillis())
                )
                try {
                    incomeRepository.updateIncome(updatedIncome)
                } catch (e: Exception) {
                    return Result.failure(Exception("Error updating transaction: ${e.message}"))
                }
                Result.success(Unit)
            } else {
                // Fetch existing expense to preserve original userId
                val existingExpense = expenseRepository.getSingleFulExpenseById(transaction.transactionId)
                    ?: return Result.failure(IllegalArgumentException("Expense not found"))

                val updatedExpense = Expense(
                    expenseId = transaction.transactionId,
                    amount = amount,
                    amountPaid = amountPaid,
                    description = transaction.description ?: "",
                    date = transaction.date,
                    categoryId = transaction.categoryId,
                    personId = transaction.personId,
                    userId = existingExpense.expense.userId, // Preserve original userId
                    paymentMethod = existingExpense.expense.paymentMethod,
                    location = existingExpense.expense.location,
                    receipt = existingExpense.expense.receipt,
                    isRecurring = existingExpense.expense.isRecurring,
                    recurringFrequency = existingExpense.expense.recurringFrequency,
                    createdAt = existingExpense.expense.createdAt,
                    updatedAt = Date(System.currentTimeMillis())
                )
                try {
                    expenseRepository.updateExpense(updatedExpense)
                }catch (e: Exception) {
                    return Result.failure(Exception("Error updating transaction: ${e.message}"))
                }
                Result.success(Unit)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteTransaction(transactionId: String, isExpense: Boolean) {
        when (isExpense) {
            true -> expenseRepository.deleteExpenseById(transactionId)
            false -> incomeRepository.deleteIncomeById(transactionId)
        }
    }

    override suspend fun searchTransactions(searchCriteria: SearchCriteria): Flow<Map<Date, List<Transaction>>> {
        val dateRange = searchCriteria.dateRange
        val adjustedStartDate = dateRange?.startDate?.let {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = it
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            calendar.timeInMillis
        }
        val adjustedEndDate = dateRange?.endDate?.let {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = it
            calendar.set(Calendar.HOUR_OF_DAY, 23)
            calendar.set(Calendar.MINUTE, 59)
            calendar.set(Calendar.SECOND, 59)
            calendar.set(Calendar.MILLISECOND, 999)
            calendar.timeInMillis
        }

        val filterType = searchCriteria.transactionType

        val personIds = searchCriteria.getPersonIds()
        val categoryIds = searchCriteria.getCategoryIds()

        // Build userIds list based on permissions
        val userIds: List<String>? = if (checkPermissionUseCase(Permission.VIEW_ALL_TRANSACTIONS)) {
            // Admin: Pass null to indicate no user filtering (all users)
            null
        } else {
            // Employee: Filter by current user only
            val userId = sessionUseCase.getCurrentUser()?.uid
                ?: throw IllegalStateException("User is not logged in")
            listOf(userId)
        }

        Log.d("TransactionServiceImpl", "canViewAll: ${userIds == null} | users: $userIds | persons: $personIds | categories: $categoryIds | start: $adjustedStartDate | end: $adjustedEndDate")

        val expenseFlow = expenseRepository.getAllFiltered(userIds, personIds, categoryIds, adjustedStartDate, adjustedEndDate).map { expenses -> expenses.map { it.toTransaction() } }
        val incomeFlow = incomeRepository.getAllFiltered(userIds, personIds, categoryIds, adjustedStartDate, adjustedEndDate).map { incomes -> incomes.map { it.toTransaction() } }

        val transactionFlow: Flow<List<Transaction>> = when (filterType) {
            TransactionType.EXPENSE -> expenseFlow
            TransactionType.INCOME -> incomeFlow
            else -> combine(expenseFlow, incomeFlow) { expenses, incomes ->
                expenses + incomes
            }
        }
        return mergeAndGroupTransactions(transactionFlow)
    }

    override suspend fun loadCurrentMonthTransactions(date: Date?): Flow<Map<Date, List<Transaction>>> {
        val canViewAll = checkPermissionUseCase(Permission.VIEW_ALL_TRANSACTIONS)
        val userIds: List<String>? = if (canViewAll) {
            // Admin: Pass null to indicate no user filtering (all users)
            null
        } else {
            // Employee: Filter by current user only
            val userId = sessionUseCase.getCurrentUser()?.uid
                ?: throw IllegalStateException("User is not logged in")
            listOf(userId)
        }

        val calendar = Calendar.getInstance()
        date?.let { calendar.time = it }
        val month = calendar.get(Calendar.MONTH)
        val year = calendar.get(Calendar.YEAR)
        val startDate = calendar.apply {
            set(Calendar.MONTH, month)
            set(Calendar.YEAR, year)
            set(Calendar.DAY_OF_MONTH, 1)
        }.time.time
        val endDate = calendar.apply {
            set(Calendar.MONTH, month)
            set(Calendar.YEAR, year)
            set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
        }.time.time

        val expenseFlow = expenseRepository.getAllFiltered(
            userIds = userIds,
            startDate = startDate,
            endDate = endDate
        )
        val incomeFlow = incomeRepository.getAllFiltered(
            userIds = userIds,
            startDate = startDate,
            endDate = endDate
        )

        return mergeAndGroupTransactions(expenseFlow, incomeFlow)
    }

    override suspend fun getCurrentMonthIncome(date: Date?): Flow<Double> {
        val canViewAll = checkPermissionUseCase(Permission.VIEW_ALL_ANALYTICS)
        val userId: String? = if (canViewAll) {
            // Admin: Pass null to get sum for all users
            null
        } else {
            // Employee: Get sum for current user only
            sessionUseCase.getCurrentUser()?.uid
                ?: throw IllegalStateException("User is not logged in")
        }

        val calendar = Calendar.getInstance()
        date?.let { calendar.time = it }
        val startDate = calendar.apply {
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time.time
        val endDate = calendar.apply {
            set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }.time.time
        return incomeRepository.getSumByDateRange(
            userId = userId,
            startDate = startDate,
            endDate = endDate
        )
    }

    override suspend fun getCurrentMonthExpense(date: Date?): Flow<Double> {
        val canViewAll = checkPermissionUseCase(Permission.VIEW_ALL_ANALYTICS)
        val userId: String? = if (canViewAll) {
            // Admin: Pass null to get sum for all users
            null
        } else {
            // Employee: Get sum for current user only
            sessionUseCase.getCurrentUser()?.uid
                ?: throw IllegalStateException("User is not logged in")
        }

        val calendar = Calendar.getInstance()
        date?.let { calendar.time = it }
        val startDate = calendar.apply {
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time.time
        val endDate = calendar.apply {
            set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }.time.time
        return expenseRepository.getSumByDateRange(
            userId = userId,
            startDate = startDate,
            endDate = endDate
        )
    }

    override suspend fun getMonthlyBalance(month: Int, year: Int): Flow<Double> {
        var userId: String? = sessionUseCase.getCurrentUser()?.uid
            ?: throw IllegalStateException("User is not logged in")

        val canViewAll = checkPermissionUseCase(Permission.VIEW_ALL_ANALYTICS)
        if (canViewAll) {
            userId = null
        }
        val calendar = Calendar.getInstance()
        val startDate = calendar.apply {
            set(Calendar.MONTH, month)
            set(Calendar.YEAR, year)
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
        }.time.time
        val endDate = calendar.apply {
            set(Calendar.MONTH, month)
            set(Calendar.YEAR, year)
            set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
        }.time.time

        val expenseSum = expenseRepository.getSumByDateRange(userId, startDate, endDate)
        val incomeSum = incomeRepository.getSumByDateRange(userId, startDate, endDate)

        return combine(expenseSum, incomeSum) { expenses, incomes ->
            incomes - expenses
        }
    }

    override suspend fun getCurrentMonthBalance(): Flow<Double> {
        val calendar = Calendar.getInstance()
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentYear = calendar.get(Calendar.YEAR)
        return getMonthlyBalance(currentMonth, currentYear)
    }


    private fun mergeAndGroupTransactions(
        transactionFlow: Flow<List<Transaction>>
    ): Flow<Map<Date, List<Transaction>>> {
        val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        
        return transactionFlow.map { transactions ->
            coroutineScope {
                Log.d("mergeAndGroupTransactions", "transactions: ${transactions.size}")
                // Process transactions in parallel
                val processedTransactions = transactions.map { transaction ->
                    async {
                        bindCategoryAndPerson(transaction)
                    }
                }.awaitAll()

                // Sort and group
                processedTransactions
                    .sortedByDescending { it.date }
                    .groupBy { transaction ->
                        @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
                        dateFormatter.parse(dateFormatter.format(transaction.date))
                    }
            }
        }
    }

    private fun mergeAndGroupTransactions(
        expenses: Flow<List<Expense>>,
        incomes: Flow<List<Income>>
    ): Flow<Map<Date, List<Transaction>>> {
        val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        
        return combine(expenses, incomes) { expenseList, incomeList ->
            coroutineScope {
                Log.d("mergeAndGroupTransactions", "expenseList: ${expenseList.size}")
                Log.d("mergeAndGroupTransactions", "incomeList: ${incomeList.size}")
                // Process expenses and incomes in parallel
                val expenseTransactionsDeferred = async {
                    expenseList.map { expense ->
                        async {
                            bindCategoryAndPerson(expense.toTransaction())
                        }
                    }.awaitAll()
                }
                
                val incomeTransactionsDeferred = async {
                    incomeList.map { income ->
                        async {
                            bindCategoryAndPerson(income.toTransaction())
                        }
                    }.awaitAll()
                }
                
                // Await both expense and income processing
                val expenseTransactions = expenseTransactionsDeferred.await()
                val incomeTransactions = incomeTransactionsDeferred.await()

                // Merge, sort and group
                (expenseTransactions + incomeTransactions)
                    .sortedByDescending { it.date }
                    .groupBy { transaction ->
                        @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
                        dateFormatter.parse(dateFormatter.format(transaction.date))
                    }
            }
        }
    }

    private suspend fun bindCategoryAndPerson(transaction: Transaction): Transaction = coroutineScope {
        // Bind category and person in parallel
        val categoryDeferred = async {
            categoryRepository.getCategoryById(transaction.categoryId)
        }
        
        val personDeferred = async {
            transaction.personId?.let { personRepository.getPersonById(it) }
        }
        
        val category = categoryDeferred.await()
        val person = personDeferred.await()
        
        transaction.copy(
            category = category,
            person = person
        )
    }
}

