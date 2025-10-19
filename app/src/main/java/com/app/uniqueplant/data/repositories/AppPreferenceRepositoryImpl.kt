package com.app.uniqueplant.data.repositories

import com.app.uniqueplant.domain.repository.PreferenceManager
import com.app.uniqueplant.domain.repository.AppPreferenceRepository
import javax.inject.Inject
import javax.inject.Singleton

const val DEFAULT_CATEGORIES_ADDED = "DEFAULT_CATEGORIES_ADDED"
const val DEFAULT_PERSON_TYPES_ADDED = "DEFAULT_PERSON_TYPES_ADDED"
const val USER_LOGGED_IN = "USER_LOGGED_IN"
const val USER_TYPE = "USER_TYPE"
const val USER_NAME = "USER_NAME"
const val USER_EMAIL = "USER_EMAIL"
const val DARK_MODE = "DARK_MODE"

/**
 * Implementation of [AppPreferenceRepository] using [PreferenceManager] for data storage.
 */
@Singleton
class AppPreferenceRepositoryImpl @Inject constructor(
    private val preferenceManager: PreferenceManager
) : AppPreferenceRepository {
    override fun setDefaultCategoriesAdded(added: Boolean) {
        preferenceManager.saveBoolean(DEFAULT_CATEGORIES_ADDED, added)
    }

    override fun isDefaultCategoriesAdded(): Boolean {
        return preferenceManager.getBoolean(DEFAULT_CATEGORIES_ADDED, false)
    }

    override fun isUserLoggedIn(): Boolean {
        return preferenceManager.getBoolean(USER_LOGGED_IN, false)
    }

    override fun setUserLoggedIn(loggedIn: Boolean) {
        preferenceManager.saveBoolean(USER_LOGGED_IN, loggedIn)
    }

    override fun setUserType(userType: String) {
        preferenceManager.saveString(USER_TYPE, userType)
    }

    override fun getUserType(): String? {
        return preferenceManager.getString(USER_TYPE, "")
    }

    override fun setUserInfo(name: String, email: String) {
        preferenceManager.saveString(USER_NAME, name)
        preferenceManager.saveString(USER_EMAIL, email)
    }

    override fun getUserInfo(): Pair<String, String> {
        val name = preferenceManager.getString(USER_NAME, "User")
        val email = preferenceManager.getString(USER_EMAIL, "")
        return Pair(name, email)
    }

    override fun removeUserType() {
        preferenceManager.remove(USER_TYPE)
    }

    override fun setDarkMode(darkMode: Boolean) {
        preferenceManager.saveBoolean(DARK_MODE, darkMode)
    }

    override fun isDefaultPersonTypesAdded(): Boolean {
        return preferenceManager.getBoolean(DEFAULT_PERSON_TYPES_ADDED, false)
    }

    override fun setDefaultPersonTypesAdded(bool: Boolean) {
        preferenceManager.saveBoolean(DEFAULT_PERSON_TYPES_ADDED, bool)
    }

}

