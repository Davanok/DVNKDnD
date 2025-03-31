package com.davanok.dvnkdnd.data.di

import com.davanok.dvnkdnd.ui.pages.charactersList.CharactersListViewModel
import com.davanok.dvnkdnd.ui.pages.dndEntityInfo.DnDEntityInfoViewModel
import com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.NewCharacterViewModel
import org.koin.dsl.module

fun viewModelsModule() = module {
    single<CharactersListViewModel> { CharactersListViewModel(get()) }

    single<NewCharacterViewModel> { NewCharacterViewModel(get(), get()) }

    single<DnDEntityInfoViewModel> { DnDEntityInfoViewModel(get()) }
}