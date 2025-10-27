package com.davanok.dvnkdnd.data.model.types

import com.davanok.dvnkdnd.data.model.dndEnums.DnDModifierOperation
import com.davanok.dvnkdnd.data.model.dndEnums.DnDModifierValueSource
import com.davanok.dvnkdnd.data.model.entities.dndModifiers.DnDModifier
import com.davanok.dvnkdnd.data.model.ui.UiSelectableState
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