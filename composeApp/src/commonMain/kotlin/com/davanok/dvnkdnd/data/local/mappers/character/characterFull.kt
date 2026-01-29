package com.davanok.dvnkdnd.data.local.mappers.character

import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterImage
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterNote
import com.davanok.dvnkdnd.data.local.db.model.DbFullEntity
import com.davanok.dvnkdnd.data.local.db.model.character.DbFullCharacter
import com.davanok.dvnkdnd.data.local.db.model.character.DbJoinCharacterItem
import com.davanok.dvnkdnd.data.local.db.model.character.DbJoinCharacterMainEntities
import com.davanok.dvnkdnd.data.local.db.model.character.DbJoinCharacterState
import com.davanok.dvnkdnd.data.local.mappers.entities.toDnDFullEntity
import com.davanok.dvnkdnd.domain.entities.character.CharacterCustomModifier
import com.davanok.dvnkdnd.domain.entities.character.CharacterFull
import com.davanok.dvnkdnd.domain.entities.character.CharacterHealth
import com.davanok.dvnkdnd.domain.entities.character.CharacterOptionalValues
import com.davanok.dvnkdnd.domain.entities.character.CharacterSelectedModifiers
import com.davanok.dvnkdnd.domain.entities.character.CharacterSpell
import com.davanok.dvnkdnd.domain.entities.character.CoinsGroup
import com.davanok.dvnkdnd.domain.entities.dndModifiers.AttributesGroup

private fun DbFullCharacter.convertCustomModifiers(): List<CharacterCustomModifier> =
    customValueModifiers.map { it.toCharacterCustomModifier() } +
            customRollModifiers.map { it.toCharacterCustomModifier() } +
            customDamageModifiers.map { it.toCharacterCustomModifier() }

private fun DbFullCharacter.convertSelectedModifiers() = CharacterSelectedModifiers(
    valueModifiers = selectedValueModifiers.map { it.modifierId }.toSet(),
    rollModifiers = selectedRollModifiers.map { it.modifierId }.toSet(),
    damageModifiers = selectedDamageModifiers.map { it.modifierId }.toSet(),
)

fun DbFullCharacter.toCharacterFull(): CharacterFull = CharacterFull(
    character = character.toCharacterBase(),
    optionalValues = optionalValues?.toCharacterOptionalValues() ?: CharacterOptionalValues(),
    images = images.map(DbCharacterImage::toDatabaseImage),
    coins = coins?.toCoinsGroup() ?: CoinsGroup(),
    items = items.map(DbJoinCharacterItem::toCharacterItem),
    spells = spells.map { CharacterSpell(it.link.ready, it.spell.toDnDFullEntity()) },
    attributes = attributes?.toAttributesGroup() ?: AttributesGroup.Default,
    health = health?.toDnDCharacterHealth() ?: CharacterHealth(),
    usedItemActivations = usedItemActivations.associate { it.activationId to it.count },
    usedSpells = usedSpells.associate { it.spellSlotTypeId to it.usedSpells.toIntArray() },
    mainEntities = mainEntities.map(DbJoinCharacterMainEntities::toCharacterMainEntityInfo),
    feats = feats.map(DbFullEntity::toDnDFullEntity),
    customModifiers = convertCustomModifiers(),
    selectedModifiers = convertSelectedModifiers(),
    selectedProficiencies = selectedProficiencies.map { it.proficiencyId }.toSet(),
    states = states.map(DbJoinCharacterState::toCharacterState),
    notes = notes.map(DbCharacterNote::toCharacterNote)
)