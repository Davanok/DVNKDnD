package com.davanok.dvnkdnd.database.model.entities

import androidx.compose.ui.util.fastMap
import androidx.room.Embedded
import androidx.room.Relation
import com.davanok.dvnkdnd.data.model.entities.dndEntities.DnDFullEntity
import com.davanok.dvnkdnd.database.entities.dndEntities.DnDBaseEntity
import com.davanok.dvnkdnd.database.entities.dndEntities.DnDSpell
import com.davanok.dvnkdnd.database.entities.dndEntities.EntityAbility
import com.davanok.dvnkdnd.database.entities.dndEntities.EntityModifiersGroup
import com.davanok.dvnkdnd.database.entities.dndEntities.EntityProficiency
import com.davanok.dvnkdnd.database.entities.dndEntities.companion.DnDAbility
import com.davanok.dvnkdnd.database.entities.dndEntities.companion.DnDFeat
import com.davanok.dvnkdnd.database.entities.dndEntities.companion.DnDProficiency
import com.davanok.dvnkdnd.database.entities.dndEntities.concept.DnDBackground
import com.davanok.dvnkdnd.database.entities.dndEntities.concept.DnDClass
import com.davanok.dvnkdnd.database.entities.dndEntities.concept.DnDRace
import com.davanok.dvnkdnd.database.entities.items.DnDItem
import com.davanok.dvnkdnd.database.model.adapters.entities.toAbilityLink
import com.davanok.dvnkdnd.database.model.adapters.entities.toFeatInfo
import com.davanok.dvnkdnd.database.model.adapters.entities.toRaceInfo

data class DbFullEntity(
    @Embedded
    val base: DnDBaseEntity,

    @Relation(EntityModifiersGroup::class, parentColumn = "id", entityColumn = "entity_id")
    val modifiers: List<DbModifiersGroups>,

    @Relation(EntityProficiency::class, parentColumn = "id", entityColumn = "entity_id")
    val proficiencies: List<DbJoinProficiency>,
    @Relation(parentColumn = "id", entityColumn = "entity_id")
    val abilities: List<EntityAbility>,

    @Relation(
        entity = DnDClass::class,
        parentColumn = "id",
        entityColumn = "id"
    )
    val cls: DbClassWithSpells?,
    @Relation(
        parentColumn = "id",
        entityColumn = "id"
    )
    val race: DnDRace?,
    @Relation(
        parentColumn = "id",
        entityColumn = "id"
    )
    val background: DnDBackground?,
    @Relation(
        parentColumn = "id",
        entityColumn = "id"
    )
    val feat: DnDFeat?,
    @Relation(
        DnDAbility::class,
        parentColumn = "id",
        entityColumn = "id"
    )
    val ability: DbAbilityInfo?,
    @Relation(
        parentColumn = "id",
        entityColumn = "id"
    )
    val proficiency: DnDProficiency?,
    @Relation(
        DnDSpell::class,
        parentColumn = "id",
        entityColumn = "id"
    )
    val spell: DbFullSpell?,
    @Relation(
        DnDItem::class,
        parentColumn = "id",
        entityColumn = "id"
    )
    val item: DbFullItem?
) {
    fun toDnDFullEntity(): DnDFullEntity = DnDFullEntity(
        id = base.id,
        parentId = base.parentId,
        userId = base.userId,
        type = base.type,
        name = base.name,
        description = base.description,
        source = base.source,
        modifiersGroups = modifiers.fastMap(DbModifiersGroups::toDnDModifiersGroup),
        proficiencies = proficiencies.fastMap(DbJoinProficiency::toJoinProficiency),
        abilities = abilities.fastMap(EntityAbility::toAbilityLink),
        cls = cls?.toClassWithSpells(),
        race = race?.toRaceInfo(),
        background = null,
        feat = feat?.toFeatInfo(),
        ability = ability?.toAbilityInfo(),
        spell = spell?.toFullSpell(),
        item = item?.toFullItem(),
        companionEntities = emptyList()
    )
}