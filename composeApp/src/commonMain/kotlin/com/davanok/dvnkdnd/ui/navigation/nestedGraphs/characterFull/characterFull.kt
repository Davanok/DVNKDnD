package com.davanok.dvnkdnd.ui.navigation.nestedGraphs.characterFull

import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.davanok.dvnkdnd.ui.navigation.Route
import com.davanok.dvnkdnd.ui.navigation.RouterEntryProvider
import com.davanok.dvnkdnd.ui.navigation.navEntryDecorators
import com.davanok.dvnkdnd.ui.navigation.rememberBackStack
import com.davanok.dvnkdnd.ui.pages.characterFull.CharacterFullScreen
import com.davanok.dvnkdnd.ui.pages.characterFull.CharacterFullViewModel
import com.davanok.dvnkdnd.ui.pages.editCharacter.EditCharacterScreen
import com.davanok.dvnkdnd.ui.pages.editCharacter.EditCharacterViewModel
import dev.zacsweers.metrox.viewmodel.assistedMetroViewModel


fun RouterEntryProvider.characterFullDestinations(
    onBack: () -> Unit,
    navigate: (route: Route) -> Unit,
) = entry<Route.CharacterFull> { route ->
    val characterId = route.characterId

    val backStack = rememberBackStack(Route.CharacterFull.Main)

    NavDisplay(
        backStack = backStack,
        entryDecorators = navEntryDecorators(),
        entryProvider = entryProvider {
            entry<Route.CharacterFull.Main> {
                CharacterFullScreen(
                    navigateBack = onBack,
                    navigateToEditCharacter = { backStack.add(Route.CharacterFull.Edit) },
                    navigateToEntityInfo = { navigate(Route.EntityInfoDialog(it.id)) },
                    viewModel = assistedMetroViewModel<CharacterFullViewModel, CharacterFullViewModel.Factory> { create(characterId) }
                )
            }

            entry<Route.CharacterFull.Edit> {
                EditCharacterScreen(
                    viewModel = assistedMetroViewModel<EditCharacterViewModel,  EditCharacterViewModel.Factory> { create(characterId) }
                )
            }
        }
    )
}