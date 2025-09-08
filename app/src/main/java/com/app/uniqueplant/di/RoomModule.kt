package com.app.uniqueplant.di

import android.content.Context
import androidx.room.Room
import com.app.uniqueplant.data.sources.local.AppDatabase
import com.app.uniqueplant.data.sources.local.dao.CategoryDao
import com.app.uniqueplant.data.sources.local.dao.ExpenseDao
import com.app.uniqueplant.data.sources.local.dao.IncomeDao
import com.app.uniqueplant.data.sources.local.dao.PersonDao
import com.app.uniqueplant.data.sources.local.dao.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room
            .databaseBuilder(
                context,
                AppDatabase::class.java,
                "unique-plant-db"
            )
            .build()
    }

    @Provides
    fun provideCategoryDao(database: AppDatabase): CategoryDao {
        return database.categoryDao()
    }

    @Provides
    fun provideExpenseDao(database: AppDatabase): ExpenseDao {
        return database.expenseDao()
    }

    @Provides
    fun provideIncomeDao(database: AppDatabase): IncomeDao {
        return database.incomeDao()
    }

    @Provides
    fun provideUserDao(database: AppDatabase): UserDao {
        return database.userDao()
    }

    @Provides
    fun providePersonDao(database: AppDatabase): PersonDao {
        return database.personDao()
    }
}
