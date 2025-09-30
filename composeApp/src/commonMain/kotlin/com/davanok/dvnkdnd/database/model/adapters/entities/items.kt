package com.davanok.dvnkdnd.database.model.adapters.entities

import com.davanok.dvnkdnd.data.model.entities.dndEntities.ArmorInfo
import com.davanok.dvnkdnd.data.model.entities.dndEntities.FullItem
import com.davanok.dvnkdnd.data.model.entities.dndEntities.FullWeapon
import com.davanok.dvnkdnd.data.model.entities.dndEntities.ItemProperty
import com.davanok.dvnkdnd.data.model.entities.dndEntities.JoinItemProperty
import com.davanok.dvnkdnd.data.model.entities.dndEntities.WeaponDamageInfo
import com.davanok.dvnkdnd.database.entities.items.Armor
import com.davanok.dvnkdnd.database.entities.items.DnDItem
import com.davanok.dvnkdnd.database.entities.items.DnDItemProperty
import com.davanok.dvnkdnd.database.entities.items.ItemPropertyLink
import com.davanok.dvnkdnd.database.entities.items.Weapon
import com.davanok.dvnkdnd.database.entities.items.WeaponDamage
import kotlin.uuid.Uuid


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

fun FullItem.toDnDItem(entityId: Uuid) = DnDItem(entityId, cost, weight)

fun JoinItemProperty.toItemPropertyLink() = ItemPropertyLink(itemId, propertyId)

fun FullWeapon.toWeapon(entityId: Uuid) = Weapon(entityId, atkBonus)