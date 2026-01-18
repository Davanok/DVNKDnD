package com.davanok.dvnkdnd.core.utils

import com.davanok.dvnkdnd.domain.entities.dndEntities.SpellSlotsType

fun renameSpellSlots(
    slots: Map<SpellSlotsType?, IntArray>
): Map<SpellSlotsType?, IntArray> {
    val namesCount = mutableMapOf<String, Int>()

    return slots.mapKeys { (type, _) ->
        type?.let {
            val count = namesCount.getOrPut(it.name) { 0 }
            namesCount[it.name] = count + 1

            if (count == 0) it
            else it.copy(name = "${it.name} ($count)")
        }
    }
}