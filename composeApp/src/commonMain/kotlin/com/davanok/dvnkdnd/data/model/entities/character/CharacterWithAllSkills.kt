package com.davanok.dvnkdnd.data.model.entities.character

import androidx.compose.ui.util.fastMap
import com.davanok.dvnkdnd.data.model.entities.dndEntities.DnDEntityWithSkills
import com.davanok.dvnkdnd.data.model.entities.dndEntities.DnDFullEntity
import kotlin.uuid.Uuid

data class CharacterWithAllSkills(
    val character: CharacterMin,

    val selectedSkills: List<Uuid>,

    val classes: List<DnDEntityWithSkills>,
    val race: DnDEntityWithSkills?,
    val subRace: DnDEntityWithSkills?,
    val background: DnDEntityWithSkills?,
    val subBackground: DnDEntityWithSkills?
)

fun CharacterFull.toCharacterWithAllSkills() = CharacterWithAllSkills(
    character = character,
    selectedSkills = selectedSkills,
    classes = classes.fastMap { it.toEntityWithSkills() },
    race = race?.toEntityWithSkills(),
    subRace = subRace?.toEntityWithSkills(),
    background = background?.toEntityWithSkills(),
    subBackground = subBackground?.toEntityWithSkills()
)

fun DnDFullEntity.toEntityWithSkills() = DnDEntityWithSkills(
    entity = toDnDEntityMin(),
    selectionLimit = selectionLimits?.skills,
    skills = skills
)