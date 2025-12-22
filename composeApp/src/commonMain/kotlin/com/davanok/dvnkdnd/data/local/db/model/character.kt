package com.davanok.dvnkdnd.data.local.db.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.davanok.dvnkdnd.domain.entities.DatabaseImage
import com.davanok.dvnkdnd.domain.entities.character.CharacterFull
import com.davanok.dvnkdnd.domain.entities.character.CharacterItem
import com.davanok.dvnkdnd.domain.entities.character.CharacterMainEntityInfo
import com.davanok.dvnkdnd.domain.entities.character.CharacterSpell
import com.davanok.dvnkdnd.domain.entities.character.CoinsGroup
import com.davanok.dvnkdnd.domain.entities.character.CharacterHealth
import com.davanok.dvnkdnd.domain.entities.dndModifiers.AttributesGroup
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacter
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterAttributes
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterCoins
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterCustomModifier
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterFeat
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterHealth
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterImage
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterMainEntity
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterOptionalValues
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterProficiency
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterSelectedModifier
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterUsedSpellSlots
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterItemLink
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterNote
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterSpellLink
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterStateLink
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.DbBaseEntity
import com.davanok.dvnkdnd.data.local.mappers.character.toAttributesGroup
import com.davanok.dvnkdnd.data.local.mappers.character.toCharacterBase
import com.davanok.dvnkdnd.data.local.mappers.character.toCharacterNote
import com.davanok.dvnkdnd.data.local.mappers.character.toCharacterOptionalValues
import com.davanok.dvnkdnd.data.local.mappers.character.toCoinsGroup
import com.davanok.dvnkdnd.data.local.mappers.character.toCustomModifier
import com.davanok.dvnkdnd.data.local.mappers.character.toDnDCharacterHealth
import com.davanok.dvnkdnd.domain.entities.character.CharacterState

data class DbJoinCharacterMainEntities(
    @Embedded
    val link: DbCharacterMainEntity,
    @Relation(
        DbBaseEntity::class,
        parentColumn = "entity_id",
        entityColumn = "id"
    )
    val entity: DbFullEntity,
    @Relation(
        DbBaseEntity::class,
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
        DbBaseEntity::class,
        parentColumn = "item_id",
        entityColumn = "id"
    )
    val item: DbFullEntity
) {
    fun toCharacterItem() = CharacterItem(
        equipped = link.equipped,
        active = link.active,
        attuned = link.attuned,
        count = link.count,
        item = item.toDnDFullEntity()
    )
}
data class DbJoinCharacterSpell(
    @Embedded
    val link: DbCharacterSpellLink,
    @Relation(
        DbBaseEntity::class,
        parentColumn = "spell_id",
        entityColumn = "id"
    )
    val spell: DbFullEntity
)
data class DbJoinCharacterState(
    @Embedded
    val link: DbCharacterStateLink,
    @Relation(
        DbBaseEntity::class,
        parentColumn = "state_id",
        entityColumn = "id"
    )
    val state: DbFullEntity
)
data class DbFullCharacter(
    @Embedded val character: DbCharacter,

    @Relation(parentColumn = "id", entityColumn = "id")
    val optionalValues: DbCharacterOptionalValues,

    @Relation(parentColumn = "id", entityColumn = "character_id")
    val images: List<DbCharacterImage>,

    @Relation(parentColumn = "id", entityColumn = "character_id")
    val coins: DbCharacterCoins?,

    @Relation(DbCharacterItemLink::class, parentColumn = "id", entityColumn = "character_id")
    val items: List<DbJoinCharacterItem>,
    @Relation(DbCharacterSpellLink::class, parentColumn = "id", entityColumn = "character_id")
    val spells: List<DbJoinCharacterSpell>,

    @Relation(parentColumn = "id", entityColumn = "id")
    val attributes: DbCharacterAttributes?,
    @Relation(parentColumn = "id", entityColumn = "id")
    val health: DbCharacterHealth?,

    @Relation(parentColumn = "id", entityColumn = "character_id")
    val usedSpells: List<DbCharacterUsedSpellSlots>,

    @Relation(
        entity = DbCharacterMainEntity::class,
        parentColumn = "id",
        entityColumn = "character_id"
    )
    val mainEntities: List<DbJoinCharacterMainEntities>,

    @Relation(
        entity = DbBaseEntity::class,
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            DbCharacterFeat::class,
            parentColumn = "character_id",
            entityColumn = "feat_id"
        )
    )
    val feats: List<DbFullEntity>,

    @Relation(
        parentColumn = "id",
        entityColumn = "character_id"
    )
    val selectedModifiers: List<DbCharacterSelectedModifier>,
    @Relation(
        parentColumn = "id",
        entityColumn = "character_id"
    )
    val selectedProficiencies: List<DbCharacterProficiency>,

    @Relation(
        parentColumn = "id",
        entityColumn = "character_id"
    )
    val customModifiers: List<DbCharacterCustomModifier>,

    @Relation(
        entity = DbCharacterStateLink::class,
        parentColumn = "id",
        entityColumn = "character_id"
    )
    val states: List<DbJoinCharacterState>,

    @Relation(
        parentColumn = "id",
        entityColumn = "character_id"
    )
    val notes: List<DbCharacterNote>
) {
    fun toCharacterFull(): CharacterFull = CharacterFull(
        character = character.toCharacterBase(),
        optionalValues = optionalValues.toCharacterOptionalValues(),
        images = images.map { DatabaseImage(it.id, it.path) },
        coins = coins?.toCoinsGroup() ?: CoinsGroup(),
        items = items.map(DbJoinCharacterItem::toCharacterItem),
        spells = spells.map { CharacterSpell(it.link.ready, it.spell.toDnDFullEntity()) },
        attributes = attributes?.toAttributesGroup() ?: AttributesGroup.Default,
        health = health?.toDnDCharacterHealth() ?: CharacterHealth(),
        usedSpells = usedSpells.associate { it.spellSlotTypeId to it.usedSpells.toIntArray() },
        mainEntities = mainEntities.map(DbJoinCharacterMainEntities::toCharacterMainEntityInfo),
        feats = feats.map(DbFullEntity::toDnDFullEntity),
        selectedModifiers = selectedModifiers.map { it.modifierId }.toSet(),
        selectedProficiencies = selectedProficiencies.map { it.proficiencyId }.toSet(),
        customModifiers = customModifiers.map(DbCharacterCustomModifier::toCustomModifier),
        states = states.map { CharacterState(state = it.state.toDnDFullEntity(), from = it.link.fromId) },
        notes = notes.map(DbCharacterNote::toCharacterNote)
    )
}
