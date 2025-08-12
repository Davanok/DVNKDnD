package com.davanok.dvnkdnd.data.model.entities.dndModifiers

import com.davanok.dvnkdnd.data.model.dndEnums.Stats
import com.davanok.dvnkdnd.database.entities.dndEntities.EntitySavingThrow
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class DnDSavingThrow(
    val id: Uuid = Uuid.random(),
    val selectable: Boolean,
    val stat: Stats,
)
fun EntitySavingThrow.toDnDSavingThrow() = DnDSavingThrow(
    id = id,
    selectable = selectable,
    stat = stat
)
fun DnDSavingThrow.toEntitySavingThrow(entityId: Uuid) = EntitySavingThrow(
    id = id,
    entityId = entityId,
    selectable = selectable,
    stat = stat
)
