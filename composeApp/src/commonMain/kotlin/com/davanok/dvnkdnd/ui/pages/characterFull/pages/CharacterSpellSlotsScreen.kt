package com.davanok.dvnkdnd.ui.pages.characterFull.pages

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.davanok.dvnkdnd.domain.entities.dndEntities.SpellSlotsType
import com.davanok.dvnkdnd.ui.components.adaptive.alternativeClickable
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.multiclass_spell_slot_type_name
import dvnkdnd.composeapp.generated.resources.spell_level
import org.jetbrains.compose.resources.stringResource
import kotlin.uuid.Uuid

@Composable
fun CharacterSpellSlotsScreen(
    availableSpellSlots: Map<SpellSlotsType?, IntArray>,
    usedSpells: Map<Uuid?, IntArray>,
    setUsedSpellsCount: (typeId: Uuid?, lvl: Int, count: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val preparedSpellSlots by remember(availableSpellSlots) {
        derivedStateOf {
            val namesCount = mutableMapOf<String, Int>()
            availableSpellSlots
                .mapKeys { (type, _) ->
                    if (type == null) null
                    else {
                        val name = type.name
                        val count = namesCount.getOrElse(name) { 0 }
                        namesCount[name] = count + 1
                        if (count == 0) type
                        else type.copy(name = "$name ($count)")
                    }
                }
        }
    }

    LazyColumn(
        modifier = modifier
    ) {
        preparedSpellSlots.forEach { (spellSlotType, spellSlots) ->
            val usedSpellsOnSlot = usedSpells.getOrElse(spellSlotType?.id, ::intArrayOf)

            item(
                key = "type: ${spellSlotType?.id}",
                contentType = "spell_slot_type"
            ) {
                Text(
                    text = spellSlotType?.name ?: stringResource(Res.string.multiclass_spell_slot_type_name),
                    style = MaterialTheme.typography.titleMedium
                )
            }

            spellSlots.forEachIndexed { index, slotCount ->
                val typeId = spellSlotType?.id
                val level = index + 1
                val activeSlotsCount = slotCount - usedSpellsOnSlot.getOrElse(index) { 0 }
                item(
                    key = "slot: $typeId - $index"
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .alternativeClickable(
                                onClick = {
                                    val currentCount = usedSpells[typeId]?.get(index) ?: 0
                                    setUsedSpellsCount(typeId, level, currentCount + 1)
                                },
                                onAlternativeClick = {
                                    val currentCount = usedSpells[typeId]?.get(index) ?: 0
                                    setUsedSpellsCount(typeId, level, currentCount - 1)
                                }
                            )
                    ) {
                        Text(text = stringResource(Res.string.spell_level, level))

                        repeat(slotCount) { slotIndex ->
                            RadioButton(
                                selected = slotIndex < activeSlotsCount,
                                onClick = null
                            )
                        }
                    }
                }
            }
        }
    }
}