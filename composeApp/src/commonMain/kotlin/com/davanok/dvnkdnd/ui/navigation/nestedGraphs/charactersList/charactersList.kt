package com.davanok.dvnkdnd.ui.navigation.nestedGraphs.charactersList

import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.davanok.dvnkdnd.ui.navigation.DefaultNavigationWrapper
import com.davanok.dvnkdnd.ui.navigation.Route
import com.davanok.dvnkdnd.ui.navigation.RouterEntryProvider
import com.davanok.dvnkdnd.ui.navigation.navEntryDecorators
import com.davanok.dvnkdnd.ui.navigation.rememberBackStack
import com.davanok.dvnkdnd.ui.pages.charactersList.characterShortInfo.CharacterShortInfoScreen
import com.davanok.dvnkdnd.ui.pages.charactersList.charactersList.CharactersListScreen
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
fun RouterEntryProvider.charactersListDestinations(
    navigate: (route: Route) -> Unit,
) = entry<Route.Main.CharactersList> {
    val backStack = rememberBackStack(Route.Main.CharactersList.CharactersList)
    val nestedNavigate: (route: Route) -> Unit = { backStack.add(it) }
    val nestedBack: () -> Unit = { backStack.removeLastOrNull() }

    DefaultNavigationWrapper(Route.Main.CharactersList, navigate) {
        NavDisplay(
            backStack = backStack,
            sceneStrategy = rememberListDetailSceneStrategy(),
            entryDecorators = navEntryDecorators(),
            entryProvider = entryProvider {
                entry<Route.Main.CharactersList.CharactersList>(
                    metadata = ListDetailSceneStrategy.listPane()
                ) {
                    CharactersListScreen(
                        onNewCharacter = { navigate(Route.New.Character) },
                        navigateToCharacter = { character ->
                            nestedNavigate(Route.Main.CharactersList.CharacterShortInfo(character.id))
                        },
                        navigateToCharacterFull = { character ->
                            navigate(Route.CharacterFull(character.id))
                        }
                    )
                }

                entry<Route.Main.CharactersList.CharacterShortInfo>(
                    metadata = ListDetailSceneStrategy.detailPane()
                ) { route ->
                    CharacterShortInfoScreen(
                        navigateToCharacter = { navigate(Route.CharacterFull(route.characterId)) },
                        navigateBack = nestedBack,
                        viewModel = koinViewModel { parametersOf(route.characterId) }
                    )
                }
            }
        )
    }
}