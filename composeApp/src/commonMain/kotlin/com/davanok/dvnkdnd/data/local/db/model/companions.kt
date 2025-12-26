package com.davanok.dvnkdnd.data.local.db.model

import androidx.room.Embedded
import androidx.room.Relation
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.DbEntityProficiency
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.companion.DbAbility
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.companion.DbAbilityRegain
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.companion.DbProficiency

data class DbJoinProficiency(
    @Embedded val link: DbEntityProficiency,
    @Relation(
        parentColumn = "proficiency_id",
        entityColumn = "id"
    )
    val proficiency: DbProficiency
)

data class DbAbilityInfo(
    @Embedded val ability: DbAbility,
    @Relation(
        parentColumn = "id",
        entityColumn = "ability_id"
    )
    val regains: List<DbAbilityRegain>
)