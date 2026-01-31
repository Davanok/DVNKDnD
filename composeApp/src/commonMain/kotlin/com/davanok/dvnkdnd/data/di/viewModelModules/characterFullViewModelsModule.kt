package com.davanok.dvnkdnd.data.di.viewModelModules

import com.davanok.dvnkdnd.domain.enums.dndEnums.DnDEntityTypes
import com.davanok.dvnkdnd.ui.pages.characterFull.CharacterAddEntityViewModel
import com.davanok.dvnkdnd.ui.pages.characterFull.CharacterFullViewModel
import com.davanok.dvnkdnd.ui.pages.editCharacter.EditCharacterViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import kotlin.uuid.Uuid

fun characterFullViewModelsModule() = module {
    viewModel { (characterId: Uuid) ->
        CharacterFullViewModel(characterId, get(), get())
    }

    viewModel { (entityType: DnDEntityTypes) ->
        CharacterAddEntityViewModel(entityType, get())
    }

    viewModel { (characterId: Uuid) ->
        EditCharacterViewModel(characterId, get(), get())
    }
}