package com.davanok.dvnkdnd.data.model.dndEnums

import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.value_source_character_level
import dvnkdnd.composeapp.generated.resources.value_source_constant
import dvnkdnd.composeapp.generated.resources.value_source_entity_level
import dvnkdnd.composeapp.generated.resources.value_source_proficiency_bonus
import org.jetbrains.compose.resources.StringResource

enum class DnDModifierValueSource(val stringRes: StringResource) {
    CONSTANT            (Res.string.value_source_constant),
    CHARACTER_LEVEL     (Res.string.value_source_character_level),
    ENTITY_LEVEL        (Res.string.value_source_entity_level),
    PROFICIENCY_BONUS   (Res.string.value_source_proficiency_bonus)
}