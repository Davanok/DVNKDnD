package com.davanok.dvnkdnd.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import androidx.navigation.toRoute
import androidx.savedstate.SavedState
import androidx.savedstate.read
import androidx.savedstate.write
import com.davanok.dvnkdnd.ui.pages.characterFull.CharacterFullScreen
import com.davanok.dvnkdnd.ui.pages.characterFull.CharacterFullViewModel
import com.davanok.dvnkdnd.ui.pages.charactersList.CharactersListScreen
import com.davanok.dvnkdnd.ui.pages.dndEntityInfo.DnDEntityInfo
import com.davanok.dvnkdnd.ui.pages.newEntity.NewEntityScreen
import com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.NewCharacterViewModel
import com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.loadingScreen.LoadingDataScreen
import com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.newCharacterAttributes.NewCharacterAttributesScreen
import com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.newCharacterHealth.NewCharacterHealthScreen
import com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.newCharacterMain.NewCharacterMainScreen
import com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.newCharacterThrows.NewCharacterStatsLargeScreen
import com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.savingNewCharacter.SavingNewCharacterScreen
import com.davanok.dvnkdnd.ui.pages.newEntity.newItem.NewItemScreen
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.parameter.parametersOf
import kotlin.reflect.typeOf
import kotlin.uuid.Uuid


object UuidNavType : NavType<Uuid>(true) {
    override fun put(
        bundle: SavedState,
        key: String,
        value: Uuid,
    ) {
        bundle.write {
            putString(key, value.toHexDashString())
        }
    }

    override fun get(
        bundle: SavedState,
        key: String,
    ): Uuid? {
        bundle.read {
            return getStringOrNull(key)?.let { Uuid.parse(it) }
        }
    }

    override fun parseValue(value: String): Uuid {
        return Uuid.parse(value)
    }
}


@Composable
fun NavigationHost(
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier,
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = Route.Main,
        typeMap = mapOf(typeOf<Uuid>() to UuidNavType)
    ) {
        mainDestinations(navController)

        newEntityDestinations(navController)

        entityInfoDestinations(navController)

        characterFullDestinations(navController)
    }
}


private fun NavGraphBuilder.mainDestinations(navController: NavHostController) =
    navigation<Route.Main>(
        startDestination = Route.Main.CharactersList
    ) {
        composable<Route.Main.CharactersList> {
            DefaultNavigationWrapper(navController) {
                CharactersListScreen(
                    onNewCharacter = { navController.navigate(Route.New) },
                    navigateToCharacter = { navController.navigate(Route.CharacterFull(it.id)) }
                )
            }
        }
        composable<Route.Main.Items> {
            DefaultNavigationWrapper(navController) {

            }
        }
        composable<Route.Main.Browse> {
            DefaultNavigationWrapper(navController) {

            }
        }
        composable<Route.Main.Profile> {
            DefaultNavigationWrapper(navController) {

            }
        }
    }

private fun NavGraphBuilder.newEntityDestinations(navController: NavHostController) {
    composable<Route.New> {
        NewEntityScreen(
            onNavigateBack = navController::navigateUp,
            onNavigate = { route -> navController.navigate(route) }
        )
    }
    composable<Route.New.Item> {
        NewItemScreen()
    }
    characterCreationFlow(navController)
}


private fun NavGraphBuilder.entityInfoDestinations(navController: NavHostController) {
    composable<Route.EntityInfo>(
        typeMap = mapOf(typeOf<Uuid>() to UuidNavType)
    ) { backStack ->
        val route: Route.EntityInfo = backStack.toRoute()
        DnDEntityInfo(
            route.entityId,
            navigateBack = navController::navigateUp
        )
    }
}

private fun NavGraphBuilder.characterCreationFlow(navController: NavHostController) =
    navigation<Route.New.Character>(startDestination = Route.New.Character.LoadData) {
        @Composable
        fun NavBackStackEntry.getSharedViewModel(): NewCharacterViewModel = 
            sharedKoinViewModel<NewCharacterViewModel, Route.New.Character>(navController)
        
        composable<Route.New.Character.LoadData> {
            LoadingDataScreen(
                onBack = navController::navigateUp,
                onContinue = {
                    navController.navigateWithRemoveFromBackStack(Route.New.Character.Main)
                }
            )
        }
        composable<Route.New.Character.Main> { backStack ->
            val newCharacterViewModel = backStack.getSharedViewModel()
            NewCharacterMainScreen(
                navigateToEntityInfo = { id -> navController.navigate(Route.EntityInfo(id)) },
                onBack = navController::navigateUp,
                onContinue = { navController.navigate(Route.New.Character.Stats) },
                viewModel = koinInject { parametersOf(newCharacterViewModel) }
            )
        }
        composable<Route.New.Character.Stats> { backStack ->
            val newCharacterViewModel = backStack.getSharedViewModel()
            NewCharacterAttributesScreen(
                onBack = navController::navigateUp,
                onContinue = { navController.navigate(Route.New.Character.Throws) },
                viewModel = koinInject { parametersOf(newCharacterViewModel) }
            )
        }
        composable<Route.New.Character.Throws> { backStack ->
            val newCharacterViewModel = backStack.getSharedViewModel()
            NewCharacterStatsLargeScreen(
                onBack = navController::navigateUp,
                onContinue = { navController.navigate(Route.New.Character.Health) },
                viewModel = koinInject { parametersOf(newCharacterViewModel) }
            )
        }
        composable<Route.New.Character.Health> { backStack ->
            val newCharacterViewModel = backStack.getSharedViewModel()
            NewCharacterHealthScreen(
                onBack = navController::navigateUp,
                onContinue = { navController.navigate(Route.New.Character.Save) },
                viewModel = koinInject { parametersOf(newCharacterViewModel) }
            )
        }
        composable<Route.New.Character.Save> { backStack ->
            val newCharacterViewModel = backStack.getSharedViewModel()
            SavingNewCharacterScreen(
                onBack = { navController.navigateWithRemoveFromBackStack(Route.Main.CharactersList) },
                onGoToCharacter = { navController.navigateWithRemoveFromBackStack(Route.CharacterFull(it)) },
                viewModel = koinInject { parametersOf(newCharacterViewModel) }
            )
        }
    }


private fun NavGraphBuilder.characterFullDestinations(navController: NavHostController) =
    navigation<Route.CharacterFull>(
        startDestination =  Route.CharacterFull.Main,
        typeMap = mapOf(typeOf<Uuid>() to UuidNavType)
    ) {
        @Composable
        fun NavBackStackEntry.getSharedViewModel(): CharacterFullViewModel =
            sharedKoinViewModel<CharacterFullViewModel, Route.CharacterFull>(navController) {
                val route: Route.CharacterFull = navController
                    .getBackStackEntry<Route.CharacterFull>()
                    .toRoute()
                parametersOf(route.characterId)
            }

        composable<Route.CharacterFull.Main> { backStackEntry ->
            val viewModel = backStackEntry.getSharedViewModel()
            CharacterFullScreen(
                navigateBack = navController::navigateUp,
                viewModel = viewModel
            )
        }
    }

private fun <T : Any> NavHostController.navigateWithRemoveFromBackStack(
    destinationRoute: T
) = navigate(destinationRoute) {
    currentDestination?.id?.let {
        popUpTo(it) { inclusive = true }
    }
}

@Composable
inline fun <reified VM : ViewModel, reified R : Any> NavBackStackEntry.sharedKoinViewModel(
    navController: NavController,
    noinline parameters: ParametersDefinition? = null,
): VM {
    val parentEntry = remember(this) {
        navController.getBackStackEntry<R>()
    }
    @Suppress("UndeclaredKoinUsage")
    return koinViewModel(
        viewModelStoreOwner = parentEntry,
        parameters = parameters
    )
}