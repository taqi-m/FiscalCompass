package com.app.uniqueplant.data.local

import com.app.uniqueplant.domain.interfaces.LocalDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalDataSourceImpl @Inject constructor(
    private val appDatabase: AppDatabase,
) : LocalDataSource {
    override suspend fun hasUnsyncedData(): Boolean {
        return appDatabase.expenseDao().hasUnsyncedData() ||
                appDatabase.incomeDao().hasUnsyncedData()
    }

    override fun getUnsyncedExpenseCount(): Flow<Int> {
        return appDatabase.expenseDao().getUnsyncedExpenseCount()
    }

    override fun getUnsyncedIncomeCount(): Flow<Int> {
        return appDatabase.incomeDao().getUnsyncedIncomeCount()
    }
}