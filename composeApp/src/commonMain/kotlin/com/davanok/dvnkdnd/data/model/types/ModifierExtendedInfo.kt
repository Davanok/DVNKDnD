package com.davanok.dvnkdnd.data.model.types

import androidx.compose.runtime.Composable
import com.davanok.dvnkdnd.data.model.dndEnums.DnDModifierOperation
import com.davanok.dvnkdnd.data.model.dndEnums.DnDModifierValueSource
import com.davanok.dvnkdnd.data.model.dndEnums.applyForStringPreview
import com.davanok.dvnkdnd.data.model.entities.dndModifiers.DnDModifier
import org.jetbrains.compose.resources.stringResource
import kotlin.uuid.Uuid

data class ModifierExtendedInfo(
    val groupId: Uuid,
    val groupName: String,
    val modifier: DnDModifier,
    val operation: DnDModifierOperation,
    val valueSource: DnDModifierValueSource,
    val state: UiSelectableState,
    val resolvedValue: Double,
)


@Composable
fun ModifierExtendedInfo.buildPreviewString(): String {
    val valueString =
        if (valueSource == DnDModifierValueSource.CONST) {
            val unaryOps = setOf(
                DnDModifierOperation.ABS,
                DnDModifierOperation.ROUND,
                DnDModifierOperation.CEIL,
                DnDModifierOperation.FLOOR,
                DnDModifierOperation.FACT
            )
            if (operation in unaryOps && modifier.value == 0.0) null
            else modifier.value.toString()
        } else
            stringResource(valueSource.stringRes)

    return operation.applyForStringPreview(valueString)
}