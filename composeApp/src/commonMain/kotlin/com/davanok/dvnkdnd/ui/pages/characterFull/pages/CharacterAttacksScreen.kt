package com.davanok.dvnkdnd.ui.pages.characterFull.pages

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.davanok.dvnkdnd.domain.entities.character.CharacterItem
import com.davanok.dvnkdnd.domain.entities.dndEntities.DnDFullEntity

@Composable
fun CharacterAttacksScreen(
    items: List<CharacterItem>,
    onClick: (DnDFullEntity) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier
    ) {
        items(
            items = items,
            key = { it.item.entity.id }
        ) {

        }
    }
}