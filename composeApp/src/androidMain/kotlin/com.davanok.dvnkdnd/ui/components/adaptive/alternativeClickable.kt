package com.davanok.dvnkdnd.ui.components.adaptive

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier

@Stable
actual fun Modifier.alternativeClickable(
    enabled: Boolean,
    onClick: () -> Unit,
    onAlternativeClick: () -> Unit,
    onOtherClick: (() -> Unit)?,
    interactionSource: MutableInteractionSource?
): Modifier = combinedClickable(
    enabled = enabled,
    onClick = onClick,
    onLongClick = onAlternativeClick,
    onDoubleClick = onOtherClick,
    interactionSource = interactionSource
)