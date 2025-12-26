package com.davanok.dvnkdnd.data.local.db.model.character

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacter
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterAttributes
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterCoins
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterCustomModifier
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterFeat
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterHealth
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterImage
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterItemLink
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterMainEntity
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterNote
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterOptionalValues
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterProficiency
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterSelectedModifier
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterSpellLink
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterStateLink
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterUsedSpellSlots
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.DbBaseEntity
import com.davanok.dvnkdnd.data.local.db.model.DbFullEntity

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
)
