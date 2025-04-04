package com.davanok.dvnkdnd.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.davanok.dvnkdnd.data.model.dnd_enums.DnDEntityTypes
import com.davanok.dvnkdnd.ui.pages.characterFull.CharacterFullScreen
import com.davanok.dvnkdnd.ui.pages.charactersList.CharactersListScreen
import com.davanok.dvnkdnd.ui.pages.dndEntityInfo.DnDEntityInfo
import com.davanok.dvnkdnd.ui.pages.newEntity.NewEntityScreen
import com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.newCharacterMain.NewCharacterMainScreen
import com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.newCharacterStats.NewCharacterStatsScreen
import com.davanok.dvnkdnd.ui.pages.newEntity.newItem.NewItemScreen


@Composable
fun NavigationHost(
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = Route.Main
    ) {
        mainDestinations(navController)

        newEntityDestinations(navController)

        entityInfoDestinations(navController)

        composable<Route.CharacterFull> { backStackEntry ->
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
            CharactersListScreen(
                onFABClick = { navController.navigate(Route.New) },
                navigateToCharacter = { navController.navigate(Route.CharacterFull(it.id)) }
            )
        }
        composable<Route.Main.Items> {

        }
        composable<Route.Main.Browse> {

        }
        composable<Route.Main.Profile> {

        }
    }

private fun NavGraphBuilder.newEntityDestinations(navController: NavHostController) {
    composable<Route.New> {
        NewEntityScreen(
            onNavigate = { route -> navController.navigate(route) }
        )
    }
    composable<Route.New.Item> {
        NewItemScreen()
    }
    characterCreationFlow(navController)
}


private fun NavGraphBuilder.entityInfoDestinations(navController: NavHostController) {
    composable<Route.EntityInfo> { backStack ->
        val info: Route.EntityInfo = backStack.toRoute()
        DnDEntityInfo(
            DnDEntityTypes.valueOf(info.entityType),
            info.entityId,
            navigateBack = { navController.navigateUp() }
        )
    }
}

private fun NavGraphBuilder.characterCreationFlow(navController: NavHostController) =
    navigation<Route.New.Character>(startDestination = Route.New.Character.Main) {
        composable<Route.New.Character.Main> {
            NewCharacterMainScreen(
                navigateToEntityInfo = { type, entity ->
                    navController.navigate(Route.EntityInfo(type.name, entity.id))
                },
                onContinue = { id -> navController.navigate(Route.New.Character.Stats(id)) }
            )
        }
        composable<Route.New.Character.Stats> {
            NewCharacterStatsScreen(

            )
        }
    }
