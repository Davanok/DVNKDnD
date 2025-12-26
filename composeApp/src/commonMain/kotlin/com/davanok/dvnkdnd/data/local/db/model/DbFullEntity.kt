package com.davanok.dvnkdnd.data.local.db.model

import androidx.room.Embedded
import androidx.room.Relation
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.DbBaseEntity
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.DbEntityAbility
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.DbEntityImage
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.DbEntityModifiersGroup
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.DbEntityProficiency
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.DbSpell
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.DbState
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.companion.DbAbility
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.companion.DbFeat
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.companion.DbProficiency
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.concept.DbBackground
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.concept.DbClass
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.concept.DbRace
import com.davanok.dvnkdnd.data.local.db.entities.items.DbItem

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
        DbState::class,
        parentColumn = "id",
        entityColumn = "id"
    )
    val state: DbFullState?,
)