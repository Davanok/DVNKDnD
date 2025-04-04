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
    }
    @Serializable
    data object New : Route {
        // main
        @Serializable data object Character : Route {
            @Serializable data object Main : Route
            @Serializable data class Stats(val characterId: Long) : Route
        }
        @Serializable data object Item : Route
        // custom
        @Serializable data object Spell : Route
        @Serializable data object Ability : Route
        @Serializable data object Feature : Route
        // homebrew
        @Serializable data object Class : Route
        @Serializable data object Race : Route
        @Serializable data object Background : Route
    }

    @Serializable
    data class CharacterFull(val characterId: Long) : Route

    @Serializable
    data class EntityInfo(val entityType: String, val entityId: Long) : Route
}