package com.davanok.dvnkdnd.database.model.adapters.entities

import com.davanok.dvnkdnd.data.model.entities.dndEntities.FullSpell
import com.davanok.dvnkdnd.data.model.entities.dndEntities.FullSpellAttack
import com.davanok.dvnkdnd.data.model.entities.dndEntities.SpellAreaInfo
import com.davanok.dvnkdnd.data.model.entities.dndEntities.SpellAttackLevelModifierInfo
import com.davanok.dvnkdnd.data.model.entities.dndEntities.SpellAttackSaveInfo
import com.davanok.dvnkdnd.database.entities.dndEntities.DnDSpell
import com.davanok.dvnkdnd.database.entities.dndEntities.SpellArea
import com.davanok.dvnkdnd.database.entities.dndEntities.SpellAttack
import com.davanok.dvnkdnd.database.entities.dndEntities.SpellAttackLevelModifier
import com.davanok.dvnkdnd.database.entities.dndEntities.SpellAttackSave
import kotlin.uuid.Uuid


fun SpellArea.toSpellAreaInfo() = SpellAreaInfo(
    range = range,
    type = type,
    width = width,
    height = height
)

fun SpellAreaInfo.toSpellArea(entityId: Uuid) = SpellArea(
    id = entityId,
    range = range,
    type = type,
    width = width,
    height = height
)

fun SpellAttackSave.toSpellAttackSaveInfo() = SpellAttackSaveInfo(
    savingThrow = savingThrow,
    halfOnSuccess = halfOnSuccess
)

fun SpellAttackSaveInfo.toSpellAttackSave(attackId: Uuid) = SpellAttackSave(
    id = attackId,
    savingThrow = savingThrow,
    halfOnSuccess = halfOnSuccess
)

fun SpellAttackLevelModifier.toSpellAttackLevelModifierInfo() = SpellAttackLevelModifierInfo(
    id = id,
    level = level,
    diceCount = diceCount,
    dice = dice,
    modifier = modifier
)

fun SpellAttackLevelModifierInfo.toSpellAttackLevelModifier(attackId: Uuid) =
    SpellAttackLevelModifier(
        id = id,
        attackId = attackId,
        level = level,
        diceCount = diceCount,
        dice = dice,
        modifier = modifier
    )


fun FullSpell.toDnDSpell(entityId: Uuid) = DnDSpell(
    id = entityId,
    school = school,
    level = level,
    castingTime = castingTime,
    castingTimeOther = castingTimeOther,
    components = components.toList(),
    ritual = ritual,
    materialComponent = materialComponent,
    duration = duration,
    concentration = concentration
)

fun FullSpellAttack.toSpellAttack(spellId: Uuid) = SpellAttack(
    id, spellId, damageType, diceCount, dice, modifier
)
