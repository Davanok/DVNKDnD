package com.davanok.dvnkdnd.data.local.db.model.character

import androidx.room.Embedded
import androidx.room.Relation
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterSpellLink
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.DbBaseEntity
import com.davanok.dvnkdnd.data.local.db.model.DbFullEntity

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