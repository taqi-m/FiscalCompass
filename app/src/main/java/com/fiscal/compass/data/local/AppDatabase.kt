package com.fiscal.compass.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.fiscal.compass.data.local.dao.CategoryDao
import com.fiscal.compass.data.local.dao.ExpenseDao
import com.fiscal.compass.data.local.dao.IncomeDao
import com.fiscal.compass.data.local.dao.PersonDao
import com.fiscal.compass.data.local.dao.UserDao
import com.fiscal.compass.data.local.model.CategoryEntity
import com.fiscal.compass.data.local.model.ExpenseEntity
import com.fiscal.compass.data.local.model.IncomeEntity
import com.fiscal.compass.data.local.model.PersonEntity
import com.fiscal.compass.data.local.model.UserEntity

@Database(
    entities = [UserEntity::class, ExpenseEntity::class, IncomeEntity::class, CategoryEntity::class, PersonEntity::class],
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