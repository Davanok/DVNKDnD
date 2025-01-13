package com.davanok.dvnkdnd.ui.navigation.host

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.davanok.dvnkdnd.ui.navigation.NavigationHost
import com.davanok.dvnkdnd.ui.navigation.Route
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.home
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import androidx.navigation.NavDestination.Companion.hasRoute


private enum class TopLevelRoute(
    val title: StringResource,
    val icon: ImageVector,
    val route: Route
) {
    CharactersList(Res.string.home, Icons.Default.Home, Route.Home)
}


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun HostScreen(
    viewModel: HostViewModel = koinInject()
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    NavigationSuiteScaffold(
        modifier = Modifier.fillMaxSize(),
        navigationSuiteItems = {
            TopLevelRoute.entries.forEach {
                item (
                    selected = navBackStackEntry?.destination?.hasRoute(it.route::class) == true,
                    icon = {
                        Icon(
                            imageVector = it.icon,
                            contentDescription = stringResource(it.title)
                        )
                           },
                    label = { Text(text = stringResource(it.title)) },
                    onClick = {
                        navController.navigate(it.route)
                    }
                )
            }
        }
    ) {
        NavigationHost(
            modifier = Modifier.fillMaxSize(),
            navController = navController
        )
    }
}