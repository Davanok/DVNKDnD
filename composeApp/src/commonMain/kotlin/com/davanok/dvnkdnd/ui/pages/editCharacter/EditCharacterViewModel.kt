package com.davanok.dvnkdnd.ui.pages.editCharacter

import androidx.lifecycle.ViewModel
import com.davanok.dvnkdnd.domain.repositories.local.CharactersRepository
import com.davanok.dvnkdnd.domain.usecases.entities.EntitiesBootstrapper
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactory
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactoryKey
import kotlin.uuid.Uuid

@AssistedInject
class EditCharacterViewModel(
    @Assisted private val characterId: Uuid,
    private val bootstrapper: EntitiesBootstrapper,
    private val repository: CharactersRepository
): ViewModel() {

    @AssistedFactory
    @ManualViewModelAssistedFactoryKey(Factory::class)
    @ContributesIntoMap(AppScope::class)
    fun interface Factory : ManualViewModelAssistedFactory {
        fun create(@Assisted characterId: Uuid): EditCharacterViewModel
    }
}