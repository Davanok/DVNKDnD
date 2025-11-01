package com.davanok.dvnkdnd.data.model.entities.dndEntities

import com.davanok.dvnkdnd.data.model.dndEnums.DamageTypes
import com.davanok.dvnkdnd.data.model.dndEnums.Dices
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class FullItem(
    val cost: Int?, // in copper pieces
    val weight: Int?,
    val attunement: Boolean,

    val properties: List<JoinItemProperty>,
    val armor: ArmorInfo?,
    val weapon: FullWeapon?,
)
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
@Serializable
data class JoinItemProperty(
    @SerialName("item_id")
    val itemId: Uuid,
    @SerialName("property_id")
    val propertyId: Uuid,

    val property: ItemProperty,
)
@Serializable
data class ItemProperty(
    val id: Uuid,
    @SerialName("user_id")
    val userId: Uuid?,
    val name: String,
    val description: String,
)
@Serializable
data class FullWeapon(
    @SerialName("atk_bonus")
    val atkBonus: Int,

    val damages: List<WeaponDamageInfo>,
)
@Serializable
data class WeaponDamageInfo(
    val id: Uuid = Uuid.random(),
    @SerialName("damage_type")
    val damageType: DamageTypes,
    @SerialName("dice_count")
    val diceCount: Int,
    val dice: Dices,
    val modifier: Int
)