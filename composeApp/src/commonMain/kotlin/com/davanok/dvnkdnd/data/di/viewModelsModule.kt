package com.davanok.dvnkdnd.data.di

import com.davanok.dvnkdnd.data.di.viewModelModules.newCharacterViewModelsModule
import com.davanok.dvnkdnd.ui.pages.charactersList.CharactersListViewModel
import com.davanok.dvnkdnd.ui.pages.dndEntityInfo.DnDEntityInfoViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

fun viewModelsModule() = module {
    includes(newCharacterViewModelsModule())

    viewModelOf(::CharactersListViewModel)

    viewModelOf(::DnDEntityInfoViewModel)
}