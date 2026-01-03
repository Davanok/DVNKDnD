package com.davanok.dvnkdnd.data.local.db.model

import androidx.room.Embedded
import androidx.room.Relation
import com.davanok.dvnkdnd.data.local.db.entities.items.DbArmor
import com.davanok.dvnkdnd.data.local.db.entities.items.DbItem
import com.davanok.dvnkdnd.data.local.db.entities.items.DbItemActivation
import com.davanok.dvnkdnd.data.local.db.entities.items.DbItemActivationCastsSpell
import com.davanok.dvnkdnd.data.local.db.entities.items.DbItemActivationRegain
import com.davanok.dvnkdnd.data.local.db.entities.items.DbItemEffect
import com.davanok.dvnkdnd.data.local.db.entities.items.DbItemProperty
import com.davanok.dvnkdnd.data.local.db.entities.items.DbItemPropertyLink
import com.davanok.dvnkdnd.data.local.db.entities.items.DbWeapon
import com.davanok.dvnkdnd.data.local.db.entities.items.DbWeaponDamage

data class DbFullWeapon(
    @Embedded
    val weapon: DbWeapon,
    @Relation(
        parentColumn = "id",
        entityColumn = "weapon_id"
    )
    val damages: List<DbWeaponDamage>
)

data class DbJoinItemProperty(
    @Embedded
    val link: DbItemPropertyLink,
    @Relation(
        parentColumn = "property_id",
        entityColumn = "id"
    )
    val property: DbItemProperty
)

data class DbFullItemActivation(
    @Embedded
    val activation: DbItemActivation,
    @Relation(
        parentColumn = "id",
        entityColumn = "id"
    )
    val castsSpell: DbItemActivationCastsSpell?,
    @Relation(
        parentColumn = "id",
        entityColumn = "activation_id"
    )
    val regains: List<DbItemActivationRegain>
)

data class DbFullItem(
    @Embedded
    val item: DbItem,

    @Relation(
        parentColumn = "id",
        entityColumn = "item_id"
    )
    val effects: List<DbItemEffect>,

    @Relation(
        DbItemActivation::class,
        parentColumn = "id",
        entityColumn = "item_id"
    )
    val activations: List<DbFullItemActivation>,

    @Relation(
        DbItemPropertyLink::class,
        parentColumn = "id",
        entityColumn = "item_id"
    )
    val properties: List<DbJoinItemProperty>,
    @Relation(
        parentColumn = "id",
        entityColumn = "id"
    )
    val armor: DbArmor?,
    @Relation(
        DbWeapon::class,
        parentColumn = "id",
        entityColumn = "id"
    )
    val weapon: DbFullWeapon?
)