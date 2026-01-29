package com.davanok.dvnkdnd.data.local.db.model

import androidx.room.Embedded
import androidx.room.Relation
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.DbEntityProficiency
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.companion.DbFeature
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.companion.DbFeatureRegain
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.companion.DbProficiency

data class DbJoinProficiency(
    @Embedded val link: DbEntityProficiency,
    @Relation(
        parentColumn = "proficiency_id",
        entityColumn = "id"
    )
    val proficiency: DbProficiency
)

data class DbFullFeature(
    @Embedded val feature: DbFeature,
    @Relation(
        parentColumn = "id",
        entityColumn = "feature_id"
    )
    val regains: List<DbFeatureRegain>
)