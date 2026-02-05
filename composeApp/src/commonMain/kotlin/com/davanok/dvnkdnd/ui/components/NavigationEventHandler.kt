package com.davanok.dvnkdnd.ui.components

import androidx.compose.runtime.Composable
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.rememberNavigationEventState

@Composable
fun NavigationEventHandler(
    enabled: Boolean = true,
    onBackCancelled: () -> Unit = {},
    onBackCompleted: () -> Unit
) {
    val navEventState = rememberNavigationEventState(
        currentInfo = NavigationEventInfo.None
    )
    NavigationBackHandler(
        state = navEventState,
        isBackEnabled = enabled,
        onBackCancelled = onBackCancelled,
        onBackCompleted = onBackCompleted
    )
}