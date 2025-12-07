package com.davanok.dvnkdnd.data.local.mappers.entities

import com.davanok.dvnkdnd.domain.entities.dndEntities.ClassWithSpells
import com.davanok.dvnkdnd.domain.entities.dndEntities.SpellSlots
import com.davanok.dvnkdnd.domain.entities.dndEntities.SpellSlotsType
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.concept.DbClass
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.concept.DbClassSpellSlots
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.concept.DbSpellSlotType
import kotlin.uuid.Uuid


fun SpellSlots.toDbClassSpellSlots(classId: Uuid) = DbClassSpellSlots(
    id = id,
    classId = classId,
    level = level,
    preparedSpells = preparedSpells,
    cantrips = cantrips,
    spellSlots = spellSlots,
    typeId = type.id
)

fun ClassWithSpells.toDbClass(entityId: Uuid) = DbClass(
    id = entityId,
    primaryStats = primaryStats,
    hitDice = hitDice,
    caster = caster
)


fun DbSpellSlotType.toSpellSlotType() = SpellSlotsType(
    id = id,
    userId = userId,
    name = name,
    regain = regain,
    spell = spell
)

fun SpellSlotsType.toDbSpellSlotType() = DbSpellSlotType(
    id = id,
    userId = userId,
    name = name,
    regain = regain,
    spell = spell
)