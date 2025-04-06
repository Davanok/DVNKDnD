package com.davanok.dvnkdnd.database.model

import androidx.compose.ui.util.fastMap
import androidx.room.Embedded
import androidx.room.Relation
import com.davanok.dvnkdnd.data.model.entities.CharacterMin
import com.davanok.dvnkdnd.data.model.entities.CharacterWithModifiers
import com.davanok.dvnkdnd.data.model.entities.DnDEntityMin
import com.davanok.dvnkdnd.data.model.entities.DnDEntityWithSubEntities
import com.davanok.dvnkdnd.data.model.entities.toDnDModifier
import com.davanok.dvnkdnd.data.model.entities.toModifiersGroup
import com.davanok.dvnkdnd.database.entities.character.Character
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
    @Relation(parentColumn = "id", entityColumn = "parentId")
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

data class DbCharacterWithModifiers(
    @Embedded val character: Character,
    @Relation(parentColumn = "id", entityColumn = "characterId")
    val characterStats: CharacterStats,
    @Relation(
        entity = EntityModifier::class,
        parentColumn = "cls",
        entityColumn = "entityId"
    )
    val clsModifiers: List<EntityModifier>,
    @Relation(
        entity = EntityModifier::class,
        parentColumn = "subCls",
        entityColumn = "entityId"
    )
    val subClsModifiers: List<EntityModifier>,
    @Relation(
        entity = EntityModifier::class,
        parentColumn = "race",
        entityColumn = "entityId"
    )
    val raceModifiers: List<EntityModifier>,
    @Relation(
        entity = EntityModifier::class,
        parentColumn = "subRace",
        entityColumn = "entityId"
    )
    val subRaceModifiers: List<EntityModifier>,
    @Relation(
        entity = EntityModifier::class,
        parentColumn = "background",
        entityColumn = "entityId"
    )
    val backgroundModifiers: List<EntityModifier>,
    @Relation(
        entity = EntityModifier::class,
        parentColumn = "subBackground",
        entityColumn = "entityId"
    )
    val subBackgroundModifiers: List<EntityModifier>
) {
    fun toCharacterWithModifiers() = CharacterWithModifiers(
        character = CharacterMin(character.id, character.name, character.level, character.mainImage),
        characterModifiers = characterStats.toModifiersGroup(),
        clsModifiers = clsModifiers.fastMap { it.toDnDModifier() },
        subClsModifiers = subClsModifiers.fastMap { it.toDnDModifier() },
        raceModifiers = raceModifiers.fastMap { it.toDnDModifier() },
        subRaceModifiers = subRaceModifiers.fastMap { it.toDnDModifier() },
        backgroundModifiers = backgroundModifiers.fastMap { it.toDnDModifier() },
        subBackgroundModifiers = subBackgroundModifiers.fastMap { it.toDnDModifier() }
    )
}
