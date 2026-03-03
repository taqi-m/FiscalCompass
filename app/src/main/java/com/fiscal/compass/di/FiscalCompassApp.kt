package com.fiscal.compass.di

import android.app.Application
import com.fiscal.compass.BuildConfig
import com.fiscal.compass.domain.initialization.AppInitializationManager
import com.fiscal.compass.domain.service.analytics.AnalyticsService
import com.fiscal.compass.domain.service.crashlytics.CrashlyticsService
import com.fiscal.compass.domain.sync.AutoSyncManager
import com.fiscal.compass.ui.util.DataStoreManager
import com.fiscal.compass.ui.util.PreferenceUtil
import com.google.firebase.FirebaseApp
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Inject

@HiltAndroidApp
class FiscalCompassApp : Application() {

    @Inject
    lateinit var dataStoreManager: DataStoreManager
    @Inject
    lateinit var autoSyncManager: AutoSyncManager
    @Inject
    lateinit var initializationManager: AppInitializationManager
    @Inject
    lateinit var analyticsService: AnalyticsService
    @Inject
    lateinit var crashlyticsService: CrashlyticsService

    // Companion object to hold the application scope
    companion object {
        // It's good practice to define the dispatcher for the scope
        val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    }

    override fun onCreate() {
        super.onCreate()

        FirebaseApp.initializeApp(this)

        // Configure Crashlytics collection based on build flavor
        FirebaseCrashlytics.getInstance().isCrashlyticsCollectionEnabled = !BuildConfig.USE_EMULATOR

        // Initialize PreferenceUtil with the injected DataStoreManager and the applicationScope
        PreferenceUtil.initialize(dataStoreManager, applicationScope)

        autoSyncManager.initialize(scope = applicationScope)

        initializationManager.initialize(scope = applicationScope)
    }
}
