package com.davanok.dvnkdnd.database.model.entities

import androidx.compose.ui.util.fastMap
import androidx.room.Embedded
import androidx.room.Relation
import com.davanok.dvnkdnd.data.model.entities.dndEntities.FullItem
import com.davanok.dvnkdnd.data.model.entities.dndEntities.FullWeapon
import com.davanok.dvnkdnd.data.model.entities.dndEntities.JoinItemProperty
import com.davanok.dvnkdnd.database.entities.items.Armor
import com.davanok.dvnkdnd.database.entities.items.DnDItem
import com.davanok.dvnkdnd.database.entities.items.DnDItemProperty
import com.davanok.dvnkdnd.database.entities.items.ItemPropertyLink
import com.davanok.dvnkdnd.database.entities.items.Weapon
import com.davanok.dvnkdnd.database.entities.items.WeaponDamage
import com.davanok.dvnkdnd.database.model.adapters.entities.toArmorInfo
import com.davanok.dvnkdnd.database.model.adapters.entities.toItemProperty
import com.davanok.dvnkdnd.database.model.adapters.entities.toWeaponDamageInfo

data class DbFullWeapon(
    @Embedded
    val weapon: Weapon,
    @Relation(
        parentColumn = "id",
        entityColumn = "weapon_id"
    )
    val damages: List<WeaponDamage>
) {
    fun toFullWeapon() = FullWeapon(
        atkBonus = weapon.atkBonus,
        damages = damages.fastMap(WeaponDamage::toWeaponDamageInfo)
    )
}
data class DbJoinItemProperty(
    @Embedded
    val link: ItemPropertyLink,
    @Relation(
        parentColumn = "property_id",
        entityColumn = "id"
    )
    val property: DnDItemProperty
) {
    fun toJoinItemProperty() = JoinItemProperty(
        itemId = link.itemId,
        propertyId = link.propertyId,
        property = property.toItemProperty()
    )
}
data class DbFullItem(
    @Embedded
    val item: DnDItem,
    @Relation(
        ItemPropertyLink::class,
        parentColumn = "id",
        entityColumn = "item_id"
    )
    val properties: List<DbJoinItemProperty>,
    @Relation(
        parentColumn = "id",
        entityColumn = "id"
    )
    val armor: Armor?,
    @Relation(
        Weapon::class,
        parentColumn = "id",
        entityColumn = "id"
    )
    val weapon: DbFullWeapon?
) {
    fun toFullItem() = FullItem(
        cost = item.cost,
        weight = item.weight,
        properties = properties.fastMap(DbJoinItemProperty::toJoinItemProperty),
        armor = armor?.toArmorInfo(),
        weapon = weapon?.toFullWeapon()
    )
}