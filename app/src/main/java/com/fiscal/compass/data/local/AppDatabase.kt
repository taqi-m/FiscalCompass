package com.fiscal.compass.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.fiscal.compass.data.local.model.CategoryEntity
import com.fiscal.compass.data.local.model.ExpenseEntity
import com.fiscal.compass.data.local.model.IncomeEntity
import com.fiscal.compass.data.local.model.PersonEntity
import com.fiscal.compass.data.local.model.UserEntity
import com.fiscal.compass.data.local.dao.CategoryDao
import com.fiscal.compass.data.local.dao.ExpenseDao
import com.fiscal.compass.data.local.dao.IncomeDao
import com.fiscal.compass.data.local.dao.PersonDao
import com.fiscal.compass.data.local.dao.UserDao

@Database(
    entities = [UserEntity::class, ExpenseEntity::class, IncomeEntity::class, CategoryEntity::class, PersonEntity::class],
    version = 2,
    exportSchema = true
)
@TypeConverters(DateConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun incomeDao(): IncomeDao
    abstract fun categoryDao(): CategoryDao
    abstract fun personDao(): PersonDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Add amountPaid column to incomes table with default value 0.0
                db.execSQL("ALTER TABLE incomes ADD COLUMN amountPaid REAL NOT NULL DEFAULT 0.0")

                // Add amountPaid column to expenses table with default value 0.0
                db.execSQL("ALTER TABLE expenses ADD COLUMN amountPaid REAL NOT NULL DEFAULT 0.0")
            }
        }
    }
}