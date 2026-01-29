package com.davanok.dvnkdnd.data.di.viewModelModules

import com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.NewCharacterViewModel
import com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.loadingScreen.LoadingDataViewModel
import com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.newCharacterHealth.NewCharacterHealthViewModel
import com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.newCharacterMain.NewCharacterMainViewModel
import com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.newCharacterMain.searchSheet.SearchSheetViewModel
import com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.newCharacterAttributes.NewCharacterAttributesViewModel
import com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.newCharacterThrows.NewCharacterThrowsViewModel
import com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.savingNewCharacter.SavingNewCharacterViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

fun newCharacterViewModelsModule() = module {
    viewModelOf(::NewCharacterViewModel)

    viewModelOf(::LoadingDataViewModel)
    viewModelOf(::SearchSheetViewModel)

    viewModel { 
        NewCharacterMainViewModel(
            get(),
            get(),
            get(),
            it.get()
        )
    }
    viewModel { 
        NewCharacterAttributesViewModel(it.get())
    }
    viewModel { 
        NewCharacterThrowsViewModel(it.get())
    }
    viewModel { 
        NewCharacterHealthViewModel(it.get())
    }
    viewModel { 
        SavingNewCharacterViewModel(it.get())
    }
}