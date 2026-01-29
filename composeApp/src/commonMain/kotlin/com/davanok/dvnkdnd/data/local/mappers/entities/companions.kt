package com.davanok.dvnkdnd.data.local.mappers.entities

import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.companion.DbFeatureRegain
import com.davanok.dvnkdnd.data.local.db.model.DbFullFeature
import com.davanok.dvnkdnd.data.local.db.model.DbJoinProficiency
import com.davanok.dvnkdnd.domain.entities.dndEntities.FullFeature
import com.davanok.dvnkdnd.domain.entities.dndEntities.JoinProficiency

fun DbJoinProficiency.toJoinProficiency() = JoinProficiency(
    level = link.level,
    proficiency = proficiency.toProficiency()
)


fun DbFullFeature.toFullFeature() = FullFeature(
    usageLimitByLevel = feature.usageLimitByLevel,
    regains = regains.map(DbFeatureRegain::toFeatureRegain),
    givesStateSelf = feature.givesStateSelf,
    givesStateTarget = feature.givesStateTarget
)