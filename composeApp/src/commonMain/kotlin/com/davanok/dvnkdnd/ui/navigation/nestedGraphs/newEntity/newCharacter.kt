package com.davanok.dvnkdnd.ui.navigation.nestedGraphs.newEntity

import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.davanok.dvnkdnd.ui.navigation.Route
import com.davanok.dvnkdnd.ui.navigation.RouterEntryProvider
import com.davanok.dvnkdnd.ui.navigation.navEntryDecorators
import com.davanok.dvnkdnd.ui.navigation.rememberBackStack
import com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.NewCharacterViewModel
import com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.loadingScreen.LoadingDataScreen
import com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.newCharacterAttributes.NewCharacterAttributesScreen
import com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.newCharacterAttributes.NewCharacterAttributesViewModel
import com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.newCharacterHealth.NewCharacterHealthScreen
import com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.newCharacterHealth.NewCharacterHealthViewModel
import com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.newCharacterMain.NewCharacterMainScreen
import com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.newCharacterMain.NewCharacterMainViewModel
import com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.newCharacterThrows.NewCharacterThrowsScreen
import com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.newCharacterThrows.NewCharacterThrowsViewModel
import com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.savingNewCharacter.SavingNewCharacterScreen
import com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.savingNewCharacter.SavingNewCharacterViewModel
import dev.zacsweers.metrox.viewmodel.assistedMetroViewModel
import dev.zacsweers.metrox.viewmodel.metroViewModel


fun RouterEntryProvider.characterCreationFlow(
    onBack: () -> Unit,
    replace: (Route) -> Unit
) = entry<Route.New.Character> {
    val backStack = rememberBackStack(Route.New.Character.LoadData)
    val sharedViewModel: NewCharacterViewModel = metroViewModel()

    val nestedOnBack: () -> Unit = { backStack.removeLastOrNull() }
    val nestedNavigate: (route: Route) -> Unit = { backStack.add(it) }
    val nestedReplace: (route: Route) -> Unit = { backStack[backStack.lastIndex] = it }

    NavDisplay(
        backStack = backStack,
        onBack = nestedOnBack,
        entryDecorators = navEntryDecorators(),
        entryProvider = entryProvider {
            entry<Route.New.Character.LoadData> {
                LoadingDataScreen(
                    onBack = onBack,
                    onContinue = { nestedReplace(Route.New.Character.Main) },
                    viewModel = metroViewModel()
                )
            }
            entry<Route.New.Character.Main> {
                NewCharacterMainScreen(
                    navigateToEntityInfo = { nestedNavigate(Route.EntityInfoDialog(it.id)) },
                    onBack = onBack,
                    onContinue = { nestedNavigate(Route.New.Character.Stats) },
                    viewModel = assistedMetroViewModel<NewCharacterMainViewModel, NewCharacterMainViewModel.Factory> { create(sharedViewModel) }
                )
            }
            entry<Route.New.Character.Stats> {
                NewCharacterAttributesScreen(
                    onBack = nestedOnBack,
                    onContinue = { nestedNavigate(Route.New.Character.Throws) },
                    viewModel = assistedMetroViewModel<NewCharacterAttributesViewModel, NewCharacterAttributesViewModel.Factory> { create(sharedViewModel) }
                )
            }
            entry<Route.New.Character.Throws> {
                NewCharacterThrowsScreen(
                    onBack = nestedOnBack,
                    onContinue = { nestedNavigate(Route.New.Character.Health) },
                    viewModel = assistedMetroViewModel<NewCharacterThrowsViewModel, NewCharacterThrowsViewModel.Factory> { create(sharedViewModel) }
                )
            }
            entry<Route.New.Character.Health> {
                NewCharacterHealthScreen(
                    onBack = nestedOnBack,
                    onContinue = { nestedNavigate(Route.New.Character.Save) },
                    viewModel = assistedMetroViewModel<NewCharacterHealthViewModel, NewCharacterHealthViewModel.Factory> { create(sharedViewModel) }
                )
            }
            entry<Route.New.Character.Save> {
                SavingNewCharacterScreen(
                    onBack = onBack,
                    onGoToCharacter = { id -> replace(Route.CharacterFull(id)) },
                    viewModel = assistedMetroViewModel<SavingNewCharacterViewModel, SavingNewCharacterViewModel.Factory> { create(sharedViewModel) }
                )
            }
        }
    )
}