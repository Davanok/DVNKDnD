package com.davanok.dvnkdnd.data.di

import com.davanok.dvnkdnd.data.di.viewModelModules.characterFullViewModelsModule
import com.davanok.dvnkdnd.data.di.viewModelModules.newCharacterViewModelsModule
import com.davanok.dvnkdnd.ui.pages.charactersList.CharactersListViewModel
import com.davanok.dvnkdnd.ui.pages.dndEntityInfo.DnDEntityInfoViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import kotlin.uuid.Uuid

fun viewModelsModule() = module {
    includes(newCharacterViewModelsModule())

    viewModelOf(::CharactersListViewModel)

    viewModel { (entityId: Uuid) ->
        DnDEntityInfoViewModel(entityId, get())
    }

    includes(characterFullViewModelsModule())
}