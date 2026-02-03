package com.davanok.dvnkdnd.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.davanok.dvnkdnd.ui.navigation.nestedGraphs.characterFull.characterFullDestinations
import com.davanok.dvnkdnd.ui.navigation.nestedGraphs.entityInfo.entityInfoDestinations
import com.davanok.dvnkdnd.ui.navigation.nestedGraphs.newEntity.newEntityDestinations
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import com.davanok.dvnkdnd.ui.navigation.components.DefaultNavigationWrapper
import com.davanok.dvnkdnd.ui.navigation.components.NotImplementedYetScreen
import com.davanok.dvnkdnd.ui.navigation.nestedGraphs.charactersList.charactersListDestinations

@Composable
fun NavigationHost(
    modifier: Modifier = Modifier,
) {
    val backStack = rememberBackStack(Route.Main.CharactersList)

    val onBack: () -> Unit = { backStack.removeLastOrNull() }
    val navigate: (Route) -> Unit = {
        if (backStack.lastOrNull() != it)
            backStack.add(it)
    }
    val replace: (Route) -> Unit = { backStack[backStack.lastIndex] = it }

    NavDisplay(
        modifier = modifier,
        backStack = backStack,
        onBack = onBack,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider(
            fallback = { NavEntry(Route.Unknown) { NotImplementedYetScreen(onBack) } }
        ) {
            mainDestinations(navigate)

            entityInfoDestinations(onBack, navigate)

            newEntityDestinations(onBack, navigate, replace)

            characterFullDestinations(onBack, navigate)
        }
    )
}


private fun RouterEntryProvider.mainDestinations(
    navigate: (route: Route) -> Unit
) {
    charactersListDestinations(navigate)
    entry<Route.Main.Items> {
        DefaultNavigationWrapper(Route.Main.Items, navigate) {

        }
    }
    entry<Route.Main.Browse> {
        DefaultNavigationWrapper(Route.Main.Browse, navigate) {

        }
    }
    entry<Route.Main.Profile> {
        DefaultNavigationWrapper(Route.Main.Profile, navigate) {

        }
    }
}
