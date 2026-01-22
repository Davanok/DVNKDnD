package com.davanok.dvnkdnd.domain.entities.dndModifiers

import com.davanok.dvnkdnd.core.utils.applyOperation
import com.davanok.dvnkdnd.core.utils.enumValueOfOrNull
import com.davanok.dvnkdnd.domain.enums.dndEnums.DnDModifierOperation
import com.davanok.dvnkdnd.domain.enums.dndEnums.DnDModifierTargetType
import com.davanok.dvnkdnd.domain.enums.dndEnums.DnDModifierValueSource
import com.davanok.dvnkdnd.ui.model.UiSelectableState
import kotlin.uuid.Uuid

data class ModifierExtendedInfo(
    val id: Uuid,
    val isCustom: Boolean,
    val priority: Int,

    val groupId: Uuid?,
    val name: String,

    val targetGlobal: DnDModifierTargetType,
    val target: String?,
    val operation: DnDModifierOperation,
    val valueSource: DnDModifierValueSource,
    val valueSourceTarget: String?,
    val value: Double,

    val clampMin: Int? = null,
    val clampMax: Int? = null,
    val minBaseValue: Int? = null,
    val maxBaseValue: Int? = null,

    val state: UiSelectableState,

    val resolvedValue: Double,
) {
    inline fun <reified E: Enum<E>>targetAs(): E? = target?.let { enumValueOfOrNull<E>(it) }

    fun shouldSkip(currentValue: Int): Boolean =
        (minBaseValue != null && currentValue < minBaseValue) ||
                (maxBaseValue != null && currentValue > maxBaseValue)

    fun applyForValue(baseValue: Int): Int {
        if (shouldSkip(baseValue)) return baseValue

        val result = applyOperation(baseValue, resolvedValue, operation)

        return result.coerceIn(clampMin, clampMax)
    }
}