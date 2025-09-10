package com.davanok.dvnkdnd.data.model.entities.character

import com.davanok.dvnkdnd.data.model.entities.DatabaseImage
import com.davanok.dvnkdnd.data.model.entities.dndEntities.DnDFullEntity
import com.davanok.dvnkdnd.data.model.entities.dndModifiers.DnDAttributesGroup
import kotlin.uuid.Uuid

data class CharacterFull(
    val character: CharacterMin,

    val images: List<DatabaseImage>,
    val coins: CoinsGroup?,

    val stats: DnDAttributesGroup?,
    val health: DnDCharacterHealth?,
    val usedSpells: List<Int>,

    val classes: List<CharacterClassInfo>,
    val race: DnDFullEntity?,
    val subRace: DnDFullEntity?,
    val background: DnDFullEntity?,
    val subBackground: DnDFullEntity?,

    val feats: List<DnDFullEntity>,

    val selectedModifiers: List<Uuid>,
    val selectedProficiencies: List<Uuid>
)
data class CharacterClassInfo(
    val level: Int,
    val cls: DnDFullEntity,
    val subCls: DnDFullEntity?
)
