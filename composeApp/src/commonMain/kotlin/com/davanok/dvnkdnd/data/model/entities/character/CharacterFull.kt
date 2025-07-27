package com.davanok.dvnkdnd.data.model.entities.character

import com.davanok.dvnkdnd.data.model.entities.DatabaseImage
import com.davanok.dvnkdnd.data.model.entities.dndEntities.DnDFullEntity
import com.davanok.dvnkdnd.data.model.entities.dndModifiers.DnDModifiersGroup
import kotlin.uuid.Uuid

data class CharacterFull(
    val character: CharacterMin,

    val images: List<DatabaseImage>,
    val coins: CoinsGroup?,

    val stats: DnDModifiersGroup?,
    val health: DnDCharacterHealth?,
    val usedSpells: List<Int>,

    val classes: List<DnDFullEntity>,
    val race: DnDFullEntity?,
    val subRace: DnDFullEntity?,
    val background: DnDFullEntity?,
    val subBackground: DnDFullEntity?,

    val feats: List<DnDFullEntity>,

    val selectedModifierBonuses: List<Uuid>,
    val selectedSkills: List<Uuid>,
    val selectedProficiencies: List<Uuid>
)
