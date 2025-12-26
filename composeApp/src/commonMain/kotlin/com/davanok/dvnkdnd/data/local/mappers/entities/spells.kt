package com.davanok.dvnkdnd.data.local.mappers.entities

import com.davanok.dvnkdnd.domain.entities.dndEntities.FullSpellAttack
import com.davanok.dvnkdnd.domain.entities.dndEntities.Spell
import com.davanok.dvnkdnd.domain.entities.dndEntities.SpellAreaInfo
import com.davanok.dvnkdnd.domain.entities.dndEntities.SpellAttackLevelModifierInfo
import com.davanok.dvnkdnd.domain.entities.dndEntities.SpellAttackSaveInfo
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.DbSpell
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.DbSpellArea
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.DbSpellAttack
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.DbSpellAttackLevelModifier
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.DbSpellAttackSave
import com.davanok.dvnkdnd.data.local.db.model.DbFullSpell
import com.davanok.dvnkdnd.data.local.db.model.DbFullSpellAttack
import com.davanok.dvnkdnd.domain.entities.dndEntities.FullSpell
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


fun Spell.toDbSpell(entityId: Uuid) = DbSpell(
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
fun DbSpell.toSpell() = Spell(
    school = school,
    level = level,
    castingTime = castingTime,
    castingTimeOther = castingTimeOther,
    components = components.toSet(),
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
    modifier = modifier,
    givesState = givesState
)

fun DbFullSpellAttack.toFullSpellAttack() = FullSpellAttack(
    id = attack.id,
    damageType = attack.damageType,
    diceCount = attack.diceCount,
    dice = attack.dice,
    modifier = attack.modifier,
    givesState = attack.givesState,
    levelModifiers = levelModifiers.map(DbSpellAttackLevelModifier::toSpellAttackLevelModifierInfo),
    save = save?.toSpellAttackSaveInfo()
)
fun DbFullSpell.toFullSpell() = FullSpell(
    spell = spell.toSpell(),
    area = area?.toSpellAreaInfo(),
    attacks = attacks.map(DbFullSpellAttack::toFullSpellAttack)
)
