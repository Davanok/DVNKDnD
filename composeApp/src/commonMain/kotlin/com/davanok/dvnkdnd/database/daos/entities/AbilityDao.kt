package com.davanok.dvnkdnd.database.daos.entities

import androidx.compose.ui.util.fastMap
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Transaction
import com.davanok.dvnkdnd.data.model.entities.dndEntities.AbilityInfo
import com.davanok.dvnkdnd.data.model.entities.dndEntities.toDnDAbility
import com.davanok.dvnkdnd.data.model.entities.dndEntities.toDnDAbilityRegain
import com.davanok.dvnkdnd.database.entities.dndEntities.companion.DnDAbility
import com.davanok.dvnkdnd.database.entities.dndEntities.companion.DnDAbilityRegain
import kotlin.uuid.Uuid

@Dao
interface AbilityDao {
    @Insert
    suspend fun insertAbility(ability: DnDAbility)
    @Insert
    suspend fun insertAbilityRegains(regains: List<DnDAbilityRegain>)
    @Transaction
    suspend fun insertAbilityInfo(entityId: Uuid, ability: AbilityInfo) {
        insertAbility(ability.toDnDAbility(entityId))
        insertAbilityRegains(
            ability.regains.fastMap { it.toDnDAbilityRegain(entityId) }
        )
    }
}