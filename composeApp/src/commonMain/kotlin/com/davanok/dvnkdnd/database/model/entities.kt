package com.davanok.dvnkdnd.database.model

import androidx.compose.ui.util.fastMap
import androidx.room.Embedded
import androidx.room.Relation
import com.davanok.dvnkdnd.data.model.entities.DnDEntityMin
import com.davanok.dvnkdnd.data.model.entities.DnDEntityWithModifiers
import com.davanok.dvnkdnd.data.model.entities.DnDEntityWithSubEntities
import com.davanok.dvnkdnd.data.model.entities.toDnDModifier
import com.davanok.dvnkdnd.database.entities.dndEntities.DnDBaseEntity
import com.davanok.dvnkdnd.database.entities.dndEntities.EntityAbility
import com.davanok.dvnkdnd.database.entities.dndEntities.EntityModifier
import com.davanok.dvnkdnd.database.entities.dndEntities.EntityProficiency
import com.davanok.dvnkdnd.database.entities.dndEntities.EntitySavingThrow
import com.davanok.dvnkdnd.database.entities.dndEntities.EntitySelectionLimits
import com.davanok.dvnkdnd.database.entities.dndEntities.EntitySkill


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

data class EntityWithModifiers(
    @Embedded val entity: DnDBaseEntity,
    @Relation(
        entity = EntitySelectionLimits::class,
        parentColumn = "id",
        entityColumn = "id",
        projection = ["modifiers"]
    )
    val selectionLimit: Int?,
    @Relation(parentColumn = "id", entityColumn = "entity_id")
    val modifiers: List<EntityModifier>,
) {
    fun toDnDEntityWithModifiers() = DnDEntityWithModifiers(
        entity = entity.toDnDEntityMin(),
        selectionLimit = selectionLimit,
        modifiers = modifiers.fastMap { it.toDnDModifier() }
    )
}

data class DbFullEntity(
    @Embedded
    val base: DnDBaseEntity,

    @Relation(parentColumn = "id", entityColumn = "entity_id")
    val modifiers: List<EntityModifier>,
    @Relation(parentColumn = "id", entityColumn = "entity_id")
    val skills: List<EntitySkill>,
    @Relation(parentColumn = "id", entityColumn = "entity_id")
    val savingThrow: List<EntitySavingThrow>,

    @Relation(parentColumn = "id", entityColumn = "entity_id")
    val proficiencies: List<EntityProficiency>,
    @Relation(parentColumn = "id", entityColumn = "entity_id")
    val abilities: List<EntityAbility>,

    @Relation(parentColumn = "id", entityColumn = "id")
    val selectionLimits: EntitySelectionLimits?

//    TODO: add companion entities (entities that inherits from BaseEntity) (proficiencies, abilities, class spells)
//    TODO?: add objects that expanding base entity
)


