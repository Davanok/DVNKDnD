package com.davanok.dvnkdnd.domain.entities.dndModifiers

import com.davanok.dvnkdnd.domain.enums.dndEnums.DnDModifierOperation
import com.davanok.dvnkdnd.domain.enums.dndEnums.DnDModifierValueSource
import com.davanok.dvnkdnd.ui.model.UiSelectableState
import kotlin.uuid.Uuid

data class ModifierExtendedInfo(
    val groupId: Uuid,
    val groupName: String,
    val modifier: DnDModifier,
    val value: Double,
    val operation: DnDModifierOperation,
    val valueSource: DnDModifierValueSource,
    val state: UiSelectableState,
    val resolvedValue: Double,
)