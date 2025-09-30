package com.davanok.dvnkdnd.database.model.adapters.entities

import com.davanok.dvnkdnd.data.model.entities.dndModifiers.DnDModifier
import com.davanok.dvnkdnd.data.model.entities.dndModifiers.DnDModifiersGroup
import com.davanok.dvnkdnd.database.entities.dndEntities.EntityModifier
import com.davanok.dvnkdnd.database.entities.dndEntities.EntityModifiersGroup
import kotlin.uuid.Uuid

fun EntityModifier.toDnDModifier() = DnDModifier(
    id = id,
    selectable = selectable,
    value = value,
    target = target
)

fun DnDModifier.toEntityModifier(groupId: Uuid) = EntityModifier(
    id = id,
    groupId = groupId,
    selectable = selectable,
    value = value,
    target = target
)
fun DnDModifiersGroup.toEntityModifiersGroup(entityId: Uuid) = EntityModifiersGroup(
    id = id,
    entityId = entityId,
    target = target,
    operation = operation,
    valueSource = valueSource,
    name = name,
    description = description,
    selectionLimit = selectionLimit,
    priority = priority,
    clampMax = clampMax,
    clampMin = clampMin,
    minBaseValue = minBaseValue,
    maxBaseValue = maxBaseValue
)