package com.davanok.dvnkdnd.data.local.implementations

import co.touchlab.kermit.Logger
import com.davanok.dvnkdnd.core.utils.runLogging
import com.davanok.dvnkdnd.core.utils.toResultFlow
import com.davanok.dvnkdnd.data.local.db.daos.character.CharactersDao
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterFeat
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterItemLink
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterSelectedDamageModifier
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterSelectedRollModifier
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterSelectedValueModifier
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterSpellLink
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterStateLink
import com.davanok.dvnkdnd.data.local.mappers.character.toCharacterFull
import com.davanok.dvnkdnd.data.local.mappers.character.toDbCharacter
import com.davanok.dvnkdnd.data.local.mappers.character.toDbCharacterAttributes
import com.davanok.dvnkdnd.data.local.mappers.character.toDbCharacterMainEntity
import com.davanok.dvnkdnd.domain.entities.character.CharacterBase
import com.davanok.dvnkdnd.domain.entities.character.CharacterCustomModifier
import com.davanok.dvnkdnd.domain.entities.character.CharacterFull
import com.davanok.dvnkdnd.domain.entities.character.CharacterMainEntityLink
import com.davanok.dvnkdnd.domain.entities.character.CharacterOptionalValues
import com.davanok.dvnkdnd.domain.entities.dndEntities.DnDEntityMin
import com.davanok.dvnkdnd.domain.entities.dndModifiers.AttributesGroup
import com.davanok.dvnkdnd.domain.entities.dndModifiers.DnDDamageModifier
import com.davanok.dvnkdnd.domain.entities.dndModifiers.DnDModifier
import com.davanok.dvnkdnd.domain.entities.dndModifiers.DnDRollModifier
import com.davanok.dvnkdnd.domain.entities.dndModifiers.DnDValueModifier
import com.davanok.dvnkdnd.domain.repositories.local.EditCharacterRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull
import kotlin.uuid.Uuid

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(scope = AppScope::class)
class EditCharacterRepositoryImpl(
    private val dao: CharactersDao,
    logger: Logger
) : EditCharacterRepository {
    val logger = logger.withTag(TAG)
    
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getFullCharacterFlow(characterId: Uuid): Flow<Result<CharacterFull>> =
        dao.getFullCharacterFlow(characterId)
            .mapNotNull { it.toCharacterFull() }
            .toResultFlow("getFullCharacterFlow", logger)

    override suspend fun setCharacterBase(
        characterBase: CharacterBase
    ): Result<Unit> = logger.runLogging("setCharacterBase") {
        dao.insertCharacter(characterBase.toDbCharacter())
    }

    override suspend fun setModifierSelection(
        characterId: Uuid,
        modifier: DnDModifier,
        selected: Boolean
    ): Result<Unit> = logger.runLogging("setModifierSelection") {
        val selectedModifier = when (modifier) {
            is DnDValueModifier -> DbCharacterSelectedValueModifier(characterId, modifier.id)
            is DnDRollModifier -> DbCharacterSelectedRollModifier(characterId, modifier.id)
            is DnDDamageModifier -> DbCharacterSelectedDamageModifier(characterId, modifier.id)
        }

        if (selected)
            dao.insertCharacterSelectedModifier(selectedModifier)
        else
            dao.deleteCharacterSelectedModifier(selectedModifier)
    }

    override suspend fun setCustomModifier(
        characterId: Uuid,
        modifier: CharacterCustomModifier
    ): Result<Unit> = logger.runLogging("setCustomModifier") {
        dao.setCharacterCustomModifier(characterId, modifier)
    }

    override suspend fun deleteCustomModifier(
        characterId: Uuid,
        modifier: CharacterCustomModifier
    ): Result<Unit> = logger.runLogging("setCustomModifier") {
        dao.deleteCharacterCustomModifier(characterId, modifier)
    }

    override suspend fun setCharacterAttributes(
        characterId: Uuid,
        attributes: AttributesGroup
    ): Result<Unit> = logger.runLogging("setCharacterAttributes") {
        dao.insertCharacterAttributes(attributes.toDbCharacterAttributes(characterId))
    }

    override suspend fun setCharacterOptionalValues(
        characterId: Uuid,
        values: CharacterOptionalValues
    ): Result<Unit> = logger.runLogging("setCharacterOptionalValues") {
        dao.insertFullOptionalValues(characterId, values)
    }

    override suspend fun setCharacterMainEntity(
        characterId: Uuid,
        entityLink: CharacterMainEntityLink
    ): Result<Unit> = logger.runLogging("setCharacterMainEntity") {
        dao.setCharacterMainEntity(entityLink.toDbCharacterMainEntity(characterId))
    }

    override suspend fun setCharacterMainEntityLevel(
        characterId: Uuid,
        entity: DnDEntityMin,
        level: Int
    ): Result<Unit> = logger.runLogging("setCharacterMainEntityLevel") {
        dao.setCharacterMainEntityLevel(characterId, entity.id, level)
    }

    override suspend fun setCharacterFeat(
        characterId: Uuid,
        feat: DnDEntityMin
    ): Result<Unit> = logger.runLogging("setCharacterFeat") {
        dao.setCharacterFeat(DbCharacterFeat(characterId, feat.id))
    }

    override suspend fun setCharacterSpell(
        characterId: Uuid,
        spell: DnDEntityMin,
        ready: Boolean
    ): Result<Unit> = logger.runLogging("setCharacterSpell") {
        dao.setCharacterSpell(DbCharacterSpellLink(characterId, spell.id, ready))
    }

    override suspend fun setCharacterItem(
        characterId: Uuid,
        item: DnDEntityMin,
        equipped: Boolean,
        attuned: Boolean,
        count: Int?
    ): Result<Unit> = logger.runLogging("setCharacterItem") {
        dao.setCharacterItemLink(
            DbCharacterItemLink(
                characterId = characterId,
                itemId = item.id,
                equipped = equipped,
                attuned = attuned,
                count = count,
            )
        )
    }

    override suspend fun setCharacterState(
        characterId: Uuid,
        state: DnDEntityMin,
        source: DnDEntityMin?,
        deletable: Boolean
    ): Result<Unit> = logger.runLogging("setCharacterState") {
        dao.setCharacterState(
            DbCharacterStateLink(
                characterId = characterId,
                stateId = state.id,
                sourceId = source?.id,
                deletable = deletable
            )
        )
    }
    
    companion object {
        private const val TAG = "EditCharacterRepository"
    }
}