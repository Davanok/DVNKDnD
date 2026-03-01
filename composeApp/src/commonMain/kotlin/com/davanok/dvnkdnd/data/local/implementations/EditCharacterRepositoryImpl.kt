package com.davanok.dvnkdnd.data.local.implementations

import com.davanok.dvnkdnd.core.utils.runLogging
import com.davanok.dvnkdnd.data.local.db.daos.character.CharactersDao
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterSelectedDamageModifier
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterSelectedRollModifier
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterSelectedValueModifier
import com.davanok.dvnkdnd.data.local.mappers.character.toCharacterFull
import com.davanok.dvnkdnd.data.local.mappers.character.toDbCharacterAttributes
import com.davanok.dvnkdnd.domain.entities.character.CharacterCustomModifier
import com.davanok.dvnkdnd.domain.entities.character.CharacterFull
import com.davanok.dvnkdnd.domain.entities.character.CharacterOptionalValues
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
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.mapLatest
import kotlin.uuid.Uuid

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(scope = AppScope::class)
class EditCharacterRepositoryImpl(
    private val dao: CharactersDao
) : EditCharacterRepository {
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getFullCharacterFlow(characterId: Uuid): Flow<Result<CharacterFull>> =
        dao.getFullCharacterFlow(characterId).mapLatest {
            Result.success(it.toCharacterFull())
        }.catch { thr -> emit(Result.failure(thr)) }

    override suspend fun setModifierSelection(
        characterId: Uuid,
        modifier: DnDModifier,
        selected: Boolean
    ): Result<Unit> = runLogging("setModifierSelection") {
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
    ): Result<Unit> = runLogging("setCustomModifier") {
        dao.setCharacterCustomModifier(characterId, modifier)
    }

    override suspend fun deleteCustomModifier(
        characterId: Uuid,
        modifier: CharacterCustomModifier
    ): Result<Unit> = runLogging("setCustomModifier") {
        dao.deleteCharacterCustomModifier(characterId, modifier)
    }

    override suspend fun setCharacterAttributes(
        characterId: Uuid,
        attributes: AttributesGroup
    ): Result<Unit> = runLogging("setCharacterAttributes") {
        dao.insertCharacterAttributes(attributes.toDbCharacterAttributes(characterId))
    }

    override suspend fun setCharacterOptionalValues(
        characterId: Uuid,
        values: CharacterOptionalValues
    ): Result<Unit> = runLogging("setCharacterOptionalValues") {
        dao.insertFullOptionalValues(characterId, values)
    }
}