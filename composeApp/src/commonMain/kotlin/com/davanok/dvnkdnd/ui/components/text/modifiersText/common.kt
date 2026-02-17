package com.davanok.dvnkdnd.ui.components.text.modifiersText

import androidx.compose.runtime.Composable
import com.davanok.dvnkdnd.domain.entities.character.CharacterCustomDamageModifier
import com.davanok.dvnkdnd.domain.entities.character.CharacterCustomModifier
import com.davanok.dvnkdnd.domain.entities.character.CharacterCustomRollModifier
import com.davanok.dvnkdnd.domain.entities.character.CharacterCustomValueModifier
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

@Composable
fun CharacterCustomModifier.buildModifierPreview() = when (this) {
    is CharacterCustomDamageModifier -> buildPreview()
    is CharacterCustomRollModifier -> buildPreview()
    is CharacterCustomValueModifier -> buildPreview()
}
@Composable
fun CharacterCustomModifier.buildModifierPreviewWithTarget() = when (this) {
    is CharacterCustomDamageModifier -> buildPreviewWithTarget()
    is CharacterCustomRollModifier -> buildPreviewWithTarget()
    is CharacterCustomValueModifier -> buildPreviewWithTarget()
}