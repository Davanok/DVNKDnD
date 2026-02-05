package com.davanok.dvnkdnd.ui.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButtonMenu
import androidx.compose.material3.FloatingActionButtonMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleFloatingActionButton
import androidx.compose.material3.ToggleFloatingActionButtonScope
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.animateFloatingActionButton
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collection.mutableVectorOf
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.semantics.traversalIndex
import com.davanok.dvnkdnd.domain.enums.dndEnums.DnDEntityTypes
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.floating_action_menu_close_menu
import dvnkdnd.composeapp.generated.resources.floating_action_menu_collapsed
import dvnkdnd.composeapp.generated.resources.floating_action_menu_expanded
import dvnkdnd.composeapp.generated.resources.floating_action_menu_toggle_menu
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun DefaultFloatingActionMenu(
    fabVisible: Boolean,
    fabIcon: @Composable ToggleFloatingActionButtonScope.() -> Unit,
    content: DefaultFloatingActionMenuScope.() -> Unit
) {
    var fabMenuExpanded by rememberSaveable { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    val scope by rememberFloatingActionMenuScope(content)

    NavigationEventHandler(fabMenuExpanded) {
        fabMenuExpanded = false
    }

    val menuItems = listOf(
        DnDEntityTypes.CLASS,
        DnDEntityTypes.RACE,
        DnDEntityTypes.BACKGROUND
    )

    FloatingActionButtonMenu(
        expanded = fabMenuExpanded,
        button = {
            TooltipBox(
                positionProvider =
                    TooltipDefaults.rememberTooltipPositionProvider(
                        if (fabMenuExpanded) {
                            TooltipAnchorPosition.Start
                        } else {
                            TooltipAnchorPosition.Above
                        }
                    ),
                tooltip = {
                    PlainTooltip {
                        Text(text = stringResource(Res.string.floating_action_menu_toggle_menu))
                    }
                          },
                state = rememberTooltipState(),
            ) {
                val semanticsContentDescription = stringResource(Res.string.floating_action_menu_toggle_menu)
                val semanticsStateDescription =
                    if (fabMenuExpanded) stringResource(Res.string.floating_action_menu_expanded)
                    else stringResource(Res.string.floating_action_menu_collapsed)
                ToggleFloatingActionButton(
                    modifier =
                        Modifier
                            .semantics {
                                traversalIndex = -1f
                                stateDescription = semanticsStateDescription
                                contentDescription = semanticsContentDescription
                            }
                            .animateFloatingActionButton(
                                visible = fabVisible || fabMenuExpanded,
                                alignment = Alignment.BottomEnd,
                            )
                            .focusRequester(focusRequester),
                    checked = fabMenuExpanded,
                    onCheckedChange = { fabMenuExpanded = !fabMenuExpanded },
                    content = fabIcon
                )
            }
        }
    ) {
        val accessibilityActionCloseMenuLabel = stringResource(Res.string.floating_action_menu_close_menu)
        scope.items.forEachIndexed { index, item ->
            val containerColor = item.containerColor.takeOrElse { MaterialTheme.colorScheme.primaryContainer }
            val contentColor = item.contentColor.takeOrElse { contentColorFor(containerColor) }

            FloatingActionButtonMenuItem(
                onClick = {
                    fabMenuExpanded = false
                    item.onClick()
                },
                text = item.text,
                icon = item.icon,
                modifier = Modifier.semantics {
                    isTraversalGroup = true
                    // Add a custom a11y action to allow closing the menu when focusing
                    // the last menu item, since the close button comes before the first
                    // menu item in the traversal order.
                    if (index == menuItems.lastIndex) {
                        customActions =
                            listOf(
                                CustomAccessibilityAction(
                                    label = accessibilityActionCloseMenuLabel,
                                    action = {
                                        fabMenuExpanded = false
                                        true
                                    },
                                )
                            )
                    }
                }
                    .then(
                        if (index == 0) {
                            Modifier.onKeyEvent {
                                // Navigating back from the first item should go back to the
                                // FAB menu button.
                                if (
                                    it.type == KeyEventType.KeyDown &&
                                    (it.key == Key.DirectionUp ||
                                            (it.isShiftPressed && it.key == Key.Tab))
                                ) {
                                    focusRequester.requestFocus()
                                    return@onKeyEvent true
                                }
                                return@onKeyEvent false
                            }
                        } else {
                            Modifier
                        }
                    )
                    .then(item.modifier),
                containerColor = containerColor,
                contentColor = contentColor
            )
        }
    }
}

data class DefaultFloatingActionMenuItem(
    val onClick: () -> Unit,
    val text: @Composable () -> Unit,
    val icon: @Composable () -> Unit,
    val modifier: Modifier,
    val containerColor: Color,
    val contentColor: Color
)

interface DefaultFloatingActionMenuScope {
    fun menuItem(
        onClick: () -> Unit,
        text: @Composable () -> Unit,
        icon: @Composable () -> Unit,
        modifier: Modifier = Modifier,
        containerColor: Color = Color.Unspecified,
        contentColor: Color = Color.Unspecified
    )
}

@Composable
private fun rememberFloatingActionMenuScope(
    content: DefaultFloatingActionMenuScope.() -> Unit
): State<DefaultFloatingActionMenuScopeImpl> {
    val latestContent = rememberUpdatedState(content)
    return remember {
        derivedStateOf {
            DefaultFloatingActionMenuScopeImpl().apply(latestContent.value)
        }
    }
}

class DefaultFloatingActionMenuScopeImpl : DefaultFloatingActionMenuScope {
    val items = mutableVectorOf<DefaultFloatingActionMenuItem>()

    override fun menuItem(
        onClick: () -> Unit,
        text: @Composable (() -> Unit),
        icon: @Composable (() -> Unit),
        modifier: Modifier,
        containerColor: Color,
        contentColor: Color
    ) {
        items.add(
            DefaultFloatingActionMenuItem(
                onClick = onClick,
                text = text,
                icon = icon,
                modifier = modifier,
                containerColor = containerColor,
                contentColor = contentColor
            )
        )
    }
}