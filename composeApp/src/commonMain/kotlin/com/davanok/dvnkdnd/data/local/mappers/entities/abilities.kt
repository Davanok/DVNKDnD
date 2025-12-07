package com.davanok.dvnkdnd.data.local.mappers.entities

import com.davanok.dvnkdnd.domain.entities.dndEntities.AbilityInfo
import com.davanok.dvnkdnd.domain.entities.dndEntities.AbilityLink
import com.davanok.dvnkdnd.domain.entities.dndEntities.AbilityRegain
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.DbEntityAbility
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.companion.DbAbility
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.companion.DbAbilityRegain
import kotlin.uuid.Uuid


fun AbilityInfo.toDbAbility(entityId: Uuid) = DbAbility(
    id = entityId,
    usageLimitByLevel = usageLimitByLevel,
    givesStateSelf = givesStateSelf,
    givesStateTarget = givesStateTarget
)
fun AbilityRegain.toDbAbilityRegain(abilityId: Uuid) = DbAbilityRegain(
    id = id,
    abilityId = abilityId,
    regainsCount = regainsCount,
    timeUnit = timeUnit,
    timeUnitCount = timeUnitCount
)
fun DbAbilityRegain.toAbilityRegain() = AbilityRegain(
    id = id,
    regainsCount = regainsCount,
    timeUnit = timeUnit,
    timeUnitCount = timeUnitCount
)
fun DbEntityAbility.toAbilityLink() = AbilityLink(
    abilityId = abilityId,
    level = level
)
fun AbilityLink.toDbEntityAbility(entityId: Uuid) = DbEntityAbility(
    entityId = entityId,
    abilityId = abilityId,
    level = level
)