package com.davanok.dvnkdnd.ui.pages.editCharacter

import androidx.lifecycle.ViewModel
import com.davanok.dvnkdnd.domain.repositories.local.CharactersRepository
import com.davanok.dvnkdnd.domain.usecases.entities.EntitiesBootstrapper
import kotlin.uuid.Uuid

class EditCharacterViewModel(
    private val characterId: Uuid,
    private val bootstrapper: EntitiesBootstrapper,
    private val repository: CharactersRepository
): ViewModel() {
}