package com.davanok.dvnkdnd.ui.pages.characterFull.dialogs

import androidx.compose.runtime.Composable
import com.davanok.dvnkdnd.domain.enums.dndEnums.DnDEntityTypes
import com.davanok.dvnkdnd.ui.pages.characterFull.CharacterAddEntityViewModel
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun CharacterAddItemDialogContent(
    viewModel: CharacterAddEntityViewModel = koinViewModel { parametersOf(DnDEntityTypes.ITEM) }
) {

}