package com.davanok.dvnkdnd.database.daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.davanok.dvnkdnd.data.model.dnd_enums.DnDEntityTypes
import com.davanok.dvnkdnd.data.model.entities.DnDEntityMin
import com.davanok.dvnkdnd.database.model.EntityWithSub

@Dao
interface EntitiesDao {
    @Query("SELECT id, type, name, source FROM base_entities WHERE type == :type AND parentId == :parentId")
    suspend fun getEntitiesMinList(type: DnDEntityTypes, parentId: Long? = null): List<DnDEntityMin>

    @Transaction
    @Query("SELECT * FROM base_entities WHERE type == :type")
    suspend fun getEntitiesWithSubList(type: DnDEntityTypes): List<EntityWithSub>
}