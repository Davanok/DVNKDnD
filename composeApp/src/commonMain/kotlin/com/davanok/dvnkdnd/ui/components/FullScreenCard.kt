package com.davanok.dvnkdnd.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
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
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = Modifier.fillMaxSize()
    ) {
        Spacer(Modifier.weight(1/6f))
        Card(
            modifier = Modifier
                .weight(2/3f)
                .align(Alignment.CenterVertically)
                .aspectRatio(0.75f)
                .then(modifier)
        ) {
            Column(
                modifier = Modifier.padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                heroIcon?.let {
                    it()
                    Spacer(Modifier.height(16.dp))
                }
                content()
                supportContent?.let {
                    Spacer(Modifier.height(16.dp))
                    it()
                }
            }
        }
        Spacer(Modifier.weight(1/6f))
    }
}