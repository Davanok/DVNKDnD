package com.davanok.dvnkdnd.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.davanok.dvnkdnd.ui.components.adaptive.LocalAdaptiveInfo
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.draw
import dvnkdnd.composeapp.generated.resources.new
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun FABScaffold(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val layoutInfo = LocalAdaptiveInfo.current
    val showFAB = layoutInfo.layoutType == NavigationSuiteType.NavigationBar

    if (showFAB)
        Scaffold(
            modifier = modifier,
            floatingActionButton = {
                FloatingActionButton(
                    onClick = onClick
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.draw),
                        contentDescription = stringResource(Res.string.new)
                    )
                }
            },
            content = { Box(modifier = Modifier.padding(it)) { content() } }
        )
    else
        Box(modifier = modifier) { content() }
}