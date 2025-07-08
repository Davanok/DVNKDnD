package com.davanok.dvnkdnd.database.model

import androidx.compose.ui.util.fastMap
import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.davanok.dvnkdnd.data.model.entities.CharacterMin
import com.davanok.dvnkdnd.data.model.entities.CharacterWithAllModifiers
import com.davanok.dvnkdnd.data.model.entities.CharacterWithAllSkills
import com.davanok.dvnkdnd.data.model.entities.toModifiersGroup
import com.davanok.dvnkdnd.database.entities.character.*
import com.davanok.dvnkdnd.database.entities.dndEntities.DnDBaseEntity
import kotlin.uuid.Uuid

fun Character.toCharacterMin() = CharacterMin(
    id = id,
    name = name,
    level = level,
    image = image
)

data class DbCharacterWithAllModifiers(
    @Embedded val character: Character,

    @Relation(parentColumn = "id", entityColumn = "id")
    val characterStats: CharacterStats?,

    @Relation(
        entity = CharacterSelectedModifierBonus::class,
        parentColumn = "id",
        entityColumn = "character_id",
        projection = ["bonus_id"]
    )
    val selectedModifiers: List<Uuid>,

    @Relation(
        entity = DnDBaseEntity::class,
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = CharacterClass::class,
            parentColumn = "character_id",
            entityColumn = "class_id"
        )
    )
    val classesWithModifiers: List<DbEntityWithModifiers>,

    @Relation(
        entity = DnDBaseEntity::class,
        parentColumn = "race",
        entityColumn = "id"
    )
    val raceModifiers: DbEntityWithModifiers?,
    @Relation(
        entity = DnDBaseEntity::class,
        parentColumn = "sub_race",
        entityColumn = "id"
    )
    val subRaceModifiers: DbEntityWithModifiers?,
    @Relation(
        entity = DnDBaseEntity::class,
        parentColumn = "background",
        entityColumn = "id"
    )
    val backgroundModifiers: DbEntityWithModifiers?,
    @Relation(
        entity = DnDBaseEntity::class,
        parentColumn = "sub_background",
        entityColumn = "id"
    )
    val subBackgroundModifiers: DbEntityWithModifiers?,
) {
    fun toCharacterWithAllModifiers() = CharacterWithAllModifiers(
        character = character.toCharacterMin(),
        characterStats = characterStats?.toModifiersGroup(),
        selectedModifiers = selectedModifiers,
        classes = classesWithModifiers.fastMap { it.toDnDEntityWithModifiers() },
        race = raceModifiers?.toDnDEntityWithModifiers(),
        subRace = subRaceModifiers?.toDnDEntityWithModifiers(),
        background = backgroundModifiers?.toDnDEntityWithModifiers(),
        subBackground = subBackgroundModifiers?.toDnDEntityWithModifiers()
    )
}

data class DbCharacterWithAllSkills(
    @Embedded val character: Character,

    @Relation(
        entity = CharacterSelectedSkill::class,
        parentColumn = "id",
        entityColumn = "character_id",
        projection = ["skill_id"]
    )
    val selectedSkills: List<Uuid>,

    @Relation(
        entity = DnDBaseEntity::class,
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = CharacterClass::class,
            parentColumn = "character_id",
            entityColumn = "class_id"
        )
    )
    val classesWithSkills: List<DbEntityWithSkills>,
    @Relation(
        entity = DnDBaseEntity::class,
        parentColumn = "race",
        entityColumn = "id"
    )
    val raceWithSkills: DbEntityWithSkills?,
    @Relation(
        entity = DnDBaseEntity::class,
        parentColumn = "sub_race",
        entityColumn = "id"
    )
    val subRaceWithSkills: DbEntityWithSkills?,
    @Relation(
        entity = DnDBaseEntity::class,
        parentColumn = "background",
        entityColumn = "id"
    )
    val backgroundWithSkills: DbEntityWithSkills?,
    @Relation(
        entity = DnDBaseEntity::class,
        parentColumn = "sub_background",
        entityColumn = "id"
    )
    val subBackgroundWithSkills: DbEntityWithSkills?
) {
    fun toCharacterWithAllSkills() = CharacterWithAllSkills(
        character = character.toCharacterMin(),
        selectedSkills = selectedSkills,
        classes = classesWithSkills.fastMap { it.toDndEntityWithSkills() },
        race = raceWithSkills?.toDndEntityWithSkills(),
        subRace = subRaceWithSkills?.toDndEntityWithSkills(),
        background = backgroundWithSkills?.toDndEntityWithSkills(),
        subBackground = subBackgroundWithSkills?.toDndEntityWithSkills()
    )
}