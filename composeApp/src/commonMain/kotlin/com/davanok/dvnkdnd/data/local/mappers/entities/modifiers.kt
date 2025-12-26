package com.davanok.dvnkdnd.data.local.mappers.entities

import com.davanok.dvnkdnd.domain.entities.dndModifiers.DnDModifier
import com.davanok.dvnkdnd.domain.entities.dndModifiers.ModifiersGroup
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.DbEntityModifier
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.DbEntityModifiersGroup
import com.davanok.dvnkdnd.data.local.db.model.DbModifiersGroups
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

fun DbModifiersGroups.toDnDModifiersGroup() = ModifiersGroup(
    id = group.id,
    target = group.target,
    operation = group.operation,
    valueSource = group.valueSource,
    valueSourceTarget = group.valueSourceTarget,
    value = group.value,
    name = group.name,
    description = group.description,
    selectionLimit = group.selectionLimit,
    priority = group.priority,
    clampMax = group.clampMax,
    clampMin = group.clampMin,
    minBaseValue = group.minBaseValue,
    maxBaseValue = group.maxBaseValue,
    modifiers = modifiers.map(DbEntityModifier::toDnDModifier)
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