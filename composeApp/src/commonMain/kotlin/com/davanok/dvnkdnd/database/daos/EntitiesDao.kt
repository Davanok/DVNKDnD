package com.davanok.dvnkdnd.database.daos

import androidx.room.Dao
import androidx.room.Query
import com.davanok.dvnkdnd.data.model.dnd_enums.DnDEntityTypes
import com.davanok.dvnkdnd.data.model.entities.DnDEntityMin

@Dao
interface EntitiesDao {
    @Query("SELECT id, name, source FROM base_entities WHERE type == :type AND parentId == :parentId")
    suspend fun getEntitiesMinList(type: DnDEntityTypes, parentId: Long? = null): List<DnDEntityMin>
}