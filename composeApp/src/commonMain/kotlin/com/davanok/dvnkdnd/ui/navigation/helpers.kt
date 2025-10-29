package com.davanok.dvnkdnd.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.navigation3.runtime.EntryProviderScope


typealias RouterEntryProvider = EntryProviderScope<Route>

@Composable
fun rememberBackStack(startDestination: Route) = rememberSaveable { mutableStateListOf(startDestination) }