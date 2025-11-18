package com.davanok.dvnkdnd.database.model.adapters.entities

import com.davanok.dvnkdnd.data.model.entities.dndEntities.ClassWithSpells
import com.davanok.dvnkdnd.data.model.entities.dndEntities.SpellSlots
import com.davanok.dvnkdnd.data.model.entities.dndEntities.SpellSlotsType
import com.davanok.dvnkdnd.database.entities.dndEntities.concept.ClassSpellSlots
import com.davanok.dvnkdnd.database.entities.dndEntities.concept.DbSpellSlotType
import com.davanok.dvnkdnd.database.entities.dndEntities.concept.DnDClass
import kotlin.uuid.Uuid


fun SpellSlots.toClassSpellSlots(classId: Uuid) = ClassSpellSlots(
    id = id,
    classId = classId,
    level = level,
    preparedSpells = preparedSpells,
    cantrips = cantrips,
    spellSlots = spellSlots,
    typeId = type.id
)

fun ClassWithSpells.toDnDClass(entityId: Uuid) = DnDClass(
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