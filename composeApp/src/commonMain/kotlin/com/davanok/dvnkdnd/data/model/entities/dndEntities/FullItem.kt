package com.davanok.dvnkdnd.data.model.entities.dndEntities

import androidx.room.PrimaryKey
import com.davanok.dvnkdnd.data.model.dndEnums.DamageTypes
import com.davanok.dvnkdnd.data.model.dndEnums.Dices
import com.davanok.dvnkdnd.database.entities.items.Armor
import com.davanok.dvnkdnd.database.entities.items.DnDItem
import com.davanok.dvnkdnd.database.entities.items.DnDItemProperty
import com.davanok.dvnkdnd.database.entities.items.ItemPropertyLink
import com.davanok.dvnkdnd.database.entities.items.Weapon
import com.davanok.dvnkdnd.database.entities.items.WeaponDamage
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class FullItem(
    val cost: Int?, // in copper pieces
    val weight: Int?,

    val properties: List<JoinItemProperty>,
    val armor: ArmorInfo?,
    val weapon: FullWeapon?,
) {
    fun toDnDItem(entityId: Uuid) = DnDItem(entityId, cost, weight)
}
@Serializable
data class ArmorInfo(
    @SerialName("armor_class")
    val armorClass: Int,
    @SerialName("dex_max_modifier")
    val dexMaxModifier: Int?,
    @SerialName("required_strength")
    val requiredStrength: Int?,
    @SerialName("stealth_disadvantage")
    val stealthDisadvantage: Boolean,
)
fun ArmorInfo.toArmor(itemId: Uuid) = Armor(
    id = itemId,
    armorClass = armorClass,
    dexMaxModifier = dexMaxModifier,
    requiredStrength = requiredStrength,
    stealthDisadvantage = stealthDisadvantage
)
fun Armor.toArmorInfo() = ArmorInfo(
    armorClass = armorClass,
    dexMaxModifier = dexMaxModifier,
    requiredStrength = requiredStrength,
    stealthDisadvantage = stealthDisadvantage
)
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
data class ItemProperty(
    val id: Uuid,
    @SerialName("user_id")
    val userId: Uuid?,
    val name: String,
    val description: String,
)
fun ItemProperty.toDnDItemProperty() = DnDItemProperty(
    id = id,
    userId = userId,
    name = name,
    description = description
)
fun DnDItemProperty.toItemProperty() = ItemProperty(
    id = id,
    userId = userId,
    name = name,
    description = description
)
@Serializable
data class FullWeapon(
    @SerialName("atk_bonus")
    val atkBonus: Int,

    val damages: List<WeaponDamageInfo>,
) {
    fun toWeapon(entityId: Uuid) = Weapon(entityId, atkBonus)
}
@Serializable
data class WeaponDamageInfo(
    @PrimaryKey val id: Uuid = Uuid.random(),
    @SerialName("damage_type")
    val damageType: DamageTypes,
    @SerialName("dice_count")
    val diceCount: Int,
    val dice: Dices,
    val modifier: Int
)
fun WeaponDamageInfo.toWeaponDamage(weaponId: Uuid) = WeaponDamage(
    id = id,
    weaponId = weaponId,
    damageType = damageType,
    diceCount = diceCount,
    dice = dice,
    modifier = modifier
)
fun WeaponDamage.toWeaponDamageInfo() = WeaponDamageInfo(
    id = id,
    damageType = damageType,
    diceCount = diceCount,
    dice = dice,
    modifier = modifier
)