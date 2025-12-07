package com.davanok.dvnkdnd.database.model.adapters.entities

import com.davanok.dvnkdnd.data.model.entities.dndEntities.AbilityInfo
import com.davanok.dvnkdnd.data.model.entities.dndEntities.AbilityLink
import com.davanok.dvnkdnd.data.model.entities.dndEntities.AbilityRegain
import com.davanok.dvnkdnd.database.entities.dndEntities.DbEntityAbility
import com.davanok.dvnkdnd.database.entities.dndEntities.companion.DbAbility
import com.davanok.dvnkdnd.database.entities.dndEntities.companion.DbAbilityRegain
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