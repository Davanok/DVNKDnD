package com.davanok.dvnkdnd.data.local.mappers.entities

import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.companion.DbAbilityRegain
import com.davanok.dvnkdnd.data.local.db.model.DbAbilityInfo
import com.davanok.dvnkdnd.data.local.db.model.DbJoinProficiency
import com.davanok.dvnkdnd.domain.entities.dndEntities.AbilityInfo
import com.davanok.dvnkdnd.domain.entities.dndEntities.JoinProficiency

fun DbJoinProficiency.toJoinProficiency() = JoinProficiency(
    level = link.level,
    proficiency = proficiency.toProficiency()
)


fun DbAbilityInfo.toAbilityInfo() = AbilityInfo(
    usageLimitByLevel = ability.usageLimitByLevel,
    regains = regains.map(DbAbilityRegain::toAbilityRegain),
    givesStateSelf = ability.givesStateSelf,
    givesStateTarget = ability.givesStateTarget
)