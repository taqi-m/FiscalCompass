package com.app.uniqueplant.di

import com.app.uniqueplant.data.local.LocalDataSourceImpl
import com.app.uniqueplant.data.local.preferences.PreferencesImpl
import com.app.uniqueplant.data.managers.NetworkStateProviderImpl
import com.app.uniqueplant.data.managers.SyncTimestampManagerImpl
import com.app.uniqueplant.data.remote.services.AuthServiceImpl
import com.app.uniqueplant.data.remote.sync.EnhancedSyncManagerImpl
import com.app.uniqueplant.domain.interfaces.AuthService
import com.app.uniqueplant.domain.interfaces.LocalDataSource
import com.app.uniqueplant.domain.interfaces.NetworkStateProvider
import com.app.uniqueplant.domain.interfaces.Preferences
import com.app.uniqueplant.domain.interfaces.SyncService
import com.app.uniqueplant.domain.interfaces.TimestampProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindSyncService(syncManager: EnhancedSyncManagerImpl): SyncService

    @Binds
    @Singleton
    abstract fun bindTimestampProvider(timestampManager: SyncTimestampManagerImpl): TimestampProvider

    @Binds
    @Singleton
    abstract fun bindLocalDataSource(localDataSource: LocalDataSourceImpl): LocalDataSource

    @Binds
    @Singleton
    abstract fun bindPreferences(preferences: PreferencesImpl): Preferences

    @Binds
    @Singleton
    abstract fun bindAuthService(authService: AuthServiceImpl): AuthService

    @Binds
    @Singleton
    abstract fun bindNetworkStateProvider(networkStateProvider: NetworkStateProviderImpl): NetworkStateProvider

}