package com.davanok.dvnkdnd.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.davanok.dvnkdnd.ui.components.adaptive.AdaptiveNavigationWrapper
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
fun HostScreen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    AdaptiveNavigationWrapper(
        modifier = Modifier
            .fillMaxSize(),
        navigationItems = {
            TopLevelRoute.entries.forEach {
                item(
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
        },
        floatingActionButton = {
            floatingActionButton(
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