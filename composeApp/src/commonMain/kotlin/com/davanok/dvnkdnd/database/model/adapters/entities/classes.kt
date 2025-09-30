package com.davanok.dvnkdnd.database.model.adapters.entities

import com.davanok.dvnkdnd.data.model.entities.dndEntities.ClassWithSpells
import com.davanok.dvnkdnd.data.model.entities.dndEntities.SpellSlots
import com.davanok.dvnkdnd.database.entities.dndEntities.concept.ClassSpellSlots
import com.davanok.dvnkdnd.database.entities.dndEntities.concept.DnDClass
import kotlin.uuid.Uuid


fun SpellSlots.toClassSpellSlots(classId: Uuid) = ClassSpellSlots(
    id = id,
    classId = classId,
    level = level,
    preparedSpells = preparedSpells,
    cantrips = cantrips,
    spellSlots = spellSlots
)
fun ClassSpellSlots.toSpellSlots() = SpellSlots(
    id = id,
    level = level,
    preparedSpells = preparedSpells,
    cantrips = cantrips,
    spellSlots = spellSlots
)

fun ClassWithSpells.toDnDClass(entityId: Uuid) = DnDClass(entityId, primaryStats, hitDice)