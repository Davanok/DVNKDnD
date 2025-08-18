package com.davanok.dvnkdnd.data.di.viewModelModules

import com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.NewCharacterViewModel
import com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.loadingScreen.LoadingDataViewModel
import com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.newCharacterMain.NewCharacterMainViewModel
import com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.newCharacterMain.searchSheet.SearchSheetViewModel
import com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.newCharacterSavingThrows.NewCharacterSavingThrowsViewModel
import com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.newCharacterSkills.NewCharacterSkillsViewModel
import com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.newCharacterStats.NewCharacterStatsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

fun newCharacterViewModelsModule() = module {
    viewModelOf(::NewCharacterViewModel)

    viewModelOf(::LoadingDataViewModel)
    viewModelOf(::SearchSheetViewModel)

    viewModel { (vm: NewCharacterViewModel) ->
        NewCharacterMainViewModel(
            get(),
            get(),
            get(),
            get(),
            vm
        )
    }
    viewModel { (vm: NewCharacterViewModel) ->
        NewCharacterStatsViewModel(vm)
    }
    viewModel { (vm: NewCharacterViewModel) ->
        NewCharacterSkillsViewModel(vm)
    }
    viewModel { (vm: NewCharacterViewModel) ->
        NewCharacterSavingThrowsViewModel(vm)
    }
}