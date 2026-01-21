package com.davanok.dvnkdnd.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavEntryDecorator
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import kotlinx.serialization.json.Json


typealias RouterEntryProvider = EntryProviderScope<Route>


private val backStackSaver = Saver<SnapshotStateList<Route>, List<String>>(
    save = { list -> list.map { Json.encodeToString(Route.serializer(), it) } },
    restore = { strings ->
        strings.map { Json.decodeFromString(Route.serializer(), it) }.toMutableStateList()
    }
)

@Composable
fun rememberBackStack(startDestination: Route) = rememberSaveable(saver = backStackSaver) {
    mutableStateListOf(startDestination)
}

@Composable
fun <T : Any> navEntryDecorators(): List<NavEntryDecorator<T>> = listOf(
    rememberSaveableStateHolderNavEntryDecorator(),
    rememberViewModelStoreNavEntryDecorator()
)