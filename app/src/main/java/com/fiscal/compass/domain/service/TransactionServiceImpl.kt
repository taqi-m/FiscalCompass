package com.fiscal.compass.domain.service

import com.fiscal.compass.data.mappers.toTransaction
import com.fiscal.compass.data.rbac.Permission
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
import com.fiscal.compass.domain.util.DateRange
import com.fiscal.compass.domain.validation.PaymentValidation
import com.fiscal.compass.presentation.model.TransactionType
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
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

    override suspend fun getTransactionById(transactionId: Long, isExpense: Boolean): Transaction {
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
                val newIncome = Income(
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
                val newExpense = Expense(
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
                val updatedIncome = Income(
                    incomeId = transaction.transactionId,
                    amount = amount,
                    amountPaid = amountPaid,
                    description = transaction.description ?: "",
                    date = transaction.date,
                    categoryId = transaction.categoryId,
                    personId = transaction.personId,
                    userId = uid
                )
                try {
                    incomeRepository.updateIncome(updatedIncome)
                } catch (e: Exception) {
                    return Result.failure(Exception("Error updating transaction: ${e.message}"))
                }
                Result.success(Unit)
            } else {
                val updatedExpense = Expense(
                    expenseId = transaction.transactionId,
                    amount = amount,
                    amountPaid = amountPaid,
                    description = transaction.description ?: "",
                    date = transaction.date,
                    categoryId = transaction.categoryId,
                    personId = transaction.personId,
                    userId = uid
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

    override suspend fun deleteTransaction(transactionId: Long, isExpense: Boolean) {
        when (isExpense) {
            true -> expenseRepository.deleteExpenseById(transactionId)
            false -> incomeRepository.deleteIncomeById(transactionId)
        }
    }

    override suspend fun searchTransactions(
        personIds: List<Long>?,
        categoryIds: List<Long>?,
        dateRange: DateRange?,
        filterType: String?
    ): Map<Date, List<Transaction>> {
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

        val expenses = if (filterType?.lowercase() == "income") {
            emptyList()
        } else {
            expenseRepository.getAllFiltered(
                personIds = personIds,
                categoryIds = categoryIds,
                startDate = adjustedStartDate,
                endDate = adjustedEndDate
            ).first()
        }

        val incomes = if (filterType?.lowercase() == "expense") {
            emptyList()
        } else {
            incomeRepository.getAllFiltered(
                personIds = personIds,
                categoryIds = categoryIds,
                startDate = adjustedStartDate,
                endDate = adjustedEndDate
            ).first()
        }
        return mergeAndGroupTransactions(expenses, incomes)
    }

    override suspend fun searchTransactions(
        personIds: List<Long>?,
        categoryIds: List<Long>?,
        startDate: Long?,
        endDate: Long?,
        filterType: String?
    ): Map<Date, List<Transaction>> {
        val dateRange = if (startDate != null || endDate != null) {
            DateRange.from(startDate, endDate)
        } else {
            null
        }
        return searchTransactions(personIds, categoryIds, dateRange, filterType)
    }

    override suspend fun loadCurrentMonthTransactions(date: Date?): Flow<Map<Date, List<Transaction>>> {
        val userId = getCurrentUserId()
        val userIds = mutableListOf(userId)
        val canViewAll = checkPermissionUseCase(Permission.VIEW_ALL_TRANSACTIONS)
        if (canViewAll) {
            userIds.clear()
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

        return combine(expenseFlow, incomeFlow) { expenses, incomes ->
            mergeAndGroupTransactions(expenses, incomes)
        }
    }

    override suspend fun getCurrentMonthIncome(date: Date?): Flow<Double> {
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
            userId = getCurrentUserId(),
            startDate = startDate,
            endDate = endDate
        )
    }

    override suspend fun getCurrentMonthExpense(date: Date?): Flow<Double> {
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
            userId = getCurrentUserId(),
            startDate = startDate,
            endDate = endDate
        )
    }

    override suspend fun getMonthlyBalance(month: Int, year: Int): Flow<Double> {
        var userId: String? = userRepository.getLoggedInUser()?.userId
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

    private suspend fun getCurrentUserId(): String {
        val userId = userRepository.getLoggedInUser()?.userId
        if (userId != null) {
            if (checkPermissionUseCase(Permission.VIEW_ALL_TRANSACTIONS))
                return ""
            return userId
        }
        return ""
    }

    private suspend fun mergeAndGroupTransactions(
        expenses: List<Expense>,
        incomes: List<Income>
    ): Map<Date, List<Transaction>> = coroutineScope {
        val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        
        // Process expenses and incomes in parallel
        val expenseTransactionsDeferred = async {
            expenses.map { expense ->
                async {
                    bindCategoryAndPerson(expense.toTransaction())
                }
            }.awaitAll()
        }
        
        val incomeTransactionsDeferred = async {
            incomes.map { income ->
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
