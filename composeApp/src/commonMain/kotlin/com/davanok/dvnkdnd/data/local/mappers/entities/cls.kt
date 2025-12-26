package com.davanok.dvnkdnd.data.local.mappers.entities

import com.davanok.dvnkdnd.data.local.db.model.DbClassSpellSlotWithType
import com.davanok.dvnkdnd.data.local.db.model.DbClassWithSpells
import com.davanok.dvnkdnd.domain.entities.dndEntities.ClassWithSpells
import com.davanok.dvnkdnd.domain.entities.dndEntities.SpellSlots

fun DbClassSpellSlotWithType.toSpellSlots() = SpellSlots(
    id = slot.id,
    level = slot.level,
    preparedSpells = slot.preparedSpells,
    cantrips = slot.cantrips,
    spellSlots = slot.spellSlots,
    type = type.toSpellSlotType()
)

fun DbClassWithSpells.toClassWithSpells() = ClassWithSpells(
    primaryStats = cls.primaryStats,
    hitDice = cls.hitDice,
    caster = cls.caster,
    spells = spells,
    slots = slots.map(DbClassSpellSlotWithType::toSpellSlots),
)