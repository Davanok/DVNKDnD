package com.davanok.dvnkdnd.ui.components

import androidx.compose.runtime.Composable
import com.davanok.dvnkdnd.data.model.dndEnums.DnDModifierOperation
import com.davanok.dvnkdnd.data.model.dndEnums.DnDModifierValueSource
import com.davanok.dvnkdnd.data.model.dndEnums.applyForStringPreview
import com.davanok.dvnkdnd.data.model.entities.dndModifiers.DnDModifiersGroup
import com.davanok.dvnkdnd.data.model.types.ModifierExtendedInfo
import org.jetbrains.compose.resources.stringResource


@Composable
private fun buildPreviewString(
    operation: DnDModifierOperation,
    valueSource: DnDModifierValueSource,
    value: Double
): String {
    val valueString =
        if (valueSource == DnDModifierValueSource.CONSTANT) {
            val unaryOps = setOf(
                DnDModifierOperation.ABS,
                DnDModifierOperation.ROUND,
                DnDModifierOperation.CEIL,
                DnDModifierOperation.FLOOR,
                DnDModifierOperation.FACT
            )
            when {
                operation in unaryOps && value == 0.0 -> null
                value.toInt().toDouble() == value -> value.toInt().toString()
                else -> value.toString()
            }
        } else
            stringResource(valueSource.stringRes)

    return operation.applyForStringPreview(valueString)
}


@Composable
fun ModifierExtendedInfo.buildPreviewString() = buildPreviewString(
    operation, valueSource, value
)
@Composable
fun DnDModifiersGroup.buildPreviewString() = buildPreviewString(
    operation, valueSource, value
)