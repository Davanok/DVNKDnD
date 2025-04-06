@file:OptIn(ExperimentalUuidApi::class)

package com.davanok.dvnkdnd.data.repositories

import com.davanok.dvnkdnd.data.model.dnd_enums.DnDEntityTypes
import com.davanok.dvnkdnd.data.model.entities.DnDEntityWithSubEntities
import com.davanok.dvnkdnd.database.entities.character.Character
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

interface NewCharacterRepository {
    suspend fun getEntitiesWithSubList(type: DnDEntityTypes): List<DnDEntityWithSubEntities>
    suspend fun createCharacter(character: Character): Uuid

    suspend fun getExistingEntities(entityIds: List<Uuid>): List<Uuid>
}