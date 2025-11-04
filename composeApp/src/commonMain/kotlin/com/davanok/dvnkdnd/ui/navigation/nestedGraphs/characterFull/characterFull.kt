package com.davanok.dvnkdnd.ui.navigation.nestedGraphs.characterFull

import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.davanok.dvnkdnd.ui.navigation.Route
import com.davanok.dvnkdnd.ui.navigation.RouterEntryProvider
import com.davanok.dvnkdnd.ui.navigation.rememberBackStack
import com.davanok.dvnkdnd.ui.pages.characterFull.CharacterFullScreen
import com.davanok.dvnkdnd.ui.pages.characterFull.CharacterFullViewModel
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf


fun RouterEntryProvider.characterFullDestinations(
    onBack: () -> Unit,
    navigate: (route: Route) -> Unit,
) = entry<Route.CharacterFull> { route ->
    val backStack = rememberBackStack(Route.CharacterFull.Main)

    val sharedViewModel: CharacterFullViewModel = koinViewModel {
        parametersOf(route.characterId)
    }

    NavDisplay(
        backStack = backStack,
        entryProvider = entryProvider {
            entry<Route.CharacterFull.Main> {
                CharacterFullScreen(
                    navigateBack = onBack,
                    navigateToEntityInfo = { navigate(Route.EntityInfoDialog(it.id)) },
                    viewModel = sharedViewModel
                )
            }
        }
    )
}