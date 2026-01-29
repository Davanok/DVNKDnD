package com.davanok.dvnkdnd.domain.entities.dndModifiers

import androidx.compose.runtime.Immutable
import com.davanok.dvnkdnd.core.utils.applyOperation
import com.davanok.dvnkdnd.core.utils.enumValueOfOrNull
import com.davanok.dvnkdnd.domain.enums.dndEnums.ValueOperation
import com.davanok.dvnkdnd.ui.model.UiSelectableState
import kotlin.uuid.Uuid

data class ModifiersGroupInfo(
    val id: Uuid,
    val name: String,
    val description: String?,
    val selectionLimit: Int
)

@Immutable
data class ValueModifierInfo(
    val modifier: DnDValueModifier,
    val group: ModifiersGroupInfo,
    val resolvedValue: Int,
    val state: UiSelectableState
) {
    inline fun <reified E: Enum<E>>targetAs(): E? = modifier.targetKey?.let { enumValueOfOrNull<E>(it) }

    fun applyForValue(baseValue: Int): Int =
        applyOperation(baseValue, resolvedValue, modifier.operation)

    fun haveEffect(): Boolean =
        !(modifier.operation == ValueOperation.ADD && resolvedValue == 0)
}