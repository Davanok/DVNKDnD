package com.davanok.dvnkdnd.data.local.mappers.entities

import com.davanok.dvnkdnd.data.local.db.entities.items.DbItemActivationRegain
import com.davanok.dvnkdnd.data.local.db.entities.items.DbItemEffect
import com.davanok.dvnkdnd.data.local.db.entities.items.DbWeaponDamage
import com.davanok.dvnkdnd.data.local.db.model.DbFullItem
import com.davanok.dvnkdnd.data.local.db.model.DbFullItemActivation
import com.davanok.dvnkdnd.data.local.db.model.DbFullWeapon
import com.davanok.dvnkdnd.domain.entities.dndEntities.FullItem
import com.davanok.dvnkdnd.domain.entities.dndEntities.FullItemActivation
import com.davanok.dvnkdnd.domain.entities.dndEntities.FullWeapon

fun DbFullWeapon.toFullWeapon() = FullWeapon(
    atkBonus = weapon.atkBonus,
    damages = damages.map(DbWeaponDamage::toWeaponDamageInfo)
)
fun DbFullItemActivation.toFullItemActivation() = FullItemActivation(
    id = activation.id,
    requiresAttunement = activation.requiresAttunement,
    givesState = activation.givesState,
    count = activation.count,
    regains = regains.map(DbItemActivationRegain::toItemActivationRegain)
)
fun DbFullItem.toFullItem() = FullItem(
    item = item.toItem(),

    effects = effects.map(DbItemEffect::toItemEffect),
    activations = activations.map(DbFullItemActivation::toFullItemActivation),

    properties = properties.map { it.property.toItemProperty() },
    armor = armor?.toArmorInfo(),
    weapon = weapon?.toFullWeapon()
)