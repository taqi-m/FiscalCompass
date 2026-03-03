package com.fiscal.compass.di

import android.content.Context
import android.util.Log
import com.fiscal.compass.BuildConfig
import com.fiscal.compass.domain.service.analytics.AnalyticsService
import com.fiscal.compass.domain.service.analytics.FirebaseAnalyticsService
import com.fiscal.compass.domain.service.analytics.NoOpAnalyticsService
import com.fiscal.compass.domain.service.crashlytics.CrashlyticsService
import com.fiscal.compass.domain.service.crashlytics.FirebaseCrashlyticsService
import com.fiscal.compass.domain.service.crashlytics.NoOpCrashlyticsService
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AnalyticsModule {

    @Provides
    @Singleton
    fun provideFirebaseAnalytics(@ApplicationContext context: Context): FirebaseAnalytics {
        val analytics = FirebaseAnalytics.getInstance(context)

        if (BuildConfig.USE_EMULATOR) {
            // Disable analytics collection in dev builds
            analytics.setAnalyticsCollectionEnabled(false)
            Log.d("AnalyticsModule", "🔧 Analytics collection DISABLED (dev build)")
        } else {
            analytics.setAnalyticsCollectionEnabled(true)
            Log.d("AnalyticsModule", "🚀 Analytics collection ENABLED (prod build)")
        }

        return analytics
    }

    @Provides
    @Singleton
    fun provideFirebaseCrashlytics(): FirebaseCrashlytics {
        val crashlytics = FirebaseCrashlytics.getInstance()

        if (BuildConfig.USE_EMULATOR) {
            // Disable crashlytics collection in dev builds
            crashlytics.isCrashlyticsCollectionEnabled = false
            Log.d("AnalyticsModule", "🔧 Crashlytics collection DISABLED (dev build)")
        } else {
            crashlytics.isCrashlyticsCollectionEnabled = true
            Log.d("AnalyticsModule", "🚀 Crashlytics collection ENABLED (prod build)")
        }

        return crashlytics
    }

    @Provides
    @Singleton
    fun provideAnalyticsService(
        firebaseAnalytics: FirebaseAnalytics
    ): AnalyticsService {
        return if (BuildConfig.USE_EMULATOR) {
            Log.d("AnalyticsModule", "🔧 Using NoOpAnalyticsService (dev build)")
            NoOpAnalyticsService()
        } else {
            Log.d("AnalyticsModule", "🚀 Using FirebaseAnalyticsService (prod build)")
            FirebaseAnalyticsService(firebaseAnalytics)
        }
    }

    @Provides
    @Singleton
    fun provideCrashlyticsService(
        firebaseCrashlytics: FirebaseCrashlytics
    ): CrashlyticsService {
        return if (BuildConfig.USE_EMULATOR) {
            Log.d("AnalyticsModule", "🔧 Using NoOpCrashlyticsService (dev build)")
            NoOpCrashlyticsService()
        } else {
            Log.d("AnalyticsModule", "🚀 Using FirebaseCrashlyticsService (prod build)")
            FirebaseCrashlyticsService(firebaseCrashlytics)
        }
    }
}

