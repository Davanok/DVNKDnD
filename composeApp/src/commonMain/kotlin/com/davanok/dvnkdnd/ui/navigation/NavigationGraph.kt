package com.davanok.dvnkdnd.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
import com.davanok.dvnkdnd.ui.pages.charactersList.CharactersListScreen
import com.davanok.dvnkdnd.ui.pages.dndEntityInfo.DnDEntityInfo
import com.davanok.dvnkdnd.ui.pages.newEntity.NewEntityScreen
import com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.loadingScreen.LoadingDataScreen
import com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.newCharacterMain.NewCharacterMainScreen
import com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.newCharacterSkills.NewCharacterSkillsScreen
import com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.newCharacterStats.NewCharacterStatsScreen
import com.davanok.dvnkdnd.ui.pages.newEntity.newItem.NewItemScreen
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
        startDestination = Route.New.Character,
        typeMap = mapOf(typeOf<Uuid>() to UuidNavType)
    ) {
        mainDestinations(navController)

        newEntityDestinations(navController)

        entityInfoDestinations(navController)

        composable<Route.CharacterFull>(
            typeMap = mapOf(typeOf<Uuid>() to UuidNavType)
        )  { backStackEntry ->
            val route: Route.CharacterFull = backStackEntry.toRoute()
            CharacterFullScreen(route.characterId)
        }
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
        composable<Route.New.Character.LoadData> {
            LoadingDataScreen(
                onBack = navController::navigateUp,
                onContinue = {
                    navController.navigate(Route.New.Character.Main) {
                        popUpTo(Route.New.Character.LoadData) {
                            inclusive = true
                        }
                    }
                }
            )
        }
        composable<Route.New.Character.Main> {
            NewCharacterMainScreen(
                navigateToEntityInfo = { navController.navigate(Route.EntityInfo(it.id)) },
                onBack = navController::navigateUp,
                onContinue = { id -> navController.navigate(Route.New.Character.Stats(id)) }
            )
        }
        composable<Route.New.Character.Stats>(
            typeMap = mapOf(typeOf<Uuid>() to UuidNavType)
        ) { backStack ->
            val route: Route.New.Character.Stats = backStack.toRoute()
            NewCharacterStatsScreen(
                characterId = route.characterId,
                onBack = { navController.navigateUp() },
                onContinue = { id -> navController.navigate(Route.New.Character.Skills(id)) }
            )
        }
        composable<Route.New.Character.Skills>(
            typeMap = mapOf(typeOf<Uuid>() to UuidNavType)
        ) { backStack ->
            val route: Route.New.Character.Skills = backStack.toRoute()
            NewCharacterSkillsScreen(
                characterId = route.characterId,
                onBack = { navController.navigateUp() },
                onContinue = { id -> navController.navigate(Route.New.Character.Health(id)) }
            )
        }
    }
