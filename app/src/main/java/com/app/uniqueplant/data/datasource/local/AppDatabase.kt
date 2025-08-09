package com.app.uniqueplant.data.datasource.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.app.uniqueplant.data.datasource.local.dao.CategoryDao
import com.app.uniqueplant.data.datasource.local.dao.ExpenseDao
import com.app.uniqueplant.data.datasource.local.dao.IncomeDao
import com.app.uniqueplant.data.datasource.local.dao.PersonDao
import com.app.uniqueplant.data.datasource.local.dao.UserDao
import com.app.uniqueplant.data.model.*

@Database(
    entities = [User::class, Expense::class, Income::class, Category::class, Person::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(DateConverter::class)
abstract class AppDatabase : RoomDatabase() {
    
    abstract fun userDao(): UserDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun incomeDao(): IncomeDao
    abstract fun categoryDao(): CategoryDao
    abstract fun personDao(): PersonDao
}