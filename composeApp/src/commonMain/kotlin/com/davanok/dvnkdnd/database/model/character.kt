package com.davanok.dvnkdnd.database.model

import androidx.compose.ui.util.fastMap
import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.davanok.dvnkdnd.data.model.entities.DatabaseImage
import com.davanok.dvnkdnd.data.model.entities.character.CharacterMainEntityInfo
import com.davanok.dvnkdnd.data.model.entities.character.CharacterFull
import com.davanok.dvnkdnd.data.model.entities.character.CharacterMin
import com.davanok.dvnkdnd.data.model.entities.character.toDnDCharacterHealth
import com.davanok.dvnkdnd.data.model.entities.character.toDnDCoinsGroup
import com.davanok.dvnkdnd.data.model.entities.dndModifiers.DnDAttributesGroup
import com.davanok.dvnkdnd.data.model.entities.dndModifiers.toAttributesGroup
import com.davanok.dvnkdnd.database.entities.character.Character
import com.davanok.dvnkdnd.database.entities.character.CharacterMainEntity
import com.davanok.dvnkdnd.database.entities.character.CharacterCoins
import com.davanok.dvnkdnd.database.entities.character.CharacterFeat
import com.davanok.dvnkdnd.database.entities.character.CharacterHealth
import com.davanok.dvnkdnd.database.entities.character.CharacterImage
import com.davanok.dvnkdnd.database.entities.character.CharacterProficiency
import com.davanok.dvnkdnd.database.entities.character.CharacterSelectedModifier
import com.davanok.dvnkdnd.database.entities.character.CharacterSpellSlots
import com.davanok.dvnkdnd.database.entities.character.CharacterAttributes
import com.davanok.dvnkdnd.database.entities.dndEntities.DnDBaseEntity

fun Character.toCharacterMin() = CharacterMin(
    id = id,
    userId = userId,
    name = name,
    level = level,
    image = image
)
data class DbJoinCharacterMainEntities(
    @Embedded
    val link: CharacterMainEntity,
    @Relation(
        DnDBaseEntity::class,
        parentColumn = "entity_id",
        entityColumn = "id"
    )
    val entity: DbFullEntity,
    @Relation(
        DnDBaseEntity::class,
        parentColumn = "sub_entity_id",
        entityColumn = "id"
    )
    val subEntity: DbFullEntity?
) {
    fun toCharacterMainEntityInfo() = CharacterMainEntityInfo(
        level = link.level,
        entity = entity.toDnDFullEntity(),
        subEntity = subEntity?.toDnDFullEntity()
    )
}
data class DbFullCharacter(
    @Embedded val character: Character,

    @Relation(parentColumn = "id", entityColumn = "character_id")
    val images: List<CharacterImage>,

    @Relation(parentColumn = "id", entityColumn = "character_id")
    val coins: CharacterCoins?,

    @Relation(parentColumn = "id", entityColumn = "id")
    val attributes: CharacterAttributes?,
    @Relation(parentColumn = "id", entityColumn = "id")
    val health: CharacterHealth?,
    @Relation(
        CharacterSpellSlots::class,
        parentColumn = "id",
        entityColumn = "id",
    )
    val usedSpells: CharacterSpellSlots?,

    @Relation(
        entity = CharacterMainEntity::class,
        parentColumn = "id",
        entityColumn = "character_id"
    )
    val mainEntities: List<DbJoinCharacterMainEntities>,

    @Relation(
        entity = DnDBaseEntity::class,
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            CharacterFeat::class,
            parentColumn = "character_id",
            entityColumn = "feat_id"
        )
    )
    val feats: List<DbFullEntity>,

    @Relation(
        parentColumn = "id",
        entityColumn = "character_id",
    )
    val selectedModifiers: List<CharacterSelectedModifier>,
    @Relation(
        parentColumn = "id",
        entityColumn = "character_id",
    )
    val selectedProficiencies: List<CharacterProficiency>
) {
    fun toCharacterFull(): CharacterFull = CharacterFull(
        character = character.toCharacterMin(),
        images = images.fastMap { DatabaseImage(it.id, it.path) },
        coins = coins?.toDnDCoinsGroup(),
        attributes = attributes?.toAttributesGroup() ?: DnDAttributesGroup.Default,
        health = health?.toDnDCharacterHealth(),
        usedSpells = usedSpells?.usedSpells.orEmpty(),
        mainEntities = mainEntities.fastMap { it.toCharacterMainEntityInfo() },
        feats = feats.fastMap { it.toDnDFullEntity() },
        selectedModifiers = selectedModifiers.fastMap { it.modifierId },
        selectedProficiencies = selectedProficiencies.fastMap { it.proficiencyId }
    )
}
