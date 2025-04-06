package com.davanok.dvnkdnd.data.di

import com.davanok.dvnkdnd.ui.pages.charactersList.CharactersListViewModel
import com.davanok.dvnkdnd.ui.pages.dndEntityInfo.DnDEntityInfoViewModel
import com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.newCharacterMain.NewCharacterMainViewModel
import com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.newCharacterStats.NewCharacterStatsViewModel
import com.davanok.dvnkdnd.ui.pages.splashScreen.SplashViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

fun viewModelsModule() = module {
    viewModelOf(::SplashViewModel)

    viewModelOf(::CharactersListViewModel)

    viewModelOf(::NewCharacterMainViewModel)
    viewModelOf(::NewCharacterStatsViewModel)

    viewModelOf(::DnDEntityInfoViewModel)
}