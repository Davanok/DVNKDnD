package com.davanok.dvnkdnd.ui.pages.home

import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.davanok.dvnkdnd.data.platform.calculateWindowSizeClass
import com.davanok.dvnkdnd.database.entities.character.Character
import org.koin.compose.koinInject


@Composable
fun CharactersListScreen(
    viewModel: CharactersListViewModel = koinInject()
) {
    val windowSizeClass = calculateWindowSizeClass()

    LaunchedEffect(Unit) {

    }

    CharactersList()
    if (windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact)
        CharacterShortInfo()
}

@Composable
private fun CharactersList(

) {

}

@Composable
private fun CharacterShortInfo(
//    character: Character
) {

}
