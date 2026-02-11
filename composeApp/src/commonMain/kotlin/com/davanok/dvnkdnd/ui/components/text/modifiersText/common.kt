package com.davanok.dvnkdnd.ui.components.text.modifiersText

import androidx.compose.runtime.Composable
import com.davanok.dvnkdnd.domain.entities.dndModifiers.DnDDamageModifier
import com.davanok.dvnkdnd.domain.entities.dndModifiers.DnDModifier
import com.davanok.dvnkdnd.domain.entities.dndModifiers.DnDRollModifier
import com.davanok.dvnkdnd.domain.entities.dndModifiers.DnDValueModifier

@Composable
fun DnDModifier.buildModifierPreview() = when (this) {
    is DnDDamageModifier -> buildPreview()
    is DnDRollModifier -> buildPreview()
    is DnDValueModifier -> buildPreview()
}

@Composable
fun DnDModifier.buildModifierPreviewWithTarget() = when (this) {
    is DnDDamageModifier -> buildPreviewWithTarget()
    is DnDRollModifier -> buildPreviewWithTarget()
    is DnDValueModifier -> buildPreviewWithTarget()
}