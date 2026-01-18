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
import com.davanok.dvnkdnd.core.utils.renameSpellSlots
import com.davanok.dvnkdnd.domain.entities.dndEntities.SpellSlotsType
import com.davanok.dvnkdnd.ui.components.FullScreenCard
import com.davanok.dvnkdnd.ui.components.adaptive.alternativeClickable
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.character_has_no_spell_slots
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
    if (availableSpellSlots.isEmpty())
        FullScreenCard(modifier = modifier) {
            Text(text = stringResource(Res.string.character_has_no_spell_slots))
        }
    else
        CharacterSpellSlotsScreenContent(
            availableSpellSlots = availableSpellSlots,
            usedSpells = usedSpells,
            setUsedSpellsCount = setUsedSpellsCount,
            modifier = modifier
        )
}

@Composable
private fun CharacterSpellSlotsScreenContent(
    availableSpellSlots: Map<SpellSlotsType?, IntArray>,
    usedSpells: Map<Uuid?, IntArray>,
    setUsedSpellsCount: (typeId: Uuid?, lvl: Int, count: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val preparedSpellSlots by remember(availableSpellSlots) {
        derivedStateOf { renameSpellSlots(availableSpellSlots) }
    }

    LazyColumn(
        modifier = modifier
    ) {
        preparedSpellSlots.forEach { (spellSlotType, spellSlots) ->
            val typeId = spellSlotType?.id
            val usedSpellsOnSlot = usedSpells.getOrElse(typeId, ::intArrayOf)

            item(
                key = "type: $typeId",
                contentType = "spell_slot_type"
            ) {
                Text(
                    text = spellSlotType?.name ?: stringResource(Res.string.multiclass_spell_slot_type_name),
                    style = MaterialTheme.typography.titleMedium
                )
            }

            spellSlots.forEachIndexed { index, slotCount ->
                val level = index + 1
                val usedCount = usedSpellsOnSlot.getOrElse(index) { 0 }
                val activeSlotsCount = slotCount - usedCount

                item(
                    key = "slot: $typeId - $index"
                ) {
                    SpellSlotsItem(
                        slotCount = slotCount,
                        activeSlotsCount = activeSlotsCount,
                        slotLevel = level,
                        onIncrementClick = {
                            setUsedSpellsCount(typeId, level, usedCount + 1)
                        },
                        onDecrementClick = {
                            setUsedSpellsCount(typeId, level, usedCount - 1)
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}


@Composable
private fun SpellSlotsItem(
    slotCount: Int,
    activeSlotsCount: Int,
    slotLevel: Int,
    onIncrementClick: () -> Unit,
    onDecrementClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .alternativeClickable(
                onClick = onIncrementClick,
                onAlternativeClick = onDecrementClick
            )
    ) {
        Text(text = stringResource(Res.string.spell_level, slotLevel))

        repeat(slotCount) { slotIndex ->
            RadioButton(
                selected = slotIndex < activeSlotsCount,
                onClick = null
            )
        }
    }
}