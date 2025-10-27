package com.davanok.dvnkdnd.ui.pages.newEntity.newItem

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.davanok.dvnkdnd.ui.components.adaptive.AdaptiveWidth
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewItemScreen() {
    val pane: @Composable () -> Unit = {
        Box(Modifier.fillMaxSize().border(2.dp, Color(Random.nextLong())))
    }
    AdaptiveWidth(
        singlePaneContent = pane,
        twoPaneContent = pane to pane,
        threePaneContent = Triple(pane, pane, pane)
    )
}