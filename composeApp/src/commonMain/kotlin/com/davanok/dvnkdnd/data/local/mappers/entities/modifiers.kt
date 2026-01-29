package com.davanok.dvnkdnd.data.local.mappers.entities

import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.DbEntityDamageModifier
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.DbEntityModifiersGroup
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.DbEntityRollModifier
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.DbEntityValueModifier
import com.davanok.dvnkdnd.data.local.db.model.DbEntityFullModifiersGroup
import com.davanok.dvnkdnd.domain.entities.dndModifiers.DnDDamageModifier
import com.davanok.dvnkdnd.domain.entities.dndModifiers.DnDRollModifier
import com.davanok.dvnkdnd.domain.entities.dndModifiers.DnDValueModifier
import com.davanok.dvnkdnd.domain.entities.dndModifiers.ModifiersGroup
import kotlin.uuid.Uuid

fun DbEntityFullModifiersGroup.toDnDModifiersGroup() = ModifiersGroup(
    id = group.id,
    name = group.name,
    description = group.description,
    selectionLimit = group.selectionLimit,
    modifiers = valueModifiers.map { it.toDnDModifier() } + rollModifiers.map { it.toDnDModifier() } + damageModifiers.map { it.toDnDModifier() }
)

fun DbEntityValueModifier.toDnDModifier() = DnDValueModifier(
    id = id,
    priority = priority,
    targetScope = targetScope,
    targetKey = targetKey,
    operation = operation,
    sourceType = sourceType,
    sourceKey = sourceKey,
    multiplier = multiplier,
    flatValue = flatValue,
    condition = condition
)

fun DbEntityRollModifier.toDnDModifier() = DnDRollModifier(
    id = id,
    targetScope = targetScope,
    targetKey = targetKey,
    operation = operation,
    condition = condition
)

fun DbEntityDamageModifier.toDnDModifier() = DnDDamageModifier(
    id = id,
    damageType = damageType,
    interaction = interaction,
    condition = condition
)

fun DnDValueModifier.toDbEntityValueModifier(groupId: Uuid) = DbEntityValueModifier(
    id = id,
    groupId = groupId,
    priority = priority,
    targetScope = targetScope,
    targetKey = targetKey,
    operation = operation,
    sourceType = sourceType,
    sourceKey = sourceKey,
    multiplier = multiplier,
    flatValue = flatValue,
    condition = condition
)

fun DnDRollModifier.toDbEntityRollModifier(groupId: Uuid) = DbEntityRollModifier(
    id = id,
    groupId = groupId,
    targetScope = targetScope,
    targetKey = targetKey,
    operation = operation,
    condition = condition
)

fun DnDDamageModifier.toDbEntityDamageModifier(groupId: Uuid) = DbEntityDamageModifier(
    id = id,
    groupId = groupId,
    damageType = damageType,
    interaction = interaction,
    condition = condition
)

fun ModifiersGroup.toDbEntityModifiersGroup(entityId: Uuid) = DbEntityModifiersGroup(
    id = id,
    entityId = entityId,
    name = name,
    description = description,
    selectionLimit = selectionLimit
)