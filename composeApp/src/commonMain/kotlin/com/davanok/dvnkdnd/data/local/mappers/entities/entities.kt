package com.davanok.dvnkdnd.data.local.mappers.entities

import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.DbEntityAbility
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.DbEntityImage
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.companion.DbFeat
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.concept.DbRace
import com.davanok.dvnkdnd.data.local.db.model.DbEntityWithImages
import com.davanok.dvnkdnd.data.local.db.model.DbEntityWithSub
import com.davanok.dvnkdnd.data.local.db.model.DbFullEntity
import com.davanok.dvnkdnd.data.local.db.model.DbJoinProficiency
import com.davanok.dvnkdnd.data.local.db.model.DbModifiersGroups
import com.davanok.dvnkdnd.domain.entities.dndEntities.DnDEntityMin
import com.davanok.dvnkdnd.domain.entities.dndEntities.DnDEntityWithSubEntities
import com.davanok.dvnkdnd.domain.entities.dndEntities.DnDFullEntity
import com.davanok.dvnkdnd.domain.entities.dndEntities.FeatInfo
import com.davanok.dvnkdnd.domain.entities.dndEntities.RaceInfo
import kotlin.uuid.Uuid

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

fun List<DbEntityImage>.mainImage(): DbEntityImage? =
    firstOrNull { it.isMain } ?: firstOrNull()

fun DbEntityWithImages.toDnDEntityMin() = DnDEntityMin(
    id = entity.id,
    type = entity.type,
    name = entity.name,
    description = entity.description,
    source = entity.source,
    image = images.mainImage()?.path,
)
fun DbEntityWithSub.toEntityWithSubEntities() = DnDEntityWithSubEntities(
    id = entity.entity.id,
    type = entity.entity.type,
    name = entity.entity.name,
    description = entity.entity.description,
    source = entity.entity.source,
    image = entity.images.mainImage()?.path,
    subEntities = subEntities.map(DbEntityWithImages::toDnDEntityMin)
)

fun DbFullEntity.toDnDFullEntity(): DnDFullEntity = DnDFullEntity(
    entity = entity.toEntityBase(),
    images = images.map(DbEntityImage::toDatabaseImage),
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