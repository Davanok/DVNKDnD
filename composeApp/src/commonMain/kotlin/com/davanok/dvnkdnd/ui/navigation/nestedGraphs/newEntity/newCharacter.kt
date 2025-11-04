package com.davanok.dvnkdnd.ui.navigation.nestedGraphs.newEntity

import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.davanok.dvnkdnd.ui.navigation.Route
import com.davanok.dvnkdnd.ui.navigation.RouterEntryProvider
import com.davanok.dvnkdnd.ui.navigation.rememberBackStack
import com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.NewCharacterViewModel
import com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.loadingScreen.LoadingDataScreen
import com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.newCharacterAttributes.NewCharacterAttributesScreen
import com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.newCharacterHealth.NewCharacterHealthScreen
import com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.newCharacterMain.NewCharacterMainScreen
import com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.newCharacterThrows.NewCharacterStatsLargeScreen
import com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.savingNewCharacter.SavingNewCharacterScreen
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf


fun RouterEntryProvider.characterCreationFlow(
    onBack: () -> Unit,
    replace: (Route) -> Unit
) = entry<Route.New.Character> {
    val backStack = rememberBackStack(Route.New.Character.LoadData)
    
    val sharedViewModel: NewCharacterViewModel = koinViewModel()

    val nestedOnBack: () -> Unit = { backStack.removeLastOrNull() }
    val nestedNavigate: (route: Route) -> Unit = { backStack.add(it) }
    val nestedReplace: (route: Route) -> Unit = { backStack[backStack.lastIndex] = it }

    NavDisplay(
        backStack = backStack,
        entryProvider = entryProvider {
            entry<Route.New.Character.LoadData> {
                LoadingDataScreen(
                    onBack = onBack,
                    onContinue = { nestedReplace(Route.New.Character.Main) }
                )
            }
            entry<Route.New.Character.Main> {
                NewCharacterMainScreen(
                    navigateToEntityInfo = { id -> nestedNavigate(Route.EntityInfoDialog(id)) },
                    onBack = onBack,
                    onContinue = { nestedNavigate(Route.New.Character.Stats) },
                    viewModel = koinViewModel { parametersOf(sharedViewModel) }
                )
            }
            entry<Route.New.Character.Stats> {
                NewCharacterAttributesScreen(
                    onBack = nestedOnBack,
                    onContinue = { nestedNavigate(Route.New.Character.Throws) },
                    viewModel = koinViewModel { parametersOf(sharedViewModel) }
                )
            }
            entry<Route.New.Character.Throws> {
                NewCharacterStatsLargeScreen(
                    onBack = nestedOnBack,
                    onContinue = { nestedNavigate(Route.New.Character.Health) },
                    viewModel = koinViewModel { parametersOf(sharedViewModel) }
                )
            }
            entry<Route.New.Character.Health> {
                NewCharacterHealthScreen(
                    onBack = nestedOnBack,
                    onContinue = { nestedNavigate(Route.New.Character.Save) },
                    viewModel = koinViewModel { parametersOf(sharedViewModel) }
                )
            }
            entry<Route.New.Character.Save> {
                SavingNewCharacterScreen(
                    onBack = onBack,
                    onGoToCharacter = { id -> replace(Route.CharacterFull(id)) },
                    viewModel = koinViewModel { parametersOf(sharedViewModel) }
                )
            }
        }
    )
}