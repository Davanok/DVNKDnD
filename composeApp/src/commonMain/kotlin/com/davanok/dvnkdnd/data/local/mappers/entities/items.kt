package com.davanok.dvnkdnd.data.local.mappers.entities

import com.davanok.dvnkdnd.domain.entities.dndEntities.ArmorInfo
import com.davanok.dvnkdnd.domain.entities.dndEntities.FullWeapon
import com.davanok.dvnkdnd.domain.entities.dndEntities.Item
import com.davanok.dvnkdnd.domain.entities.dndEntities.ItemProperty
import com.davanok.dvnkdnd.domain.entities.dndEntities.WeaponDamageInfo
import com.davanok.dvnkdnd.data.local.db.entities.items.DbArmor
import com.davanok.dvnkdnd.data.local.db.entities.items.DbItem
import com.davanok.dvnkdnd.data.local.db.entities.items.DbItemActivation
import com.davanok.dvnkdnd.data.local.db.entities.items.DbItemActivationRegain
import com.davanok.dvnkdnd.data.local.db.entities.items.DbItemEffect
import com.davanok.dvnkdnd.data.local.db.entities.items.DbItemProperty
import com.davanok.dvnkdnd.data.local.db.entities.items.DbWeapon
import com.davanok.dvnkdnd.data.local.db.entities.items.DbWeaponDamage
import com.davanok.dvnkdnd.domain.entities.dndEntities.FullItemActivation
import com.davanok.dvnkdnd.domain.entities.dndEntities.ItemActivationRegain
import com.davanok.dvnkdnd.domain.entities.dndEntities.ItemEffect
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

fun Item.toDbItem(entityId: Uuid) = DbItem(
    id = entityId,
    cost = cost,
    weight = weight
)
fun DbItem.toItem() = Item(
    cost = cost,
    weight = weight
)

fun FullWeapon.toDbWeapon(entityId: Uuid) = DbWeapon(id = entityId, atkBonus = atkBonus)

fun ItemEffect.toDbItemEffect(itemId: Uuid) = DbItemEffect(
    id = id,
    itemId = itemId,
    scope = scope,
    givesState = givesState
)
fun DbItemEffect.toItemEffect() = ItemEffect(
    id = id,
    scope = scope,
    givesState = givesState
)

fun FullItemActivation.toDbItemActivation(itemId: Uuid) = DbItemActivation(
    id = id,
    itemId = itemId,
    requiresAttunement = requiresAttunement,
    givesState = givesState,
    count = count
)

fun ItemActivationRegain.toDbItemActivationRegain(activationId: Uuid) = DbItemActivationRegain(
    id = id,
    activationId = activationId,
    regainsCount = regainsCount,
    timeUnit = timeUnit,
    timeUnitCount = timeUnitCount
)

fun DbItemActivationRegain.toItemActivationRegain() = ItemActivationRegain(
    id = id,
    regainsCount = regainsCount,
    timeUnit = timeUnit,
    timeUnitCount = timeUnitCount
)