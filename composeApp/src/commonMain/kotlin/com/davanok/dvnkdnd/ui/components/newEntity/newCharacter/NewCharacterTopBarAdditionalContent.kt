package com.davanok.dvnkdnd.ui.components.newEntity.newCharacter

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.davanok.dvnkdnd.data.model.entities.character.CharacterShortInfo

@Composable
fun NewCharacterTopBarAdditionalContent(character: CharacterShortInfo) {
    Row (
        modifier = Modifier.padding(horizontal = 8.dp).fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        character.className?.let {
            Text(it)
        }
        character.raceName?.let {
            Text(it)
        }
        character.backgroundName?.let {
            Text(it)
        }
    }
}
