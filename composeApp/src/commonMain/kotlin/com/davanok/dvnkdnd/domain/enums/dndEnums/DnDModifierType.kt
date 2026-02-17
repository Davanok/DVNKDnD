package com.davanok.dvnkdnd.domain.enums.dndEnums

import com.davanok.dvnkdnd.core.UiEntry
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.modifier_type_damage
import dvnkdnd.composeapp.generated.resources.modifier_type_roll
import dvnkdnd.composeapp.generated.resources.modifier_type_value
import org.jetbrains.compose.resources.StringResource

enum class DnDModifierType(override val stringRes: StringResource): UiEntry {
    VALUE   (Res.string.modifier_type_value),
    ROLL    (Res.string.modifier_type_roll),
    DAMAGE  (Res.string.modifier_type_damage)
}