package com.davanok.dvnkdnd.ui.navigation

import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.davanok.dvnkdnd.ui.navigation.nestedGraphs.characterFull.characterFullDestinations
import com.davanok.dvnkdnd.ui.navigation.nestedGraphs.entityInfo.entityInfoDestinations
import com.davanok.dvnkdnd.ui.navigation.nestedGraphs.newEntity.newEntityDestinations
import com.davanok.dvnkdnd.ui.pages.charactersList.CharactersListScreen

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun NavigationHost(
    modifier: Modifier = Modifier,
) {
    val backStack = rememberBackStack(Route.Main.CharactersList)

    val onBack: () -> Unit = { backStack.removeLastOrNull() }
    val navigate: (Route) -> Unit = { backStack.add(it) }
    val replace: (Route) -> Unit = { backStack[backStack.lastIndex] = it }

    NavDisplay(
        modifier = modifier,
        backStack = backStack,
        onBack = onBack,
        entryProvider = entryProvider(
            fallback = { NavEntry(Route.Unknown) { NotImplementedYetScreen(onBack) } }
        ) {
            mainDestinations(navigate)

            entityInfoDestinations(onBack, navigate)

            newEntityDestinations(onBack, navigate, replace)

            characterFullDestinations(onBack)
        }
    )
}


private fun RouterEntryProvider.mainDestinations(
    navigate: (route: Route) -> Unit
) {
    entry<Route.Main.CharactersList> {
        DefaultNavigationWrapper(Route.Main.CharactersList, navigate) {
            CharactersListScreen(
                onNewCharacter = { navigate(Route.New) },
                navigateToCharacter = { navigate(Route.CharacterFull(it.id)) }
            )
        }
    }
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
