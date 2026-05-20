package com.davanok.dvnkdnd.domain.usecases.character.characterEntities

import com.davanok.dvnkdnd.domain.entities.dndEntities.DnDEntityMin
import kotlin.uuid.Uuid

interface CharacterEntitiesUseCase {
    suspend fun addCharacterEntity(
        characterId: Uuid,
        entity: DnDEntityMin,
        subEntity: DnDEntityMin?
    ): Result<Unit>

    suspend fun removeCharacterEntity(
        characterId: Uuid,
        entity: DnDEntityMin
    )
}