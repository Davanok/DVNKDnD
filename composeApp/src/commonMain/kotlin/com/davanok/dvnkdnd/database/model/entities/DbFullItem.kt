package com.davanok.dvnkdnd.database.model.entities

import androidx.room.Embedded
import androidx.room.Relation
import com.davanok.dvnkdnd.data.model.entities.dndEntities.FullItem
import com.davanok.dvnkdnd.data.model.entities.dndEntities.FullWeapon
import com.davanok.dvnkdnd.data.model.entities.dndEntities.JoinItemProperty
import com.davanok.dvnkdnd.database.entities.items.DbArmor
import com.davanok.dvnkdnd.database.entities.items.DbItem
import com.davanok.dvnkdnd.database.entities.items.DbItemProperty
import com.davanok.dvnkdnd.database.entities.items.DbItemPropertyLink
import com.davanok.dvnkdnd.database.entities.items.DbWeapon
import com.davanok.dvnkdnd.database.entities.items.DbWeaponDamage
import com.davanok.dvnkdnd.database.model.adapters.entities.toArmorInfo
import com.davanok.dvnkdnd.database.model.adapters.entities.toItemProperty
import com.davanok.dvnkdnd.database.model.adapters.entities.toWeaponDamageInfo

data class DbFullWeapon(
    @Embedded
    val weapon: DbWeapon,
    @Relation(
        parentColumn = "id",
        entityColumn = "weapon_id"
    )
    val damages: List<DbWeaponDamage>
) {
    fun toFullWeapon() = FullWeapon(
        atkBonus = weapon.atkBonus,
        damages = damages.map(DbWeaponDamage::toWeaponDamageInfo)
    )
}
data class DbJoinItemProperty(
    @Embedded
    val link: DbItemPropertyLink,
    @Relation(
        parentColumn = "property_id",
        entityColumn = "id"
    )
    val property: DbItemProperty
) {
    fun toJoinItemProperty() = JoinItemProperty(
        itemId = link.itemId,
        propertyId = link.propertyId,
        property = property.toItemProperty()
    )
}
data class DbFullItem(
    @Embedded
    val item: DbItem,
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
) {
    fun toFullItem() = FullItem(
        cost = item.cost,
        weight = item.weight,
        attunement = item.attunement,
        properties = properties.map(DbJoinItemProperty::toJoinItemProperty),
        armor = armor?.toArmorInfo(),
        weapon = weapon?.toFullWeapon()
    )
}