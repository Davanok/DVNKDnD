@file:OptIn(ExperimentalUuidApi::class)

package com.davanok.dvnkdnd.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.bundle.Bundle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.davanok.dvnkdnd.ui.pages.characterFull.CharacterFullScreen
import com.davanok.dvnkdnd.ui.pages.charactersList.CharactersListScreen
import com.davanok.dvnkdnd.ui.pages.dndEntityInfo.DnDEntityInfo
import com.davanok.dvnkdnd.ui.pages.newEntity.NewEntityScreen
import com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.newCharacterMain.NewCharacterMainScreen
import com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.newCharacterStats.NewCharacterStatsScreen
import com.davanok.dvnkdnd.ui.pages.newEntity.newItem.NewItemScreen
import io.github.aakira.napier.Napier
import kotlin.reflect.typeOf
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid


object UuidNavType : NavType<Uuid>(true) {
    override fun get(bundle: Bundle, key: String): Uuid? {
        return bundle.getByteArray(key)?.let { Uuid.fromByteArray(it) }
    }

    override fun parseValue(value: String): Uuid {
        return Uuid.parse(value)
    }

    override fun put(
        bundle: Bundle,
        key: String,
        value: Uuid,
    ) {
        bundle.putByteArray(key, value.toByteArray())
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
    composable<Route.EntityInfo>(
        typeMap = mapOf(typeOf<Uuid>() to UuidNavType)
    ) { backStack ->
        val route: Route.EntityInfo = backStack.toRoute()
        DnDEntityInfo(
            route.entityId,
            navigateBack = { navController.navigateUp() }
        )
    }
}

private fun NavGraphBuilder.characterCreationFlow(navController: NavHostController) =
    navigation<Route.New.Character>(startDestination = Route.New.Character.Main) {
        composable<Route.New.Character.Main> {
            NewCharacterMainScreen(
                navigateToEntityInfo = { navController.navigate(Route.EntityInfo(it.id)) },
                onContinue = { id ->
                    Napier.d { "navigate" }
                    navController.navigate(Route.New.Character.Stats(id))
                }
            )
        }
        composable<Route.New.Character.Stats>(
            typeMap = mapOf(typeOf<Uuid>() to UuidNavType)
        )  { backStack ->
            val route: Route.New.Character.Stats = backStack.toRoute()
            NewCharacterStatsScreen(
                characterId = route.characterId,
                onBack = { navController.navigateUp() },
                onContinue = { id -> navController.navigate(Route.New.Character.Skills(id)) }
            )
        }
    }
