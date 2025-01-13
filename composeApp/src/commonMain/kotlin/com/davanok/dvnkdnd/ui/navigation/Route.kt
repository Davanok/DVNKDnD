package com.davanok.dvnkdnd.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface Route {
    @Serializable data object Home : Route

    @Serializable
    sealed interface NewCharacter : Route {
        @Serializable data object Class : NewCharacter
    }
}