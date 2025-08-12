package com.davanok.dvnkdnd.data.model.entities.character

import androidx.compose.runtime.Immutable
import com.davanok.dvnkdnd.data.model.entities.dndEntities.DnDEntityMin
import com.davanok.dvnkdnd.data.model.entities.dndEntities.DnDFullEntity
import com.davanok.dvnkdnd.data.model.entities.dndModifiers.DnDSkill
import kotlin.uuid.Uuid

data class CharacterWithAllSkills(
    val character: CharacterShortInfo,

    val selectedSkills: List<Uuid>,

    val classes: List<DnDEntityWithSkills>,
    val race: DnDEntityWithSkills?,
    val subRace: DnDEntityWithSkills?,
    val background: DnDEntityWithSkills?,
    val subBackground: DnDEntityWithSkills?
)

@Immutable
data class DnDEntityWithSkills(
    val entity: DnDEntityMin,
    val selectionLimit: Int?,
    val skills: List<DnDSkill>,
)

fun DnDFullEntity.toEntityWithSkills() = DnDEntityWithSkills(
    entity = toDnDEntityMin(),
    selectionLimit = selectionLimits?.skills,
    skills = skills
)