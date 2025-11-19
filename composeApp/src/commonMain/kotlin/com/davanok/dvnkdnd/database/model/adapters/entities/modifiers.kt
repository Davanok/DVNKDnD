package com.davanok.dvnkdnd.database.model.adapters.entities

import com.davanok.dvnkdnd.data.model.entities.dndModifiers.DnDModifier
import com.davanok.dvnkdnd.data.model.entities.dndModifiers.ModifiersGroup
import com.davanok.dvnkdnd.database.entities.dndEntities.DbEntityModifier
import com.davanok.dvnkdnd.database.entities.dndEntities.DbEntityModifiersGroup
import kotlin.uuid.Uuid

fun DbEntityModifier.toDnDModifier() = DnDModifier(
    id = id,
    selectable = selectable,
    target = target
)

fun DnDModifier.toDbEntityModifier(groupId: Uuid) = DbEntityModifier(
    id = id,
    groupId = groupId,
    selectable = selectable,
    target = target
)
fun ModifiersGroup.toDbEntityModifiersGroup(entityId: Uuid) = DbEntityModifiersGroup(
    id = id,
    entityId = entityId,
    target = target,
    operation = operation,
    valueSource = valueSource,
    valueSourceTarget = valueSourceTarget,
    value = value,
    name = name,
    description = description,
    selectionLimit = selectionLimit,
    priority = priority,
    clampMax = clampMax,
    clampMin = clampMin,
    minBaseValue = minBaseValue,
    maxBaseValue = maxBaseValue
)