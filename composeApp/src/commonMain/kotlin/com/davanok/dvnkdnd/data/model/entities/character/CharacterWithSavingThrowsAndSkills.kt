package com.davanok.dvnkdnd.data.model.entities.character

import com.davanok.dvnkdnd.data.model.entities.dndModifiers.DnDModifiersGroup
import kotlin.uuid.Uuid

data class CharacterWithSavingThrowsAndSkills(
    val character: CharacterShortInfo,
    val proficiencyBonus: Int,
    val stats: DnDModifiersGroup,

    val selectedSavingThrows: List<Uuid>,
    val savingThrowsEntities: List<DnDEntityWithSavingThrows>,

    val selectedSkills: List<Uuid>,
    val skillsEntities: List<DnDEntityWithSkills>
)