package com.davanok.dvnkdnd.domain.entities.character.characterUtils

import com.davanok.dvnkdnd.domain.dnd.DnDConstants
import com.davanok.dvnkdnd.domain.dnd.calculateModifier
import com.davanok.dvnkdnd.domain.entities.character.CharacterAttack
import com.davanok.dvnkdnd.domain.entities.character.CharacterFull
import com.davanok.dvnkdnd.domain.enums.dndEnums.Attributes
import com.davanok.dvnkdnd.domain.enums.dndEnums.ItemPropertyType

fun CharacterFull.findAttacks(): List<CharacterAttack> {
    // 1. Pre-calculate Modifiers
    val strMod = calculateModifier(appliedValues.attributes[Attributes.STRENGTH])
    val dexMod = calculateModifier(appliedValues.attributes[Attributes.DEXTERITY])

    // 2. Pre-calculate Proficiencies (Performance Fix)
    // We do this once outside the loop instead of for every item.
    val characterPropertyProficiencies = entitiesWithLevel
        .flatMap { (entity, level) ->
            entity.proficiencies
                .filter { it.level <= level && it.proficiency.id in selectedProficiencies }
                .map { it.proficiency.itemPropertyId } // Map directly to the ID we need
        }
        .toSet()

    return items.filter { it.equipped }.mapNotNull { characterItem ->
        val weapon = characterItem.item.item?.weapon ?: return@mapNotNull null
        val properties = characterItem.item.item.properties

        // 3. Identify Properties
        val isFinesse = properties.any { it.type == ItemPropertyType.FINESSE }
        val isRangedAmmo = properties.any { it.type == ItemPropertyType.AMMUNITION }
        val isThrown = properties.any { it.type == ItemPropertyType.THROWN }
        val reachProp = properties.firstOrNull { it.type == ItemPropertyType.REACH }

        // 4. Determine Ability Modifier (Rule Fix)
        // Ranged (Bows) use Dex.
        // Finesse uses the higher of Str or Dex.
        // Thrown uses Str (unless it is also Finesse, which is covered by the Finesse check).
        // Standard Melee uses Str.
        val useDex = isRangedAmmo || (isFinesse && dexMod > strMod)
        val primaryAttrMod = if (useDex) dexMod else strMod

        // 5. Calculate Range
        // If it is Ranged/Thrown, parse the value. If Reach, add modifier. Else default 5ft.
        val rangeValues = when {
            isRangedAmmo || isThrown -> {
                val propValue = properties.firstOrNull {
                    it.type == ItemPropertyType.AMMUNITION || it.type == ItemPropertyType.THROWN
                }?.value

                propValue?.split('/')
                    ?.mapNotNull { it.trim().toIntOrNull() }
                    ?.ifEmpty { null }
                    ?: DnDConstants.RANGED_ITEM_DEFAULT_RANGE
            }
            reachProp != null -> {
                val reachMod = reachProp.value?.toIntOrNull() ?: DnDConstants.REACH_ITEM_DEFAULT_RANGE_MODIFIER
                listOf(DnDConstants.ITEM_DEFAULT_RANGE + reachMod)
            }
            else -> listOf(DnDConstants.ITEM_DEFAULT_RANGE)
        }

        // 6. Calculate Hit Bonus
        val itemPropertyIds = properties.map { it.id }.toSet()

        // Check intersection between character proficiencies and item properties
        val isProficient = characterPropertyProficiencies.any { it in itemPropertyIds }

        val attackBonus = primaryAttrMod + weapon.atkBonus + if (isProficient) proficiencyBonus else 0

        // 7. Damage Logic
        val damageEntries = weapon.damages.map { it.copy(modifier = it.modifier + primaryAttrMod) }

        CharacterAttack(
            rangeValues = rangeValues,
            attackBonus = attackBonus,
            damages = damageEntries,
            source = characterItem
        )
    }
}