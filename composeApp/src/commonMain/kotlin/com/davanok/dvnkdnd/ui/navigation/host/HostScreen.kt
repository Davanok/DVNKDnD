package com.davanok.dvnkdnd.ui.navigation.host

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.davanok.dvnkdnd.ui.navigation.NavigationHost
import com.davanok.dvnkdnd.ui.navigation.Route
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.items
import dvnkdnd.composeapp.generated.resources.browse
import dvnkdnd.composeapp.generated.resources.home
import dvnkdnd.composeapp.generated.resources.category
import dvnkdnd.composeapp.generated.resources.manage_search
import dvnkdnd.composeapp.generated.resources.person
import dvnkdnd.composeapp.generated.resources.profile
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject


private enum class TopLevelRoute(
    val title: StringResource,
    val icon: DrawableResource,
    val route: Route
) {
    CharactersList(Res.string.home, Res.drawable.home, Route.Main.CharactersList),
    Items(Res.string.items, Res.drawable.category, Route.Main.Items),
    Browse(Res.string.browse, Res.drawable.manage_search, Route.Main.Browse),
    Profile(Res.string.profile, Res.drawable.person, Route.Main.Profile)
}


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
                item(
                    selected = navBackStackEntry?.destination?.hasRoute(it.route::class) == true,
                    icon = {
                        Icon(
                            painter = painterResource(it.icon),
                            contentDescription = stringResource(it.title)
                        )
                    },
                    label = { Text(text = stringResource(it.title)) },
                    alwaysShowLabel = false,
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