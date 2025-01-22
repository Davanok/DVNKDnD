package com.davanok.dvnkdnd.ui.navigation

import com.davanok.dvnkdnd.ui.navigation.Route
import kotlinx.serialization.Serializable

@Serializable
sealed interface Route {
    @Serializable
    data object Main {
        @Serializable data object CharactersList : Route
        @Serializable data object Items : Route
        @Serializable data object Browse : Route
        @Serializable data object Profile : Route
    }
    @Serializable
    data object New : Route {
        @Serializable data object Character : Route
        @Serializable data object Item : Route
        @Serializable data object Spell : Route
        @Serializable data object Class : Route
        @Serializable data object Race : Route
        @Serializable data object Background : Route
        @Serializable data object Ability : Route
    }

    @Serializable
    data class CharacterFull(val characterId: Long) : Route
}