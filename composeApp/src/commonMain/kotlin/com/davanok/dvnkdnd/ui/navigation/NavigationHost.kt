package com.davanok.dvnkdnd.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.davanok.dvnkdnd.ui.pages.home.CharactersListScreen

@Composable
fun NavigationHost(
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = Route.Home
    ) {
        composable<Route.Home> { CharactersListScreen() }
    }
}