package com.davanok.dvnkdnd.data.model.entities.character.characterUtils

import com.davanok.dvnkdnd.data.model.dndEnums.DnDEntityTypes
import com.davanok.dvnkdnd.data.model.entities.character.CharacterFull
import com.davanok.dvnkdnd.data.model.entities.dndEntities.SpellSlotsType
import com.davanok.dvnkdnd.data.model.util.DnDConstants
import com.davanok.dvnkdnd.data.model.util.resolveCasterSpellSlotsContribution

fun CharacterFull.calculateSpellSlots(): Map<SpellSlotsType, IntArray> {
    val classes = mainEntities
        .filter { it.entity.entity.type == DnDEntityTypes.CLASS && (it.entity.cls != null || it.subEntity?.cls != null) }
    if (classes.isEmpty()) return emptyMap()

    val resultByType = mutableMapOf<SpellSlotsType, Pair<Int, IntArray>>()
    var effectiveCasterLevel = 0

    classes.forEach { mainEntity ->
        val sub = mainEntity.subEntity?.cls
        val ent = mainEntity.entity.cls
        val cls =  if (!sub?.slots.isNullOrEmpty()) sub else ent ?: return@forEach
        val slots = cls.slots

        if (slots.isEmpty()) {
            effectiveCasterLevel += resolveCasterSpellSlotsContribution(mainEntity.level, cls.caster)
            return@forEach
        }

        if (slots.any { it.type.id == SpellSlotsType.Multiclass.id }) {
            effectiveCasterLevel += resolveCasterSpellSlotsContribution(mainEntity.level, cls.caster)
        }

        slots.groupBy { it.type }.forEach { (typeId, list) ->
            val chosen = list.sortedBy { it.level }.lastOrNull { it.level <= mainEntity.level }
            chosen?.let {
                val existing = resultByType[typeId]
                if (existing == null || it.level > existing.first) {
                    resultByType[typeId] = it.level to it.spellSlots.toIntArray()
                }
            }
        }
    }

    val multiclassSlots = when {
        effectiveCasterLevel <= 0 -> IntArray(0)
        effectiveCasterLevel < DnDConstants.MULTICLASS_SPELL_SLOTS_BY_LEVEL.size ->
            DnDConstants.MULTICLASS_SPELL_SLOTS_BY_LEVEL[effectiveCasterLevel]
        else -> DnDConstants.MULTICLASS_SPELL_SLOTS_BY_LEVEL.last()
    }

    val resultSpellSlots = resultByType.mapValues { it.value.second }.toMutableMap()
    resultSpellSlots.keys.firstOrNull { it.id == SpellSlotsType.Multiclass.id }.let {
        if (it == null) resultSpellSlots[SpellSlotsType.Multiclass] = multiclassSlots
        else resultSpellSlots[it] = multiclassSlots
    }

    return resultSpellSlots
}