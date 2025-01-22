package com.davanok.dvnkdnd.ui.components.adaptive

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.FloatingActionButtonElevation
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailDefaults
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.PermanentDrawerSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteItemColors
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldLayout
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collection.mutableVectorOf
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.davanok.dvnkdnd.data.platform.BackHandler
import com.davanok.dvnkdnd.data.platform.calculateWindowSizeClass
import com.davanok.dvnkdnd.data.types.ui.WindowHeightSizeClass
import com.davanok.dvnkdnd.data.types.ui.WindowWidthSizeClass
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.app_name
import dvnkdnd.composeapp.generated.resources.close_drawer
import dvnkdnd.composeapp.generated.resources.menu_open
import dvnkdnd.composeapp.generated.resources.open_drawer
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import androidx.compose.runtime.getValue

@Composable
fun rememberStateOfFAB(
    content: AdaptiveFloatingActionButtonScope.() -> Unit
): State<AdaptiveFloatingActionButtonScope> {
    val latestContent = rememberUpdatedState(content)
    return remember {
        derivedStateOf { AdaptiveFloatingActionButtonScope().apply(latestContent.value) }
    }
}

data class AdaptiveFloatingActionButton(
    val onClick: () -> Unit,
    val modifier: Modifier = Modifier,
    val shape: Shape?,
    val containerColor: Color?,
    val contentColor: Color?,
    val elevation: FloatingActionButtonElevation?,
    val interactionSource: MutableInteractionSource? = null,
    val icon: @Composable () -> Unit,
    val text: @Composable RowScope.() -> Unit
)


class AdaptiveFloatingActionButtonScope {
    val items = mutableVectorOf<AdaptiveFloatingActionButton>()
    fun FloatingActionButton(
        onClick: () -> Unit,
        modifier: Modifier = Modifier,
        shape: Shape? = null,
        containerColor: Color? = null,
        contentColor: Color? = null,
        elevation: FloatingActionButtonElevation? = null,
        interactionSource: MutableInteractionSource? = null,
        icon: @Composable () -> Unit,
        text: @Composable RowScope.() -> Unit
    ) {
        items.add(
            AdaptiveFloatingActionButton(
                onClick,
                modifier,
                shape,
                containerColor,
                contentColor,
                elevation,
                interactionSource,
                icon,
                text
            )
        )
    }
}

@Composable
fun rememberStateOfItems(
    content: AdaptiveNavItemScope.() -> Unit
): State<AdaptiveNavItemScope> {
    val latestContent = rememberUpdatedState(content)
    return remember {
        derivedStateOf { AdaptiveNavItemScope().apply(latestContent.value) }
    }
}

data class AdaptiveNavItem(
    val selected: Boolean,
    val onClick: () -> Unit,
    val icon: @Composable () -> Unit,
    val modifier: Modifier = Modifier,
    val enabled: Boolean = true,
    val label: @Composable (() -> Unit),
    val alwaysShowLabel: Boolean = true,
    val badge: (@Composable () -> Unit)? = null,
    val colors: NavigationSuiteItemColors? = null,
    val interactionSource: MutableInteractionSource? = null
)


class AdaptiveNavItemScope() {
    val items = mutableVectorOf<AdaptiveNavItem>()
    fun NavItem(
        selected: Boolean,
        onClick: () -> Unit,
        icon: @Composable () -> Unit,
        modifier: Modifier = Modifier,
        enabled: Boolean = true,
        label: @Composable (() -> Unit),
        alwaysShowLabel: Boolean = true,
        badge: (@Composable () -> Unit)? = null,
        colors: NavigationSuiteItemColors? = null,
        interactionSource: MutableInteractionSource? = null
    ) {
        items.add(
            AdaptiveNavItem(
                selected,
                onClick,
                icon,
                modifier,
                enabled,
                label,
                alwaysShowLabel,
                badge,
                colors,
                interactionSource
            )
        )
    }
}


@Composable
fun AdaptiveNavigationWrapper(
    layoutType: NavigationSuiteType = calculateNavSuiteType(),
    navigationItems: AdaptiveNavItemScope.() -> Unit,
    floatingActionButton: (AdaptiveFloatingActionButtonScope.() -> Unit)? = null,
    modifier: Modifier = Modifier,
    containerColor: Color = NavigationSuiteScaffoldDefaults.containerColor,
    contentColor: Color = NavigationSuiteScaffoldDefaults.contentColor,
    content: @Composable () -> Unit
) {
    val windowSizeClass = calculateWindowSizeClass()
    val coroutineScope = rememberCoroutineScope()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val gesturesEnabled =
        drawerState.isOpen || layoutType == NavigationSuiteType.NavigationRail

    BackHandler(enabled = drawerState.isOpen) {
        coroutineScope.launch {
            drawerState.close()
        }
    }
    val scope = rememberStateOfItems(navigationItems)
    val fabScope = if (floatingActionButton == null) null else rememberStateOfFAB(floatingActionButton)

    Surface(modifier = modifier, color = containerColor, contentColor = contentColor) {
        ModalNavigationDrawer(
            modifier = modifier,
            drawerState = drawerState,
            gesturesEnabled = gesturesEnabled,
            drawerContent = {
                ModalNavigationDrawerContent(
                    scope = scope.value,
                    onDrawerClicked = {
                        coroutineScope.launch {
                            drawerState.close()
                        }
                    },
                    fabScope = fabScope?.value,
                )
            },
        ) {
            NavigationSuiteScaffoldLayout(
                layoutType = layoutType,
                navigationSuite = {
                    AdaptiveNavigationSuite(
                        layoutType,
                        scope.value,
                        onDrawerClicked = {
                            coroutineScope.launch {
                                drawerState.open()
                            }
                        },
                        fabScope = fabScope?.value
                    )
                }
            ) {
                val paddingValues by remember(windowSizeClass, layoutType) {
                    derivedStateOf {
                        val size = if (windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact || windowSizeClass.heightSizeClass == WindowHeightSizeClass.Compact) 16.dp
                        else 24.dp
                        if (layoutType == NavigationSuiteType.NavigationBar) PaddingValues(horizontal = size)
                        else PaddingValues(end = size)
                    }
                }
                Box(
                    Modifier
                        .consumeWindowInsets(
                            when (layoutType) {
                                NavigationSuiteType.NavigationBar ->
                                    NavigationBarDefaults.windowInsets
                                NavigationSuiteType.NavigationRail ->
                                    NavigationRailDefaults.windowInsets
                                NavigationSuiteType.NavigationDrawer ->
                                    DrawerDefaults.windowInsets
                                else -> WindowInsets(0, 0, 0, 0)
                            }
                        )
                        .padding(paddingValues)
                ) {
                    content()
                }
            }
        }
    }
}

@Composable
fun ModalNavigationDrawerContent(
    scope: AdaptiveNavItemScope,
    onDrawerClicked: () -> Unit,
    fabScope: AdaptiveFloatingActionButtonScope?,
    modifier: Modifier = Modifier,
) {
    ModalDrawerSheet(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(Res.string.app_name).uppercase(),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            IconButton(onClick = onDrawerClicked) {
                Icon(
                    painter = painterResource(Res.drawable.menu_open),
                    contentDescription = stringResource(Res.string.close_drawer)
                )
            }
        }

        fabScope?.let {
            Column(
                modifier = Modifier.padding(top = 8.dp, bottom = 40.dp)
            ) {
                it.items.forEach { fab ->
                    val containerColor = fab.containerColor?: FloatingActionButtonDefaults.containerColor
                    ExtendedFloatingActionButton(
                        modifier = fab.modifier,
                        onClick = fab.onClick,
                        shape = fab.shape?: FloatingActionButtonDefaults.shape,
                        containerColor = containerColor,
                        contentColor = fab.contentColor?: contentColorFor(containerColor),
                        elevation = fab.elevation?: FloatingActionButtonDefaults.elevation(),
                        interactionSource = fab.interactionSource
                    ) {
                        fab.icon()
                        fab.text(this)
                    }
                }
            }
        }

        scope.items.forEach { item ->
            NavigationDrawerItem(
                label = item.label,
                selected = item.selected,
                onClick = item.onClick,
                modifier = item.modifier,
                icon = item.icon,
                badge = item.badge,
                colors = item.colors?.navigationDrawerItemColors?: NavigationDrawerItemDefaults.colors(),
                interactionSource = item.interactionSource
            )
        }
    }
}

@Composable
private fun AdaptiveNavigationSuite(
    layoutType: NavigationSuiteType,
    itemsScope: AdaptiveNavItemScope,
    onDrawerClicked: () -> Unit,
    fabScope: AdaptiveFloatingActionButtonScope? = null,
    modifier: Modifier = Modifier
) {
    when (layoutType) {
        NavigationSuiteType.NavigationBar -> NavigationBar(
            scope = itemsScope,
            modifier = modifier
        )
        NavigationSuiteType.NavigationRail -> NavigationRail(
            scope = itemsScope,
            onDrawerClicked = onDrawerClicked,
            fabScope = fabScope,
            modifier = modifier
        )
        NavigationSuiteType.NavigationDrawer -> PermanentNavigationDrawer(
            scope = itemsScope,
            fabScope = fabScope,
            modifier = modifier
        )
    }
}

@Composable
private fun NavigationBar(
    scope: AdaptiveNavItemScope,
    modifier: Modifier = Modifier
){
    NavigationBar(
        modifier = modifier,
    ) {
        scope.items.forEach { item ->
            NavigationBarItem(
                label = item.label,
                selected = item.selected,
                onClick = item.onClick,
                modifier = item.modifier,
                icon = item.icon,
                interactionSource = item.interactionSource
            )
        }
    }
}

@Composable
private fun NavigationRail(
    scope: AdaptiveNavItemScope,
    onDrawerClicked: () -> Unit,
    fabScope: AdaptiveFloatingActionButtonScope? = null,
    modifier: Modifier = Modifier
){
    NavigationRail(
        modifier = modifier,
        header = {
            NavigationRailItem(
                selected = false,
                onClick = onDrawerClicked,
                icon = {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = stringResource(Res.string.open_drawer)
                    )
                }
            )
            fabScope?.let {
                Column(
                    modifier = Modifier.padding(top = 8.dp, bottom = 32.dp)
                ) {
                    it.items.forEach { fab ->
                        val containerColor = fab.containerColor?: FloatingActionButtonDefaults.containerColor
                        FloatingActionButton(
                            content = fab.icon,
                            onClick = fab.onClick,
                            shape = fab.shape?: FloatingActionButtonDefaults.shape,
                            containerColor = containerColor,
                            contentColor = fab.contentColor?: contentColorFor(containerColor),
                            elevation = fab.elevation?: FloatingActionButtonDefaults.elevation(),
                            interactionSource = fab.interactionSource
                        )
                    }
                }
            }
        }
    ) {
        scope.items.forEach { item ->
            NavigationRailItem(
                label = item.label,
                selected = item.selected,
                onClick = item.onClick,
                modifier = item.modifier,
                icon = item.icon,
                interactionSource = item.interactionSource
            )
        }
    }
}

@Composable
fun PermanentNavigationDrawer(
    scope: AdaptiveNavItemScope,
    fabScope: AdaptiveFloatingActionButtonScope? = null,
    modifier: Modifier = Modifier,
) {
    PermanentDrawerSheet(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(Res.string.app_name).uppercase(),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }

        fabScope?.let {
            Column(
                modifier = Modifier.padding(top = 8.dp, bottom = 40.dp)
            ) {
                it.items.forEach { fab ->
                    val containerColor = fab.containerColor?: FloatingActionButtonDefaults.containerColor
                    ExtendedFloatingActionButton(
                        modifier = fab.modifier,
                        onClick = fab.onClick,
                        shape = fab.shape?: FloatingActionButtonDefaults.shape,
                        containerColor = containerColor,
                        contentColor = fab.contentColor?: contentColorFor(containerColor),
                        elevation = fab.elevation?: FloatingActionButtonDefaults.elevation(),
                        interactionSource = fab.interactionSource
                    ) {
                        fab.icon()
                        fab.text(this)
                    }
                }
            }
        }

        scope.items.forEach { item ->
            NavigationDrawerItem(
                label = item.label,
                selected = item.selected,
                onClick = item.onClick,
                modifier = item.modifier,
                icon = item.icon,
                badge = item.badge,
                colors = item.colors?.navigationDrawerItemColors?: NavigationDrawerItemDefaults.colors(),
                interactionSource = item.interactionSource
            )
        }
    }
}