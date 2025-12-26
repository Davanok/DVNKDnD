package com.davanok.dvnkdnd.data.local.mappers.entities

import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.DbBaseEntity
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.DbEntityAbility
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.companion.DbFeat
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.concept.DbRace
import com.davanok.dvnkdnd.data.local.db.model.DbEntityWithSub
import com.davanok.dvnkdnd.data.local.db.model.DbFullEntity
import com.davanok.dvnkdnd.data.local.db.model.DbJoinProficiency
import com.davanok.dvnkdnd.data.local.db.model.DbModifiersGroups
import com.davanok.dvnkdnd.domain.entities.DatabaseImage
import com.davanok.dvnkdnd.domain.entities.dndEntities.DnDEntityMin
import com.davanok.dvnkdnd.domain.entities.dndEntities.DnDEntityWithSubEntities
import com.davanok.dvnkdnd.domain.entities.dndEntities.DnDFullEntity
import com.davanok.dvnkdnd.domain.entities.dndEntities.FeatInfo
import com.davanok.dvnkdnd.domain.entities.dndEntities.RaceInfo
import kotlin.uuid.Uuid

fun DbBaseEntity.toDnDEntityMin() = DnDEntityMin(
    id = id,
    type = type,
    name = name,
    source = source
)

fun DbFeat.toFeatInfo() = FeatInfo(
    repeatable = repeatable
)
fun FeatInfo.toDbFeat(entityId: Uuid) = DbFeat(
    id = entityId,
    repeatable = repeatable
)

fun DbRace.toRaceInfo() = RaceInfo(
    speed = speed,
    size = size
)
fun RaceInfo.toDbRace(entityId: Uuid) = DbRace(
    id = entityId,
    speed = speed,
    size = size
)

fun DbEntityWithSub.toEntityWithSubEntities() = DnDEntityWithSubEntities(
    id = entity.id,
    type = entity.type,
    name = entity.name,
    source = entity.source,
    subEntities = subEntities.map(DbBaseEntity::toDnDEntityMin)
)

fun DbFullEntity.toDnDFullEntity(): DnDFullEntity = DnDFullEntity(
    entity = entity.toEntityBase(),
    images = images.map { DatabaseImage(it.id, it.path) },
    modifiersGroups = modifiers.map(DbModifiersGroups::toDnDModifiersGroup),
    proficiencies = proficiencies.map(DbJoinProficiency::toJoinProficiency),
    abilities = abilities.map(DbEntityAbility::toAbilityLink),
    cls = cls?.toClassWithSpells(),
    race = race?.toRaceInfo(),
    background = null,
    feat = feat?.toFeatInfo(),
    ability = ability?.toAbilityInfo(),
    spell = spell?.toFullSpell(),
    item = item?.toFullItem(),
    state = state?.toFullState(),
    companionEntities = emptyList()
)