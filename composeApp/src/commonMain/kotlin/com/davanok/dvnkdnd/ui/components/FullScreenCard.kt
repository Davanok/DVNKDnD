package com.davanok.dvnkdnd.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun FullScreenCard(
    heroIcon: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit,
    supportContent: (@Composable () -> Unit)? = null,
    navButtons: (@Composable RowScope.() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .then(modifier),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxSize(0.66f)
                .padding(16.dp)
        ) {
            Column (
                modifier = Modifier.padding(24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    heroIcon?.invoke()

                    content()

                    supportContent?.invoke()
                }
                navButtons?.let {
                    Row(
                        modifier = Modifier
                            .align(Alignment.End),
                        horizontalArrangement = Arrangement.spacedBy(24.dp),
                        content = it
                    )
                }
            }
        }
    }
}