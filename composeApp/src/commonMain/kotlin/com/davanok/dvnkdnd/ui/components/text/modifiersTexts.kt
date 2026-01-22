package com.davanok.dvnkdnd.ui.components.text

import androidx.compose.runtime.Composable
import com.davanok.dvnkdnd.domain.enums.dndEnums.DnDModifierOperation
import com.davanok.dvnkdnd.domain.enums.dndEnums.DnDModifierValueSource
import com.davanok.dvnkdnd.domain.entities.dndModifiers.ModifiersGroup
import com.davanok.dvnkdnd.domain.entities.dndModifiers.ModifierExtendedInfo
import com.davanok.dvnkdnd.ui.components.toCompactString
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.base_value_replace
import org.jetbrains.compose.resources.stringResource

@Composable
private fun buildPreviewString(
    operation: DnDModifierOperation,
    valueSource: DnDModifierValueSource,
    value: Double,
    baseValue: String
): String {
    val valueString = if (valueSource == DnDModifierValueSource.CONSTANT) {
        // If it's a unary operation and value is 0, we pass null to indicate "no offset"
        if (operation.isUnaryOffset && value == 0.0) null
        else value.toCompactString()
    } else {
        stringResource(valueSource.stringRes)
    }

    return operation.applyForStringPreview(valueString, baseValue)
}


@Composable
fun ModifierExtendedInfo.buildPreviewString(
    baseValue: String = stringResource(Res.string.base_value_replace)
) = buildPreviewString(
    operation, valueSource, value, baseValue
)
@Composable
fun ModifiersGroup.buildPreviewString(
    baseValue: String = stringResource(Res.string.base_value_replace)
) = buildPreviewString(
    operation, valueSource, value, baseValue
)