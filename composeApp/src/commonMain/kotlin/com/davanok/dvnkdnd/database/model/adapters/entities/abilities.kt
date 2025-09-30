package com.davanok.dvnkdnd.database.model.adapters.entities

import com.davanok.dvnkdnd.data.model.entities.dndEntities.AbilityInfo
import com.davanok.dvnkdnd.data.model.entities.dndEntities.AbilityLink
import com.davanok.dvnkdnd.data.model.entities.dndEntities.AbilityRegain
import com.davanok.dvnkdnd.database.entities.dndEntities.EntityAbility
import com.davanok.dvnkdnd.database.entities.dndEntities.companion.DnDAbility
import com.davanok.dvnkdnd.database.entities.dndEntities.companion.DnDAbilityRegain
import kotlin.uuid.Uuid


fun AbilityInfo.toDnDAbility(entityId: Uuid) = DnDAbility(
    id = entityId,
    usageLimitByLevel = usageLimitByLevel
)
fun AbilityRegain.toDnDAbilityRegain(abilityId: Uuid) = DnDAbilityRegain(
    id = id,
    abilityId = abilityId,
    regainsCount = regainsCount,
    timeUnit = timeUnit,
    timeUnitCount = timeUnitCount
)
fun DnDAbilityRegain.toAbilityRegain() = AbilityRegain(
    id = id,
    regainsCount = regainsCount,
    timeUnit = timeUnit,
    timeUnitCount = timeUnitCount
)
fun EntityAbility.toAbilityLink() = AbilityLink(
    abilityId = abilityId,
    level = level
)
fun AbilityLink.toEntityAbility(entityId: Uuid) = EntityAbility(
    entityId = entityId,
    abilityId = abilityId,
    level = level
)