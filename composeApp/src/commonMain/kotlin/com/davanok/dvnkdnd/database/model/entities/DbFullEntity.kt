package com.davanok.dvnkdnd.database.model.entities

import androidx.room.Embedded
import androidx.room.Relation
import com.davanok.dvnkdnd.data.model.entities.DatabaseImage
import com.davanok.dvnkdnd.data.model.entities.dndEntities.DnDFullEntity
import com.davanok.dvnkdnd.database.entities.dndEntities.DbBaseEntity
import com.davanok.dvnkdnd.database.entities.dndEntities.DbEntityAbility
import com.davanok.dvnkdnd.database.entities.dndEntities.DbEntityImage
import com.davanok.dvnkdnd.database.entities.dndEntities.DbEntityModifiersGroup
import com.davanok.dvnkdnd.database.entities.dndEntities.DbEntityProficiency
import com.davanok.dvnkdnd.database.entities.dndEntities.DbSpell
import com.davanok.dvnkdnd.database.entities.dndEntities.DbState
import com.davanok.dvnkdnd.database.entities.dndEntities.companion.DbAbility
import com.davanok.dvnkdnd.database.entities.dndEntities.companion.DbFeat
import com.davanok.dvnkdnd.database.entities.dndEntities.companion.DbProficiency
import com.davanok.dvnkdnd.database.entities.dndEntities.concept.DbBackground
import com.davanok.dvnkdnd.database.entities.dndEntities.concept.DbClass
import com.davanok.dvnkdnd.database.entities.dndEntities.concept.DbRace
import com.davanok.dvnkdnd.database.entities.items.DbItem
import com.davanok.dvnkdnd.database.model.adapters.entities.toAbilityLink
import com.davanok.dvnkdnd.database.model.adapters.entities.toFeatInfo
import com.davanok.dvnkdnd.database.model.adapters.entities.toRaceInfo

data class DbFullEntity(
    @Embedded
    val entity: DbBaseEntity,

    @Relation(parentColumn = "id", entityColumn = "entity_id")
    val images: List<DbEntityImage>,

    @Relation(DbEntityModifiersGroup::class, parentColumn = "id", entityColumn = "entity_id")
    val modifiers: List<DbModifiersGroups>,

    @Relation(DbEntityProficiency::class, parentColumn = "id", entityColumn = "entity_id")
    val proficiencies: List<DbJoinProficiency>,
    @Relation(parentColumn = "id", entityColumn = "entity_id")
    val abilities: List<DbEntityAbility>,

    @Relation(
        entity = DbClass::class,
        parentColumn = "id",
        entityColumn = "id"
    )
    val cls: DbClassWithSpells?,
    @Relation(
        parentColumn = "id",
        entityColumn = "id"
    )
    val race: DbRace?,
    @Relation(
        parentColumn = "id",
        entityColumn = "id"
    )
    val background: DbBackground?,
    @Relation(
        parentColumn = "id",
        entityColumn = "id"
    )
    val feat: DbFeat?,
    @Relation(
        DbAbility::class,
        parentColumn = "id",
        entityColumn = "id"
    )
    val ability: DbAbilityInfo?,
    @Relation(
        parentColumn = "id",
        entityColumn = "id"
    )
    val proficiency: DbProficiency?,
    @Relation(
        DbSpell::class,
        parentColumn = "id",
        entityColumn = "id"
    )
    val spell: DbFullSpell?,
    @Relation(
        DbItem::class,
        parentColumn = "id",
        entityColumn = "id"
    )
    val item: DbFullItem?,
    @Relation(
        parentColumn = "id",
        entityColumn = "id"
    )
    val state: DbState?,
) {
    fun toDnDFullEntity(): DnDFullEntity = DnDFullEntity(
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
        state = null,
        companionEntities = emptyList()
    )
}