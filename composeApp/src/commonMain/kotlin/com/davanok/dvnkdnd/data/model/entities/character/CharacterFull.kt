package com.davanok.dvnkdnd.data.model.entities.character

import androidx.compose.ui.util.fastFlatMap
import com.davanok.dvnkdnd.data.model.entities.DatabaseImage
import com.davanok.dvnkdnd.data.model.entities.dndEntities.DnDFullEntity
import com.davanok.dvnkdnd.data.model.entities.dndModifiers.DnDAttributesGroup
import com.davanok.dvnkdnd.data.model.entities.dndModifiers.DnDSkillsGroup
import com.davanok.dvnkdnd.data.model.entities.dndModifiers.toSkillsGroup
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlin.uuid.Uuid

@Serializable
data class CharacterFull(
    val character: CharacterMin,

    @Transient
    val images: List<DatabaseImage> = emptyList(),
    val coins: CoinsGroup?,

    val attributes: DnDAttributesGroup = DnDAttributesGroup.Default,
    @Transient
    val skills: DnDSkillsGroup = attributes.toSkillsGroup(),
    val health: DnDCharacterHealth?,
    val usedSpells: List<Int>,

    val mainEntities: List<CharacterMainEntityInfo>,

    val feats: List<DnDFullEntity>,

    val selectedModifiers: List<Uuid>,
    val selectedProficiencies: List<Uuid>
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
