package com.davanok.dvnkdnd.data.model.entities.character

import androidx.compose.ui.util.fastFlatMap
import androidx.compose.ui.util.fastMap
import com.davanok.dvnkdnd.data.model.entities.dndEntities.DnDEntityWithModifiers
import com.davanok.dvnkdnd.data.model.entities.dndEntities.DnDFullEntity
import com.davanok.dvnkdnd.data.model.entities.dndModifiers.DnDModifiersGroup
import kotlin.uuid.Uuid

data class CharacterWithAllModifiers(
    val character: CharacterMin,
    val characterStats: DnDModifiersGroup?,

    val selectedModifierBonuses: List<Uuid>,
    val classes: List<DnDEntityWithModifiers>,
    val race: DnDEntityWithModifiers?,
    val subRace: DnDEntityWithModifiers?,
    val background: DnDEntityWithModifiers?,
    val subBackground: DnDEntityWithModifiers?,
)

fun CharacterFull.toCharacterWithAllModifiers() = CharacterWithAllModifiers(
    character = character,
    characterStats = stats,
    selectedModifierBonuses = selectedModifierBonuses,
    classes = classes.fastFlatMap {
        listOfNotNull(it.cls, it.subCls)
            .fastMap { e -> e.toEntityWithModifiers() }
                                  },
    race = race?.toEntityWithModifiers(),
    subRace = subRace?.toEntityWithModifiers(),
    background = background?.toEntityWithModifiers(),
    subBackground = subBackground?.toEntityWithModifiers()
)

fun DnDFullEntity.toEntityWithModifiers() = DnDEntityWithModifiers(
    entity = toDnDEntityMin(),
    selectionLimit = selectionLimits?.modifiers,
    modifiers = modifierBonuses
)