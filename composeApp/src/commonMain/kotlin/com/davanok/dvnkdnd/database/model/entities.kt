package com.davanok.dvnkdnd.database.model

import androidx.compose.ui.util.fastMap
import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.davanok.dvnkdnd.data.model.entities.CharacterWithModifiers
import com.davanok.dvnkdnd.data.model.entities.DnDEntityMin
import com.davanok.dvnkdnd.data.model.entities.DnDEntityWithSubEntities
import com.davanok.dvnkdnd.data.model.entities.toDnDModifier
import com.davanok.dvnkdnd.data.model.entities.toModifiersGroup
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
    val subEntities: List<DnDBaseEntity>
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
    val id: Long,
    val name: String,
    @Relation(parentColumn = "id", entityColumn = "characterId")
    val characterStats: CharacterStats,
    @Relation(
        parentColumn = "id",
        entityColumn = "entityId",
        associateBy = Junction(DnDBaseEntity::class, parentColumn = "cls", entityColumn = "id")
    )
    val clsModifiers: List<EntityModifier>,
    @Relation(
        parentColumn = "id",
        entityColumn = "entityId",
        associateBy = Junction(DnDBaseEntity::class, parentColumn = "subCls", entityColumn = "id")
    )
    val subClsModifiers: List<EntityModifier>,
    @Relation(
        parentColumn = "id",
        entityColumn = "entityId",
        associateBy = Junction(DnDBaseEntity::class, parentColumn = "race", entityColumn = "id")
    )
    val raceModifiers: List<EntityModifier>,
    @Relation(
        parentColumn = "id",
        entityColumn = "entityId",
        associateBy = Junction(DnDBaseEntity::class, parentColumn = "subRace", entityColumn = "id")
    )
    val subRaceModifiers: List<EntityModifier>,
    @Relation(
        parentColumn = "id",
        entityColumn = "entityId",
        associateBy = Junction(DnDBaseEntity::class, parentColumn = "background", entityColumn = "id")
    )
    val backgroundModifiers: List<EntityModifier>,
    @Relation(
        parentColumn = "id",
        entityColumn = "entityId",
        associateBy = Junction(DnDBaseEntity::class, parentColumn = "subBackground", entityColumn = "id")
    )
    val subBackgroundModifiers: List<EntityModifier>
) {
    fun toCharacterWithModifiers() = CharacterWithModifiers(
        id = id,
        name = name,
        characterModifiers = characterStats.toModifiersGroup(),
        clsModifiers = clsModifiers.fastMap { it.toDnDModifier() },
        subClsModifiers = subClsModifiers.fastMap { it.toDnDModifier() },
        raceModifiers = raceModifiers.fastMap { it.toDnDModifier() },
        subRaceModifiers = subRaceModifiers.fastMap { it.toDnDModifier() },
        backgroundModifiers = backgroundModifiers.fastMap { it.toDnDModifier() },
        subBackgroundModifiers = subBackgroundModifiers.fastMap { it.toDnDModifier() }
    )
}
