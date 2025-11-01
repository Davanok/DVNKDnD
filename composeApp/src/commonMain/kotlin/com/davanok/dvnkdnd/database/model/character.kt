package com.davanok.dvnkdnd.database.model

import androidx.compose.ui.util.fastMap
import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.davanok.dvnkdnd.data.model.entities.DatabaseImage
import com.davanok.dvnkdnd.data.model.entities.character.CharacterFull
import com.davanok.dvnkdnd.data.model.entities.character.CharacterItem
import com.davanok.dvnkdnd.data.model.entities.character.CharacterMainEntityInfo
import com.davanok.dvnkdnd.data.model.entities.character.CoinsGroup
import com.davanok.dvnkdnd.data.model.entities.character.DnDCharacterHealth
import com.davanok.dvnkdnd.data.model.entities.dndEntities.DnDFullEntity
import com.davanok.dvnkdnd.data.model.entities.dndModifiers.DnDAttributesGroup
import com.davanok.dvnkdnd.database.entities.character.Character
import com.davanok.dvnkdnd.database.entities.character.CharacterAttributes
import com.davanok.dvnkdnd.database.entities.character.CharacterCoins
import com.davanok.dvnkdnd.database.entities.character.CharacterCustomModifier
import com.davanok.dvnkdnd.database.entities.character.CharacterFeat
import com.davanok.dvnkdnd.database.entities.character.CharacterHealth
import com.davanok.dvnkdnd.database.entities.character.CharacterImage
import com.davanok.dvnkdnd.database.entities.character.CharacterMainEntity
import com.davanok.dvnkdnd.database.entities.character.DbCharacterOptionalValues
import com.davanok.dvnkdnd.database.entities.character.CharacterProficiency
import com.davanok.dvnkdnd.database.entities.character.CharacterSelectedModifier
import com.davanok.dvnkdnd.database.entities.character.CharacterSpellSlots
import com.davanok.dvnkdnd.database.entities.character.DbCharacterItemLink
import com.davanok.dvnkdnd.database.entities.character.DbCharacterNote
import com.davanok.dvnkdnd.database.entities.dndEntities.DnDBaseEntity
import com.davanok.dvnkdnd.database.model.adapters.character.toAttributesGroup
import com.davanok.dvnkdnd.database.model.adapters.character.toCharacterBase
import com.davanok.dvnkdnd.database.model.adapters.character.toCharacterNote
import com.davanok.dvnkdnd.database.model.adapters.character.toCharacterOptionalValues
import com.davanok.dvnkdnd.database.model.adapters.character.toCoinsGroup
import com.davanok.dvnkdnd.database.model.adapters.character.toCustomModifier
import com.davanok.dvnkdnd.database.model.adapters.character.toDnDCharacterHealth
import com.davanok.dvnkdnd.database.model.entities.DbFullEntity

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
data class DbJoinCharacterItem(
    @Embedded
    val link: DbCharacterItemLink,
    @Relation(
        DnDBaseEntity::class,
        parentColumn = "item_id",
        entityColumn = "id"
    )
    val item: DnDFullEntity
)
data class DbFullCharacter(
    @Embedded val character: Character,

    @Relation(parentColumn = "id", entityColumn = "id")
    val optionalValues: DbCharacterOptionalValues,

    @Relation(parentColumn = "id", entityColumn = "character_id")
    val images: List<CharacterImage>,

    @Relation(parentColumn = "id", entityColumn = "character_id")
    val coins: CharacterCoins?,

    @Relation(parentColumn = "id", entityColumn = "character_id")
    val items: List<DbJoinCharacterItem>,

    @Relation(parentColumn = "id", entityColumn = "id")
    val attributes: CharacterAttributes?,
    @Relation(parentColumn = "id", entityColumn = "id")
    val health: CharacterHealth?,
    @Relation(
        CharacterSpellSlots::class,
        parentColumn = "id",
        entityColumn = "id"
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
        entityColumn = "character_id"
    )
    val selectedModifiers: List<CharacterSelectedModifier>,
    @Relation(
        parentColumn = "id",
        entityColumn = "character_id"
    )
    val selectedProficiencies: List<CharacterProficiency>,

    @Relation(
        parentColumn = "id",
        entityColumn = "character_id"
    )
    val customModifiers: List<CharacterCustomModifier>,

    @Relation(
        parentColumn = "id",
        entityColumn = "character_id"
    )
    val notes: List<DbCharacterNote>
) {
    fun toCharacterFull(): CharacterFull = CharacterFull(
        character = character.toCharacterBase(),
        optionalValues = optionalValues.toCharacterOptionalValues(),
        images = images.fastMap { DatabaseImage(it.id, it.path) },
        coins = coins?.toCoinsGroup() ?: CoinsGroup(),
        items = items.fastMap { CharacterItem(it.link.attuned, it.link.equipped, it.item) },
        attributes = attributes?.toAttributesGroup() ?: DnDAttributesGroup.Default,
        health = health?.toDnDCharacterHealth() ?: DnDCharacterHealth(),
        usedSpells = usedSpells?.usedSpells.orEmpty(),
        mainEntities = mainEntities.fastMap(DbJoinCharacterMainEntities::toCharacterMainEntityInfo),
        feats = feats.fastMap(DbFullEntity::toDnDFullEntity),
        selectedModifiers = selectedModifiers.fastMap { it.modifierId }.toSet(),
        selectedProficiencies = selectedProficiencies.fastMap { it.proficiencyId }.toSet(),
        customModifiers = customModifiers.fastMap(CharacterCustomModifier::toCustomModifier),
        notes = notes.map(DbCharacterNote::toCharacterNote)
    )
}
