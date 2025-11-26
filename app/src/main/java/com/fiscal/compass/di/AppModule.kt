package com.fiscal.compass.di

import com.fiscal.compass.data.local.LocalDataSourceImpl
import com.fiscal.compass.data.local.preferences.PreferencesImpl
import com.fiscal.compass.data.managers.NetworkStateProviderImpl
import com.fiscal.compass.data.managers.SyncTimestampManagerImpl
import com.fiscal.compass.data.remote.services.AuthServiceImpl
import com.fiscal.compass.data.remote.sync.EnhancedSyncManagerImpl
import com.fiscal.compass.domain.interfaces.AuthService
import com.fiscal.compass.domain.interfaces.LocalDataSource
import com.fiscal.compass.domain.interfaces.NetworkStateProvider
import com.fiscal.compass.domain.interfaces.Preferences
import com.fiscal.compass.domain.interfaces.SyncService
import com.fiscal.compass.domain.interfaces.TimestampProvider
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