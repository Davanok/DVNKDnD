package com.davanok.dvnkdnd.ui.components.adaptive

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.PointerMatcher
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.onClick
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.input.pointer.PointerButton

@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
@Stable
actual fun Modifier.alternativeClickable(
    enabled: Boolean,
    onClick: () -> Unit,
    onAlternativeClick: () -> Unit,
    onOtherClick: (() -> Unit)?,
    interactionSource: MutableInteractionSource?
): Modifier = composed {
    val currentInteractionSource = interactionSource ?: remember { MutableInteractionSource() }
    indication(currentInteractionSource, LocalIndication.current)
        .onClick(
            enabled = enabled,
            onClick = onClick,
            onLongClick = onAlternativeClick,
            onDoubleClick = onOtherClick,
            interactionSource = currentInteractionSource
        ).onClick(
            enabled = enabled,
            matcher = PointerMatcher.mouse(PointerButton.Secondary),
            onClick = onAlternativeClick,
            interactionSource = currentInteractionSource
        ).then(
            if (onOtherClick == null) Modifier else Modifier.onClick(
                enabled = enabled,
                matcher = PointerMatcher.mouse(PointerButton.Tertiary),
                onClick = onOtherClick,
                interactionSource = currentInteractionSource
            )
        )
}
