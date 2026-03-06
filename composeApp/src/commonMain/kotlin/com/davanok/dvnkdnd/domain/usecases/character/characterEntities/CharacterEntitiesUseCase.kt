package com.davanok.dvnkdnd.domain.usecases.character.characterEntities

import com.davanok.dvnkdnd.domain.entities.dndEntities.DnDEntityMin
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlin.uuid.Uuid

@Inject
@SingleIn(AppScope::class)
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