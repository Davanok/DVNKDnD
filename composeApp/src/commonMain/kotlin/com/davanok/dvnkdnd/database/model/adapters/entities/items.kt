package com.davanok.dvnkdnd.database.model.adapters.entities

import com.davanok.dvnkdnd.data.model.entities.dndEntities.ArmorInfo
import com.davanok.dvnkdnd.data.model.entities.dndEntities.FullItem
import com.davanok.dvnkdnd.data.model.entities.dndEntities.FullWeapon
import com.davanok.dvnkdnd.data.model.entities.dndEntities.ItemProperty
import com.davanok.dvnkdnd.data.model.entities.dndEntities.JoinItemProperty
import com.davanok.dvnkdnd.data.model.entities.dndEntities.WeaponDamageInfo
import com.davanok.dvnkdnd.database.entities.items.DbArmor
import com.davanok.dvnkdnd.database.entities.items.DbItem
import com.davanok.dvnkdnd.database.entities.items.DbItemProperty
import com.davanok.dvnkdnd.database.entities.items.DbItemPropertyLink
import com.davanok.dvnkdnd.database.entities.items.DbWeapon
import com.davanok.dvnkdnd.database.entities.items.DbWeaponDamage
import kotlin.uuid.Uuid


fun ArmorInfo.toDbArmor(itemId: Uuid) = DbArmor(
    id = itemId,
    armorClass = armorClass,
    dexMaxModifier = dexMaxModifier,
    requiredStrength = requiredStrength,
    stealthDisadvantage = stealthDisadvantage
)
fun DbArmor.toArmorInfo() = ArmorInfo(
    armorClass = armorClass,
    dexMaxModifier = dexMaxModifier,
    requiredStrength = requiredStrength,
    stealthDisadvantage = stealthDisadvantage
)
fun ItemProperty.toDbItemProperty() = DbItemProperty(
    id = id,
    userId = userId,
    name = name,
    description = description
)
fun DbItemProperty.toItemProperty() = ItemProperty(
    id = id,
    userId = userId,
    name = name,
    description = description
)
fun WeaponDamageInfo.toDbWeaponDamage(weaponId: Uuid) = DbWeaponDamage(
    id = id,
    weaponId = weaponId,
    damageType = damageType,
    diceCount = diceCount,
    dice = dice,
    modifier = modifier
)
fun DbWeaponDamage.toWeaponDamageInfo() = WeaponDamageInfo(
    id = id,
    damageType = damageType,
    diceCount = diceCount,
    dice = dice,
    modifier = modifier
)

fun FullItem.toDbItem(entityId: Uuid) = DbItem(
    id = entityId,
    cost = cost,
    weight = weight,
    attunement = attunement
)

fun JoinItemProperty.toDbItemPropertyLink() = DbItemPropertyLink(
    itemId = itemId,
    propertyId = propertyId
)

fun FullWeapon.toDbWeapon(entityId: Uuid) = DbWeapon(id = entityId, atkBonus = atkBonus)