package com.app.uniqueplant.di

import android.app.Application
import com.app.uniqueplant.ui.util.DataStoreManager
import com.app.uniqueplant.ui.util.PreferenceUtil
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Inject

@HiltAndroidApp
class UniquePlantApplication : Application() {

    @Inject
    lateinit var dataStoreManager: DataStoreManager

    // Companion object to hold the application scope
    companion object {
        // It's good practice to define the dispatcher for the scope
        val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    }

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        // Initialize PreferenceUtil with the injected DataStoreManager and the applicationScope
        PreferenceUtil.initialize(dataStoreManager, applicationScope)
    }
}
