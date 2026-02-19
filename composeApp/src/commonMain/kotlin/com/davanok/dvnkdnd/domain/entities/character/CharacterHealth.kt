package com.davanok.dvnkdnd.domain.entities.character

import com.davanok.dvnkdnd.domain.enums.dndEnums.DnDModifierHealthTargets
import kotlinx.serialization.Serializable

@Serializable
data class CharacterHealth(
    val max: Int = 0,
    val current: Int = max,
    val temp: Int = 0
)

fun CharacterHealth.toMap(): Map<DnDModifierHealthTargets, Int> = mapOf(
    DnDModifierHealthTargets.CURRENT to current,
    DnDModifierHealthTargets.MAX to max,
    DnDModifierHealthTargets.TEMP to temp
)
fun Map<DnDModifierHealthTargets, Int>.toCharacterHealth() = CharacterHealth(
    max = get(DnDModifierHealthTargets.MAX) ?: 0,
    current = get(DnDModifierHealthTargets.CURRENT) ?: 0,
    temp = get(DnDModifierHealthTargets.TEMP) ?: 0
)
