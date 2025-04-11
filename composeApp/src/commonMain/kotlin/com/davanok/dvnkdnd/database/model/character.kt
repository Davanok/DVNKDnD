package com.davanok.dvnkdnd.database.model

import androidx.compose.ui.util.fastMap
import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.davanok.dvnkdnd.data.model.entities.CharacterMin
import com.davanok.dvnkdnd.data.model.entities.CharacterWithModifiers
import com.davanok.dvnkdnd.data.model.entities.toModifiersGroup
import com.davanok.dvnkdnd.database.entities.character.Character
import com.davanok.dvnkdnd.database.entities.character.CharacterClasses
import com.davanok.dvnkdnd.database.entities.character.CharacterSelectedModifiers
import com.davanok.dvnkdnd.database.entities.character.CharacterStats
import com.davanok.dvnkdnd.database.entities.dndEntities.DnDBaseEntity
import kotlin.uuid.Uuid

data class DbCharacterWithModifiers(
    @Embedded val character: Character,

    @Relation(parentColumn = "id", entityColumn = "id")
    val characterStats: CharacterStats?,

    @Relation(
        entity = CharacterSelectedModifiers::class,
        parentColumn = "id",
        entityColumn = "character_id",
        projection = ["modifier_id"]
    )
    val selectedModifiers: List<Uuid>,

    @Relation(
        entity = DnDBaseEntity::class,
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = CharacterClasses::class,
            parentColumn = "character_id",
            entityColumn = "class_id"
        )
    )
    val classesWithModifiers: List<EntityWithModifiers>,

    @Relation(
        entity = DnDBaseEntity::class,
        parentColumn = "race",
        entityColumn = "id"
    )
    val raceModifiers: EntityWithModifiers?,
    @Relation(
        entity = DnDBaseEntity::class,
        parentColumn = "subRace",
        entityColumn = "id"
    )
    val subRaceModifiers: EntityWithModifiers?,
    @Relation(
        entity = DnDBaseEntity::class,
        parentColumn = "background",
        entityColumn = "id"
    )
    val backgroundModifiers: EntityWithModifiers?,
    @Relation(
        entity = DnDBaseEntity::class,
        parentColumn = "subBackground",
        entityColumn = "id"
    )
    val subBackgroundModifiers: EntityWithModifiers?,
) {
    fun toCharacterWithModifiers() = CharacterWithModifiers(
        character = CharacterMin(
            character.id,
            character.name,
            character.level,
            character.mainImage
        ),
        characterStats = characterStats?.toModifiersGroup(),
        selectedModifiers = selectedModifiers,
        classes = classesWithModifiers.fastMap { it.toDnDEntityWithModifiers() },
        race = raceModifiers?.toDnDEntityWithModifiers(),
        subRace = subRaceModifiers?.toDnDEntityWithModifiers(),
        background = backgroundModifiers?.toDnDEntityWithModifiers(),
        subBackground = subBackgroundModifiers?.toDnDEntityWithModifiers()
    )
}