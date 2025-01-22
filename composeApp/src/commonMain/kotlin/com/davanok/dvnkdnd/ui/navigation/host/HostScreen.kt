package com.davanok.dvnkdnd.ui.navigation.host

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.davanok.dvnkdnd.ui.components.adaptive.AdaptiveNavigationWrapper
import com.davanok.dvnkdnd.ui.components.adaptive.calculateNavSuiteType
import com.davanok.dvnkdnd.ui.navigation.NavigationHost
import com.davanok.dvnkdnd.ui.navigation.Route
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.browse
import dvnkdnd.composeapp.generated.resources.category
import dvnkdnd.composeapp.generated.resources.draw
import dvnkdnd.composeapp.generated.resources.home
import dvnkdnd.composeapp.generated.resources.items
import dvnkdnd.composeapp.generated.resources.manage_search
import dvnkdnd.composeapp.generated.resources.new
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
    val navType = calculateNavSuiteType()
    AdaptiveNavigationWrapper(
        modifier = Modifier.fillMaxSize(),
        navigationItems = {
            val navItem = { it: TopLevelRoute ->
                NavItem(
                    selected = navBackStackEntry?.destination?.hasRoute(it.route::class) == true,
                    onClick = {
                        navController.navigate(it.route)
                    },
                    icon = {
                        Icon(
                            painter = painterResource(it.icon),
                            contentDescription = stringResource(it.title)
                        )
                    },
                    label = { Text(text = stringResource(it.title)) }
                )
            }
            TopLevelRoute.entries.take(TopLevelRoute.entries.size / 2).forEach { navItem(it) }
            if (navType == NavigationSuiteType.NavigationBar)
                NavItem(
                    selected = navBackStackEntry?.destination?.hasRoute(Route.New::class) == true,
                    onClick = { navController.navigate(Route.New) },
                    icon = {
                        Icon(
                            painter = painterResource(Res.drawable.draw),
                            contentDescription = stringResource(Res.string.new)
                        )
                    },
                    label = { Text(text = stringResource(Res.string.new)) }
                )
            TopLevelRoute.entries.takeLast(TopLevelRoute.entries.size / 2).forEach { navItem(it) }
        },
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = { navController.navigate(Route.New) },
                icon = {
                    Icon(
                        painter = painterResource(Res.drawable.draw),
                        contentDescription = stringResource(Res.string.new)
                    )
                },
                text = {
                    Text(
                        text = stringResource(Res.string.new),
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                }
            )
        }
    ) {
        NavigationHost(
            modifier = Modifier.fillMaxSize(),
            navController = navController
        )
    }
}