package com.davanok.dvnkdnd.ui.components.text

import androidx.compose.runtime.Composable
import com.davanok.dvnkdnd.domain.entities.dndEntities.ArmorInfo
import com.davanok.dvnkdnd.ui.components.appendSep
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.armor_class_short
import dvnkdnd.composeapp.generated.resources.dexterity_short
import dvnkdnd.composeapp.generated.resources.max_value
import dvnkdnd.composeapp.generated.resources.stealth_disadvantage_short
import org.jetbrains.compose.resources.stringResource

@Composable
fun ArmorInfo.buildString() = buildString {
    append(stringResource(Res.string.armor_class_short))
    append(": ")
    append(armorClass)

    if (dexMaxModifier != 0) {
        append(" + ")
        append(stringResource(Res.string.dexterity_short))

        if (dexMaxModifier != null) {
            append(" (")
            append(stringResource(Res.string.max_value, dexMaxModifier))
            append(')')
        }
    }

    if (stealthDisadvantage) {
        appendSep()
        append(stringResource(Res.string.stealth_disadvantage_short))
    }
}