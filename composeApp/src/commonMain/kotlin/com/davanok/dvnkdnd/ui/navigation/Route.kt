package com.davanok.dvnkdnd.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface Route {
    @Serializable
    sealed interface Main {
        @Serializable data object CharactersList : Route
        @Serializable data object Items : Route
        @Serializable data object Browse : Route
        @Serializable data object Profile : Route
    }

    @Serializable
    sealed interface NewCharacter : Route {
        @Serializable data object Class : NewCharacter
    }

    @Serializable
    data class CharacterFull(val characterId: Long) : Route
}