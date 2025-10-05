package com.davanok.dvnkdnd.data.model.entities.character

import androidx.compose.ui.util.fastFlatMap
import com.davanok.dvnkdnd.data.model.entities.DatabaseImage
import com.davanok.dvnkdnd.data.model.entities.dndEntities.DnDFullEntity
import com.davanok.dvnkdnd.data.model.entities.dndModifiers.DnDAttributesGroup
import com.davanok.dvnkdnd.data.model.entities.dndModifiers.DnDSkillsGroup
import com.davanok.dvnkdnd.data.model.entities.dndModifiers.toAttributesGroup
import com.davanok.dvnkdnd.data.model.entities.dndModifiers.toSkillsGroup
import com.davanok.dvnkdnd.data.model.util.calculateModifier
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlin.uuid.Uuid

@Serializable
data class CharacterFull(
    val character: CharacterBase,

    @Transient
    val images: List<DatabaseImage> = emptyList(),
    val coins: CoinsGroup,

    val attributes: DnDAttributesGroup = DnDAttributesGroup.Default,
    @Transient
    val savingThrows: DnDAttributesGroup = attributes
        .toMap()
        .mapValues { (_, value) -> calculateModifier(value) }
        .toAttributesGroup(),
    @Transient
    val skillsThrows: DnDSkillsGroup = attributes
        .toMap()
        .mapValues { (_, value) -> calculateModifier(value) }
        .toAttributesGroup()
        .toSkillsGroup(),
    val health: DnDCharacterHealth,
    val usedSpells: List<Int>,

    val mainEntities: List<CharacterMainEntityInfo>,

    val feats: List<DnDFullEntity>,

    val selectedModifiers: List<Uuid>,
    val selectedProficiencies: List<Uuid>,

    val customModifiers: List<CustomModifier>
) {
    val entities: List<DnDFullEntity>
        get() = mainEntities.fastFlatMap { listOfNotNull(it.entity, it.subEntity) } + feats
}

@Serializable
data class CharacterMainEntityInfo(
    val level: Int,
    val entity: DnDFullEntity,
    val subEntity: DnDFullEntity?
)
