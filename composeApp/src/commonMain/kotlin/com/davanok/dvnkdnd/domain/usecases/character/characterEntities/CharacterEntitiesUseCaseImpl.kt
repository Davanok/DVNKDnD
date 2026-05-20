package com.davanok.dvnkdnd.domain.usecases.character.characterEntities

import co.touchlab.kermit.Logger
import com.davanok.dvnkdnd.core.utils.runLogging
import com.davanok.dvnkdnd.domain.entities.character.CharacterMainEntityLink
import com.davanok.dvnkdnd.domain.entities.dndEntities.DnDEntityMin
import com.davanok.dvnkdnd.domain.enums.dndEnums.DnDEntityTypes
import com.davanok.dvnkdnd.domain.repositories.local.EditCharacterRepository
import com.davanok.dvnkdnd.domain.usecases.entities.bootstrap.EntitiesBootstrapper
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlin.uuid.Uuid

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(scope = AppScope::class)
class CharacterEntitiesUseCaseImpl(
    private val characterRepository: EditCharacterRepository,
    private val entitiesBootstrapper: EntitiesBootstrapper,
    logger: Logger
) : CharacterEntitiesUseCase {
    private val logger = logger.withTag(TAG)

    override suspend fun addCharacterEntity(
        characterId: Uuid,
        entity: DnDEntityMin,
        subEntity: DnDEntityMin?
    ) = logger.runLogging("addCharacterEntity") {
        when (entity.type) {
            DnDEntityTypes.CLASS,
            DnDEntityTypes.RACE,
            DnDEntityTypes.BACKGROUND -> {
                validateSubEntity(entity, subEntity)
                addCharacterMainEntity(characterId, entity, subEntity)
            }

            DnDEntityTypes.FEAT,
            DnDEntityTypes.SPELL,
            DnDEntityTypes.ITEM,
            DnDEntityTypes.STATE -> {
                require(subEntity == null) { "${entity.type} cannot have a subEntity" }
                addOtherEntity(characterId, entity)
            }

            DnDEntityTypes.SUB_CLASS,
            DnDEntityTypes.SUB_RACE,
            DnDEntityTypes.SUB_BACKGROUND -> {
                error("Cannot add sub-entity (${entity.type}) as a standalone parent entity")
            }

            DnDEntityTypes.FEATURE -> {
                error("Cannot add entity (${entity.type}) directly to character")
            }
        }
    }

    override suspend fun removeCharacterEntity(
        characterId: Uuid,
        entity: DnDEntityMin
    ) {
        TODO("Not yet implemented")
    }

    private suspend fun addCharacterMainEntity(
        characterId: Uuid,
        entity: DnDEntityMin,
        subEntity: DnDEntityMin?
    ) {
        entitiesBootstrapper.checkAndLoadEntity(entity.id).getOrThrow()
        subEntity?.let { entitiesBootstrapper.checkAndLoadEntity(it.id).getOrThrow() }

        val entityLink =
            CharacterMainEntityLink(level = 1, entityId = entity.id, subEntityId = subEntity?.id)
        characterRepository.setCharacterMainEntity(characterId, entityLink).getOrThrow()
    }

    private suspend fun addOtherEntity(characterId: Uuid, entity: DnDEntityMin) {
        entitiesBootstrapper.checkAndLoadEntity(entity.id).getOrThrow()

        with(characterRepository) {
            when (entity.type) {
                DnDEntityTypes.FEAT -> setCharacterFeat(
                    characterId = characterId,
                    feat = entity
                )

                DnDEntityTypes.SPELL -> setCharacterSpell(
                    characterId = characterId,
                    spell = entity,
                    ready = false
                )

                DnDEntityTypes.ITEM -> setCharacterItem(
                    characterId = characterId,
                    item = entity,
                    equipped = false,
                    attuned = false,
                    count = null,
                )

                DnDEntityTypes.STATE -> setCharacterState(
                    characterId = characterId,
                    state = entity,
                    source = null,
                    deletable = true
                )

                else -> error("Type ${entity.type} is not supported for direct addition")
            }
        }.getOrThrow()
    }

    private fun validateSubEntity(parent: DnDEntityMin, sub: DnDEntityMin?) {
        if (sub == null) return

        val expectedSubType = when (parent.type) {
            DnDEntityTypes.CLASS -> DnDEntityTypes.SUB_CLASS
            DnDEntityTypes.RACE -> DnDEntityTypes.SUB_RACE
            DnDEntityTypes.BACKGROUND -> DnDEntityTypes.SUB_BACKGROUND
            else -> null
        }

        check(sub.type == expectedSubType) { "Invalid sub-entity type: ${sub.type} for parent ${parent.type}" }
        check(sub.parentId == parent.id) { "Sub-entity does not belong to the provided parent entity" }
    }

    companion object {
        private const val TAG = "CharacterEntitiesUseCase"
    }
}