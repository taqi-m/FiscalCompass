package com.app.uniqueplant.data.datasource.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharedPreferencesRepositoryImpl @Inject constructor(
    context: Context
) : SharedPreferencesRepository {

    companion object {
        private const val PREFERENCES_NAME = "unique_plant_preferences"
    }

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    override fun saveString(key: String, value: String) {
        sharedPreferences.edit { putString(key, value) }
    }

    override fun getString(key: String, defaultValue: String): String {
        return sharedPreferences.getString(key, defaultValue) ?: defaultValue
    }

    override fun saveInt(key: String, value: Int) {
        sharedPreferences.edit { putInt(key, value) }
    }

    override fun getInt(key: String, defaultValue: Int): Int {
        return sharedPreferences.getInt(key, defaultValue)
    }

    override fun saveBoolean(key: String, value: Boolean) {
        sharedPreferences.edit { putBoolean(key, value) }
    }

    override fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return sharedPreferences.getBoolean(key, defaultValue)
    }

    override fun saveFloat(key: String, value: Float) {
        sharedPreferences.edit { putFloat(key, value) }
    }

    override fun getFloat(key: String, defaultValue: Float): Float {
        return sharedPreferences.getFloat(key, defaultValue)
    }

    override fun saveLong(key: String, value: Long) {
        sharedPreferences.edit { putLong(key, value) }
    }

    override fun getLong(key: String, defaultValue: Long): Long {
        return sharedPreferences.getLong(key, defaultValue)
    }

    override fun remove(key: String) {
        sharedPreferences.edit { remove(key) }
    }

    override fun clear() {
        sharedPreferences.edit { clear() }
    }

    override fun contains(key: String): Boolean {
        return sharedPreferences.contains(key)
    }

    override fun isDefaultCategoriesAdded(): Boolean {
        return sharedPreferences.getBoolean("default_categories_added", false)
    }

    override fun setDefaultCategoriesAdded(added: Boolean) {
        sharedPreferences.edit { putBoolean("default_categories_added", added) }
    }

    override fun isUserLoggedIn(): Boolean {
        return sharedPreferences.getBoolean("user_logged_in", false)
    }

    override fun setUserLoggedIn(loggedIn: Boolean) {
        sharedPreferences.edit{ putBoolean("user_logged_in", loggedIn)}
    }

    override fun setUserType(userType: String) {
        sharedPreferences.edit{ putString("user_type", userType)}
    }

    override fun getUserType(): String? {
        return sharedPreferences.getString("user_type", null)
    }

    override fun removeUserType() {
        remove("user_type")
    }

    override fun setDarkMode(darkMode: Boolean) {
        sharedPreferences.edit { putBoolean("dark_mode", darkMode) }
    }

    override fun isDefaultPersonTypesAdded(): Boolean {
        return sharedPreferences.getBoolean("default_person_types_added", false)
    }

    override fun setDefaultPersonTypesAdded(bool: Boolean) {
        sharedPreferences.edit {
            putBoolean("default_person_types_added", bool)
        }
    }
}
