package com.app.uniqueplant.data.sources.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.app.uniqueplant.data.model.CategoryEntity
import com.app.uniqueplant.data.model.ExpenseEntity
import com.app.uniqueplant.data.model.IncomeEntity
import com.app.uniqueplant.data.model.PersonEntity
import com.app.uniqueplant.data.model.UserEntity
import com.app.uniqueplant.data.sources.local.dao.CategoryDao
import com.app.uniqueplant.data.sources.local.dao.ExpenseDao
import com.app.uniqueplant.data.sources.local.dao.IncomeDao
import com.app.uniqueplant.data.sources.local.dao.PersonDao
import com.app.uniqueplant.data.sources.local.dao.UserDao

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