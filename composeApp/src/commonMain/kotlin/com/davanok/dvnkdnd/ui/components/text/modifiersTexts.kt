package com.davanok.dvnkdnd.ui.components.text

import androidx.compose.runtime.Composable
import com.davanok.dvnkdnd.domain.enums.dndEnums.DnDModifierOperation
import com.davanok.dvnkdnd.domain.enums.dndEnums.DnDModifierValueSource
import com.davanok.dvnkdnd.domain.entities.dndModifiers.ModifiersGroup
import com.davanok.dvnkdnd.domain.entities.dndModifiers.ModifierExtendedInfo
import com.davanok.dvnkdnd.ui.presentation.applyForStringPreview
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
fun ModifiersGroup.buildPreviewString() = buildPreviewString(
    operation, valueSource, value
)