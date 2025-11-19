package com.davanok.dvnkdnd.database.daos.entities

import androidx.compose.ui.util.fastMap
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Transaction
import com.davanok.dvnkdnd.data.model.entities.dndEntities.AbilityInfo
import com.davanok.dvnkdnd.database.entities.dndEntities.companion.DbAbility
import com.davanok.dvnkdnd.database.entities.dndEntities.companion.DbAbilityRegain
import com.davanok.dvnkdnd.database.model.adapters.entities.toDbAbility
import com.davanok.dvnkdnd.database.model.adapters.entities.toDbAbilityRegain
import kotlin.uuid.Uuid

@Dao
interface AbilityDao {
    @Insert
    suspend fun insertAbility(ability: DbAbility)

    @Insert
    suspend fun insertAbilityRegains(regains: List<DbAbilityRegain>)

    @Transaction
    suspend fun insertAbilityInfo(entityId: Uuid, ability: AbilityInfo) {
        insertAbility(ability.toDbAbility(entityId))
        insertAbilityRegains(
            ability.regains.fastMap { it.toDbAbilityRegain(entityId) }
        )
    }
}