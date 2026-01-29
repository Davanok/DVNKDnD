package com.davanok.dvnkdnd.domain.entities.character

import androidx.compose.runtime.Immutable
import com.davanok.dvnkdnd.domain.entities.dndModifiers.AttributesGroup
import com.davanok.dvnkdnd.domain.entities.dndModifiers.DnDValueModifier
import kotlin.uuid.Uuid


data class DnDValueModifierWithResolvedValue(
    val modifier: DnDValueModifier,
    val resolvedValue: Int,
)

data class ValueModifiersGroupWithResolvedValues(
    val id: Uuid,
    val name: String,
    val description: String?,
    val selectionLimit: Int,

    val modifiers: List<DnDValueModifierWithResolvedValue>
)

@Immutable
data class CharacterWithAllModifiers(
    val character: CharacterBase,
    val attributes: AttributesGroup,

    val selectedModifiers: Set<Uuid>,

    val modifierGroups: List<ValueModifiersGroupWithResolvedValues>
)