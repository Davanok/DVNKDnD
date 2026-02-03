package com.davanok.dvnkdnd.ui.navigation.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.davanok.dvnkdnd.ui.components.adaptive.AdaptiveNavigationWrapper
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


@Composable
fun DefaultNavigationWrapper(
    currentRoute: Route,
    navigate: (Route) -> Unit,
    content: @Composable () -> Unit
) {
    AdaptiveNavigationWrapper(
        modifier = Modifier
            .fillMaxSize(),
        navigationItems = {
            TopLevelRoute.entries.forEach {
                item(
                    selected = it.route == currentRoute,
                    onClick = { navigate(it.route) },
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
                onClick = { navigate(Route.New) },
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
        },
        content = content
    )
}
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