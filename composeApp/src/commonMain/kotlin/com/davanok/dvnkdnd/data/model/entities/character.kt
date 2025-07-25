package com.davanok.dvnkdnd.data.model.entities

import androidx.compose.ui.util.fastMap
import okio.Path
import kotlin.uuid.Uuid


data class CharacterMin(
    val id: Uuid,
    val name: String,
    val level: Int,
    val image: Path? = null
)

data class CharacterWithAllModifiers(
    val character: CharacterMin,
    val characterStats: DnDModifiersGroup?,

    val selectedModifierBonuses: List<Uuid>,
    val classes: List<DnDEntityWithModifiers>,
    val race: DnDEntityWithModifiers?,
    val subRace: DnDEntityWithModifiers?,
    val background: DnDEntityWithModifiers?,
    val subBackground: DnDEntityWithModifiers?,
)

data class CharacterWithAllSkills(
    val character: CharacterMin,

    val selectedSkills: List<Uuid>,

    val classes: List<DnDEntityWithSkills>,
    val race: DnDEntityWithSkills?,
    val subRace: DnDEntityWithSkills?,
    val background: DnDEntityWithSkills?,
    val subBackground: DnDEntityWithSkills?
)

data class CharacterFull(
    val character: CharacterMin,

    val images: List<DatabaseImage>,

    val stats: DnDModifiersGroup?,
    val health: DnDCharacterHealth?,
    val usedSpells: List<Int>,

    val classes: List<DnDFullEntity>,
    val race: DnDFullEntity?,
    val subRace: DnDFullEntity?,
    val background: DnDFullEntity?,
    val subBackground: DnDFullEntity?,

    val feats: List<DnDFullEntity>,

    val selectedModifierBonuses: List<Uuid>,
    val selectedSkills: List<Uuid>,
    val selectedProficiencies: List<Uuid>
) {
    fun toCharacterWithAllModifiers() = CharacterWithAllModifiers(
        character = character,
        characterStats = stats,
        selectedModifierBonuses = selectedModifierBonuses,
        classes = classes.fastMap { it.toEntityWithModifiers() },
        race = race?.toEntityWithModifiers(),
        subRace = subRace?.toEntityWithModifiers(),
        background = background?.toEntityWithModifiers(),
        subBackground = subBackground?.toEntityWithModifiers()
    )
    fun toCharacterWithAllSkills() = CharacterWithAllSkills(
        character = character,
        selectedSkills = selectedSkills,
        classes = classes.fastMap { it.toEntityWithSkills() },
        race = race?.toEntityWithSkills(),
        subRace = subRace?.toEntityWithSkills(),
        background = background?.toEntityWithSkills(),
        subBackground = subBackground?.toEntityWithSkills()
    )
    companion object {
        private fun DnDFullEntity.toEntityWithModifiers() = DnDEntityWithModifiers(
            entity = toDnDEntityMin(),
            selectionLimit = selectionLimits?.modifiers,
            modifiers = modifierBonuses
        )
        private fun DnDFullEntity.toEntityWithSkills() = DnDEntityWithSkills(
            entity = toDnDEntityMin(),
            selectionLimit = selectionLimits?.skills,
            skills = skills
        )
    }
}

data class DnDCharacterHealth(
    val max: Int,
    val current: Int,
    val temp: Int
)
