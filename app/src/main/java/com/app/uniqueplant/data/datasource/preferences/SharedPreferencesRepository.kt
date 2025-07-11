package com.app.uniqueplant.data.datasource.preferences

interface SharedPreferencesRepository {
    // String preferences
    fun saveString(key: String, value: String)
    fun getString(key: String, defaultValue: String = ""): String

    // Int preferences
    fun saveInt(key: String, value: Int)
    fun getInt(key: String, defaultValue: Int = 0): Int

    // Boolean preferences
    fun saveBoolean(key: String, value: Boolean)
    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean

    // Float preferences
    fun saveFloat(key: String, value: Float)
    fun getFloat(key: String, defaultValue: Float = 0f): Float

    // Long preferences
    fun saveLong(key: String, value: Long)
    fun getLong(key: String, defaultValue: Long = 0L): Long

    // Remove a preference
    fun remove(key: String)

    // Clear all preferences
    fun clear()

    // Check if key exists
    fun contains(key: String): Boolean
    fun isDefaultCategoriesAdded(): Boolean

    fun setDefaultCategoriesAdded(added: Boolean)
    fun isUserLoggedIn(): Boolean
    fun setUserLoggedIn(loggedIn: Boolean)
    fun setUserType(userType: String)
    fun getUserType(): String?
    fun removeUserType()
}
