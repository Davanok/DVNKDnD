package com.davanok.dvnkdnd.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface Route {
    @Serializable
    data object Main {
        @Serializable data object CharactersList : Route
        @Serializable data object Items : Route
        @Serializable data object Browse : Route
        @Serializable data object Profile : Route
        @Serializable data object New : Route
    }

    @Serializable
    data class CharacterFull(val characterId: Long) : Route
}