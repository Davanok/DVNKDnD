package com.davanok.dvnkdnd.data.model.entities.dndEntities

import com.davanok.dvnkdnd.database.entities.items.Armor
import com.davanok.dvnkdnd.database.entities.items.DnDItem
import com.davanok.dvnkdnd.database.entities.items.ItemProperty
import com.davanok.dvnkdnd.database.entities.items.ItemPropertyLink
import com.davanok.dvnkdnd.database.entities.items.Weapon
import com.davanok.dvnkdnd.database.entities.items.WeaponDamage
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class FullItem(
    val id: Uuid,
    val cost: Int?, // in copper pieces
    val weight: Int?,

    val properties: List<JoinItemProperty>,
    val armor: Armor?,
    val weapon: FullWeapon?,
) {
    fun toDnDItem() = DnDItem(id, cost, weight)
}

@Serializable
data class JoinItemProperty(
    @SerialName("item_id")
    val itemId: Uuid,
    @SerialName("property_id")
    val propertyId: Uuid,

    val property: ItemProperty,
) {
    fun toItemPropertyLink() = ItemPropertyLink(itemId, propertyId)
}

@Serializable
data class FullWeapon(
    val id: Uuid,
    @SerialName("atk_bonus")
    val atkBonus: Int,

    val damages: List<WeaponDamage>,
) {
    fun toWeapon() = Weapon(id, atkBonus)
}