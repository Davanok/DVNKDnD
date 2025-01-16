package com.davanok.dvnkdnd.data.di

import com.davanok.dvnkdnd.ui.navigation.host.HostViewModel
import com.davanok.dvnkdnd.ui.pages.charactersList.CharactersListViewModel
import org.koin.dsl.module

fun viewModelsModule() = module {
    single<HostViewModel> { HostViewModel() }
    single<CharactersListViewModel> { CharactersListViewModel(get()) }
}