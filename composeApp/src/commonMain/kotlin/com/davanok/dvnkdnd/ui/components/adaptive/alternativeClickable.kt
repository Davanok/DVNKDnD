package com.davanok.dvnkdnd.ui.components.adaptive

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier

@Stable
expect fun Modifier.alternativeClickable(
    enabled: Boolean = true,
    onClick: () -> Unit,
    onAlternativeClick: () -> Unit,
    onOtherClick: (() -> Unit)? = null,
    interactionSource: MutableInteractionSource? = null
): Modifier