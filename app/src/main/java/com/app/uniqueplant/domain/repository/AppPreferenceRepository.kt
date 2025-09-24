package com.app.uniqueplant.domain.repository


interface AppPreferenceRepository {
    fun setDefaultCategoriesAdded(added: Boolean)
    fun isDefaultCategoriesAdded(): Boolean
    fun isUserLoggedIn(): Boolean
    fun setUserLoggedIn(loggedIn: Boolean)
    fun setUserType(userType: String)
    fun getUserType(): String?
    fun setUserInfo(name: String, email: String)
    fun getUserInfo(): Pair<String, String>
    fun removeUserType()
    fun setDarkMode(darkMode: Boolean)
    fun isDefaultPersonTypesAdded(): Boolean
    fun setDefaultPersonTypesAdded(bool: Boolean)
}