package com.davanok.dvnkdnd.domain.enums.dndEnums

import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.value_source_attribute
import dvnkdnd.composeapp.generated.resources.value_source_attribute_modifier
import dvnkdnd.composeapp.generated.resources.value_source_character_level
import dvnkdnd.composeapp.generated.resources.value_source_entity_level
import dvnkdnd.composeapp.generated.resources.value_source_flat
import dvnkdnd.composeapp.generated.resources.value_source_proficiency_bonus
import dvnkdnd.composeapp.generated.resources.value_source_skill_modifier
import org.jetbrains.compose.resources.StringResource

enum class ValueSourceType(val stringRes: StringResource) {
    FLAT                (Res.string.value_source_flat),                   // Use 'flatValue' directly (e.g., +2)
    ATTRIBUTE           (Res.string.value_source_attribute),              // Use Stat. Key: "DEXTERITY"
    ATTRIBUTE_MODIFIER  (Res.string.value_source_attribute_modifier),     // Use (Stat-10)/2. Key: "DEXTERITY"
    SKILL_MODIFIER      (Res.string.value_source_skill_modifier),         // Use (Stat-10)/2 + bonuses. Key: "STEALTH"
    PROFICIENCY_BONUS   (Res.string.value_source_proficiency_bonus),      // Use current character PB
    CHARACTER_LEVEL     (Res.string.value_source_character_level),        // Use Character Level
    ENTITY_LEVEL        (Res.string.value_source_entity_level)            // Use Level of entity that gives that modifier or character level.
}