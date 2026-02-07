package com.davanok.dvnkdnd.data.local.db.model.character

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacter
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterAttributes
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterCoins
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterCustomDamageModifier
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterCustomRollModifier
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterCustomValueModifier
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterFeat
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterHealth
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterImage
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterItemActivationsCount
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterItemLink
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterMainEntity
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterSettings
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterNote
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterOptionalValues
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterProficiency
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterSelectedDamageModifier
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterSelectedRollModifier
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterSelectedValueModifier
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterSpellLink
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterStateLink
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterUsedSpellSlots
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.DbBaseEntity
import com.davanok.dvnkdnd.data.local.db.model.DbFullEntity

data class DbFullCharacter(
    @Embedded val character: DbCharacter,

    @Relation(parentColumn = "id", entityColumn = "id")
    val optionalValues: DbCharacterOptionalValues?,

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
    val usedItemActivations: List<DbCharacterItemActivationsCount>,
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

    @Relation(parentColumn = "id", entityColumn = "character_id")
    val selectedValueModifiers: List<DbCharacterSelectedValueModifier>,
    @Relation(parentColumn = "id", entityColumn = "character_id")
    val selectedRollModifiers: List<DbCharacterSelectedRollModifier>,
    @Relation(parentColumn = "id", entityColumn = "character_id")
    val selectedDamageModifiers: List<DbCharacterSelectedDamageModifier>,

    @Relation(parentColumn = "id", entityColumn = "character_id")
    val customValueModifiers: List<DbCharacterCustomValueModifier>,
    @Relation(parentColumn = "id", entityColumn = "character_id")
    val customRollModifiers: List<DbCharacterCustomRollModifier>,
    @Relation(parentColumn = "id", entityColumn = "character_id")
    val customDamageModifiers: List<DbCharacterCustomDamageModifier>,

    @Relation(
        parentColumn = "id",
        entityColumn = "character_id"
    )
    val selectedProficiencies: List<DbCharacterProficiency>,

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
    val notes: List<DbCharacterNote>,

    @Relation(
        parentColumn = "id",
        entityColumn = "id"
    )
    val settings: DbCharacterSettings?
)
