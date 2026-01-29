package com.davanok.dvnkdnd.data.local.mappers.entities

import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.DbEntityFeature
import com.davanok.dvnkdnd.domain.entities.dndEntities.FullFeature
import com.davanok.dvnkdnd.domain.entities.dndEntities.FeatureLink
import com.davanok.dvnkdnd.domain.entities.dndEntities.FeatureRegain
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.companion.DbFeature
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.companion.DbFeatureRegain
import kotlin.uuid.Uuid


fun FullFeature.toDbFeature(entityId: Uuid) = DbFeature(
    id = entityId,
    usageLimitByLevel = usageLimitByLevel,
    givesStateSelf = givesStateSelf,
    givesStateTarget = givesStateTarget
)
fun FeatureRegain.toDbFeatureRegain(featureId: Uuid) = DbFeatureRegain(
    id = id,
    featureId = featureId,
    regainsCount = regainsCount,
    timeUnit = timeUnit,
    timeUnitCount = timeUnitCount
)
fun DbFeatureRegain.toFeatureRegain() = FeatureRegain(
    id = id,
    regainsCount = regainsCount,
    timeUnit = timeUnit,
    timeUnitCount = timeUnitCount
)
fun DbEntityFeature.toFeatureLink() = FeatureLink(
    featureId = featureId,
    level = level
)
fun FeatureLink.toDbEntityFeature(entityId: Uuid) = DbEntityFeature(
    entityId = entityId,
    featureId = featureId,
    level = level
)