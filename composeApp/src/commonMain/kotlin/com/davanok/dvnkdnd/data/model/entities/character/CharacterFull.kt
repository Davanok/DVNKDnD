package com.davanok.dvnkdnd.data.model.entities.character

import androidx.compose.ui.util.fastFlatMap
import com.davanok.dvnkdnd.data.model.entities.DatabaseImage
import com.davanok.dvnkdnd.data.model.entities.dndEntities.DnDFullEntity
import com.davanok.dvnkdnd.data.model.entities.dndModifiers.DnDAttributesGroup
import com.davanok.dvnkdnd.data.model.entities.dndModifiers.DnDSkillsGroup
import com.davanok.dvnkdnd.data.model.entities.dndModifiers.toSkillsGroup
import com.davanok.dvnkdnd.data.model.util.proficiencyBonusByLevel
import kotlin.uuid.Uuid

data class CharacterFull(
    val character: CharacterMin,

    val images: List<DatabaseImage>,
    val coins: CoinsGroup?,

    val attributes: DnDAttributesGroup = DnDAttributesGroup.Default,
//    @Transient
    val skills: DnDSkillsGroup = attributes.toSkillsGroup(),
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
) {
    val entities: List<DnDFullEntity>
        get() = classes.fastFlatMap { listOfNotNull(it.cls, it.subCls) } +
                listOfNotNull(race, subRace, background, subBackground) + feats
    val proficiencyBonus: Int
        get() = proficiencyBonusByLevel(character.level)
}


data class CharacterClassInfo(
    val level: Int,
    val cls: DnDFullEntity,
    val subCls: DnDFullEntity?
)
