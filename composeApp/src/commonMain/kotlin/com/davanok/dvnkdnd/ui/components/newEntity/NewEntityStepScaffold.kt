package com.davanok.dvnkdnd.ui.components.newEntity

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import com.davanok.dvnkdnd.ui.components.customToolBar.CollapsingTitle
import com.davanok.dvnkdnd.ui.components.customToolBar.CustomTopAppBar
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.back
import dvnkdnd.composeapp.generated.resources.cancel
import dvnkdnd.composeapp.generated.resources.next
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource


enum class BackClickAction(val icon: ImageVector, val stringRes: StringResource) {
    Cancel(Icons.Default.Close, Res.string.cancel),
    Back(Icons.AutoMirrored.Default.ArrowBack, Res.string.back)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewEntityStepScaffold(
    title: String,
    additionalContent: @Composable (() -> Unit)? = null,
    onNextClick: () -> Unit,
    nextClickEnabled: Boolean = true,
    onBackClick: () -> Unit,
    backClickAction: BackClickAction = BackClickAction.Back,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val topAppBarExpanded by remember(scrollBehavior.state.collapsedFraction) {
        derivedStateOf { scrollBehavior.state.collapsedFraction < 0.5 }
    }
    Scaffold(
        modifier = modifier.then(
            Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
        ),
        topBar = {
            CustomTopAppBar(
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick
                    ) {
                        Icon(
                            imageVector = backClickAction.icon,
                            contentDescription = stringResource(backClickAction.stringRes)
                        )
                    }
                },
                collapsingTitle = CollapsingTitle.medium(title),
                additionalContent = additionalContent,
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = topAppBarExpanded && nextClickEnabled,
                enter = slideInVertically { it * 2 },
                exit = slideOutVertically { it * 2 }
            ) {
                FloatingActionButton(
                    onClick = onNextClick
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = stringResource(Res.string.next)
                    )
                }
            }
        }
    ) {
        Box(Modifier.padding(it)) {
            content()
        }
    }
}