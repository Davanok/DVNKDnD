package com.davanok.dvnkdnd.database.model

import androidx.compose.ui.util.fastMap
import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.davanok.dvnkdnd.data.model.entities.CharacterMin
import com.davanok.dvnkdnd.data.model.entities.CharacterWithModifiers
import com.davanok.dvnkdnd.data.model.entities.DnDEntityMin
import com.davanok.dvnkdnd.data.model.entities.DnDEntityWithSubEntities
import com.davanok.dvnkdnd.data.model.entities.toDnDModifier
import com.davanok.dvnkdnd.data.model.entities.toModifiersGroup
import com.davanok.dvnkdnd.database.entities.character.Character
import com.davanok.dvnkdnd.database.entities.character.CharacterClasses
import com.davanok.dvnkdnd.database.entities.character.CharacterStats
import com.davanok.dvnkdnd.database.entities.dndEntities.DnDBaseEntity
import com.davanok.dvnkdnd.database.entities.dndEntities.EntityModifier


fun DnDBaseEntity.toDnDEntityMin() = DnDEntityMin(
    id = id,
    type = type,
    name = name,
    source = source
)

data class EntityWithSub(
    @Embedded val entity: DnDBaseEntity,
    @Relation(parentColumn = "id", entityColumn = "parent_id")
    val subEntities: List<DnDBaseEntity>,
) {
    fun toEntityWithSubEntities() = DnDEntityWithSubEntities(
        id = entity.id,
        type = entity.type,
        name = entity.name,
        source = entity.source,
        subEntities = subEntities.fastMap { it.toDnDEntityMin() }
    )
}
data class DnDClassWithModifiers(
    @Embedded val entity: DnDBaseEntity,
    @Relation(parentColumn = "id", entityColumn = "entity_id")
    val modifiers: List<EntityModifier>
)

data class DbCharacterWithModifiers(
    @Embedded val character: Character,

    @Relation(parentColumn = "id", entityColumn = "id")
    val characterStats: CharacterStats?,

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
    val classesWithModifiers: List<DnDClassWithModifiers>,

    @Relation(parentColumn = "race", entityColumn = "entity_id")
    val raceModifiers: List<EntityModifier>,
    @Relation(parentColumn = "subRace", entityColumn = "entity_id")
    val subRaceModifiers: List<EntityModifier>,
    @Relation(parentColumn = "background", entityColumn = "entity_id")
    val backgroundModifiers: List<EntityModifier>,
    @Relation(parentColumn = "subBackground", entityColumn = "entity_id")
    val subBackgroundModifiers: List<EntityModifier>,
) {
    fun toCharacterWithModifiers() = CharacterWithModifiers(
        character = CharacterMin(
            character.id,
            character.name,
            character.level,
            character.mainImage
        ),
        characterModifiers = characterStats?.toModifiersGroup(),
        classesWithModifiers = classesWithModifiers.associate { it.entity.toDnDEntityMin() to it.modifiers.fastMap { it.toDnDModifier() } },
        raceModifiers = raceModifiers.fastMap { it.toDnDModifier() },
        subRaceModifiers = subRaceModifiers.fastMap { it.toDnDModifier() },
        backgroundModifiers = backgroundModifiers.fastMap { it.toDnDModifier() },
        subBackgroundModifiers = subBackgroundModifiers.fastMap { it.toDnDModifier() }
    )
}
