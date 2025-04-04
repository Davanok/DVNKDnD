package com.davanok.dvnkdnd.data.di

import com.davanok.dvnkdnd.ui.pages.charactersList.CharactersListViewModel
import com.davanok.dvnkdnd.ui.pages.dndEntityInfo.DnDEntityInfoViewModel
import com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.newCharacterMain.NewCharacterMainViewModel
import com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.newCharacterStats.NewCharacterStatsViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

fun viewModelsModule() = module {
    viewModelOf(::CharactersListViewModel)
//    single<CharactersListViewModel> { CharactersListViewModel(get()) }

    viewModelOf(::NewCharacterMainViewModel)
//    single<NewCharacterMainViewModel> { NewCharacterMainViewModel(get(), get(), get()) }
    viewModelOf(::NewCharacterStatsViewModel)
//    single<NewCharacterStatsViewModel> { NewCharacterStatsViewModel() }

    viewModelOf(::DnDEntityInfoViewModel)
//    single<DnDEntityInfoViewModel> { DnDEntityInfoViewModel(get()) }
}