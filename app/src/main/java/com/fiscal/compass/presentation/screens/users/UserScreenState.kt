package com.fiscal.compass.presentation.screens.users

import com.fiscal.compass.domain.model.base.User

data class UserScreenState(
    val displayState: DisplayState? = null
)


sealed interface DisplayState {
    data object Loading : DisplayState
    data object Error : DisplayState
    data class Content(
        val userResults: UserResults,
        val contentDisplayState: ContentDisplayState? = null
    ) : DisplayState
}

sealed interface ContentDisplayState {
    data object Refreshing : ContentDisplayState
    data object Paginating : ContentDisplayState
    data object PaginationError : ContentDisplayState
}

data class UserResults(
    val users: List<User>,
    val canLoadMore: Boolean
)