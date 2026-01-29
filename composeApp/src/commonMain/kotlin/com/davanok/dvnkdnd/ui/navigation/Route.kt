package com.davanok.dvnkdnd.ui.navigation

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Immutable
@Serializable
sealed interface Route {
    @Serializable data object Unknown: Route
    @Serializable
    data object Main {
        @Serializable data object CharactersList : Route {
            @Serializable data object CharactersList : Route
            @Serializable data class CharacterShortInfo(val characterId: Uuid) : Route
        }
        @Serializable data object Items : Route
        @Serializable data object Browse : Route
        @Serializable data object Profile : Route
    }
    @Serializable
    data object New : Route {
        // main
        @Serializable
        data object Character : Route {
            @Serializable data object LoadData : Route
            @Serializable data object Main : Route
            @Serializable data object Stats : Route
            @Serializable data object Throws : Route
            @Serializable data object Health : Route
            @Serializable data object Save : Route
        }
        @Serializable data object Item : Route
        // custom
        @Serializable data object Spell : Route
        @Serializable data object Feature : Route
        // homebrew
        @Serializable data object Class : Route
        @Serializable data object Race : Route
        @Serializable data object Background : Route
    }

    @Serializable
    data class CharacterFull(val characterId: Uuid) : Route {
        @Serializable data object Main: Route
    }

    @Serializable
    data class EntityInfoDialog(val entityId: Uuid) : Route
}