package com.davanok.dvnkdnd.data.local.mappers.entities

import com.davanok.dvnkdnd.data.local.db.entities.items.DbItemActivationCastsSpell
import com.davanok.dvnkdnd.data.local.db.entities.items.DbItemActivationRegain
import com.davanok.dvnkdnd.data.local.db.entities.items.DbItemEffect
import com.davanok.dvnkdnd.data.local.db.model.DbFullItem
import com.davanok.dvnkdnd.data.local.db.model.DbFullItemActivation
import com.davanok.dvnkdnd.data.local.db.model.DbFullWeapon
import com.davanok.dvnkdnd.data.local.db.model.DbWeaponDamageWithCondition
import com.davanok.dvnkdnd.domain.entities.dndEntities.FullItem
import com.davanok.dvnkdnd.domain.entities.dndEntities.FullItemActivation
import com.davanok.dvnkdnd.domain.entities.dndEntities.FullWeapon
import com.davanok.dvnkdnd.domain.entities.dndEntities.ItemActivationCastsSpell
import kotlin.uuid.Uuid

fun DbFullWeapon.toFullWeapon() = FullWeapon(
    atkBonus = weapon.atkBonus,
    damages = damages.map(DbWeaponDamageWithCondition::toWeaponDamageInfo)
)
fun DbFullItemActivation.toFullItemActivation() = FullItemActivation(
    id = activation.id,
    name = activation.name,
    requiresAttunement = activation.requiresAttunement,
    givesState = activation.givesState,
    count = activation.count,
    regains = regains.map(DbItemActivationRegain::toItemActivationRegain),
    castsSpell = castsSpell?.toItemActivationCastsSpell()
)
fun DbFullItem.toFullItem() = FullItem(
    item = item.toItem(),

    effects = effects.map(DbItemEffect::toItemEffect),
    activations = activations.map(DbFullItemActivation::toFullItemActivation),

    properties = properties.map { it.property.toItemProperty() },
    armor = armor?.toArmorInfo(),
    weapon = weapon?.toFullWeapon()
)

fun DbItemActivationCastsSpell.toItemActivationCastsSpell() = ItemActivationCastsSpell(
    spellId = spellId,
    level = level
)
fun ItemActivationCastsSpell.toDbItemActivationCastsSpell(activationId: Uuid) = DbItemActivationCastsSpell(
    id = activationId,
    spellId = spellId,
    level = level
)