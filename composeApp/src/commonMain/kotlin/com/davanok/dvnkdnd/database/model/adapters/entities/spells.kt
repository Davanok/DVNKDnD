package com.davanok.dvnkdnd.database.model.adapters.entities

import com.davanok.dvnkdnd.data.model.entities.dndEntities.FullSpell
import com.davanok.dvnkdnd.data.model.entities.dndEntities.FullSpellAttack
import com.davanok.dvnkdnd.data.model.entities.dndEntities.SpellAreaInfo
import com.davanok.dvnkdnd.data.model.entities.dndEntities.SpellAttackLevelModifierInfo
import com.davanok.dvnkdnd.data.model.entities.dndEntities.SpellAttackSaveInfo
import com.davanok.dvnkdnd.database.entities.dndEntities.DbSpell
import com.davanok.dvnkdnd.database.entities.dndEntities.DbSpellArea
import com.davanok.dvnkdnd.database.entities.dndEntities.DbSpellAttack
import com.davanok.dvnkdnd.database.entities.dndEntities.DbSpellAttackLevelModifier
import com.davanok.dvnkdnd.database.entities.dndEntities.DbSpellAttackSave
import kotlin.uuid.Uuid


fun DbSpellArea.toSpellAreaInfo() = SpellAreaInfo(
    range = range,
    type = type,
    width = width,
    height = height
)

fun SpellAreaInfo.toDbSpellArea(entityId: Uuid) = DbSpellArea(
    id = entityId,
    range = range,
    type = type,
    width = width,
    height = height
)

fun DbSpellAttackSave.toSpellAttackSaveInfo() = SpellAttackSaveInfo(
    savingThrow = savingThrow,
    halfOnSuccess = halfOnSuccess
)

fun SpellAttackSaveInfo.toDbSpellAttackSave(attackId: Uuid) = DbSpellAttackSave(
    id = attackId,
    savingThrow = savingThrow,
    halfOnSuccess = halfOnSuccess
)

fun DbSpellAttackLevelModifier.toSpellAttackLevelModifierInfo() = SpellAttackLevelModifierInfo(
    id = id,
    level = level,
    diceCount = diceCount,
    dice = dice,
    modifier = modifier
)

fun SpellAttackLevelModifierInfo.toDbSpellAttackLevelModifier(attackId: Uuid) =
    DbSpellAttackLevelModifier(
        id = id,
        attackId = attackId,
        level = level,
        diceCount = diceCount,
        dice = dice,
        modifier = modifier
    )


fun FullSpell.toDbSpell(entityId: Uuid) = DbSpell(
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

fun FullSpellAttack.toDbSpellAttack(spellId: Uuid) = DbSpellAttack(
    id = id,
    spellId = spellId,
    damageType = damageType,
    diceCount = diceCount,
    dice = dice,
    modifier = modifier
)
