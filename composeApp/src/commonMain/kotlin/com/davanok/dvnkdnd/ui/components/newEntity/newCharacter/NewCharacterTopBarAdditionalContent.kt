package com.davanok.dvnkdnd.ui.components.newEntity.newCharacter

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.davanok.dvnkdnd.data.model.entities.character.CharacterShortInfo
import com.davanok.dvnkdnd.ui.components.toSignedString
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.proficiency_bonus_value
import dvnkdnd.composeapp.generated.resources.selected_value
import org.jetbrains.compose.resources.stringResource

@Composable
fun NewCharacterTopBarAdditionalContent(
    character: CharacterShortInfo,
    content: @Composable ColumnScope.() -> Unit = {}
) {
    Column {
        Text(
            text = character.name,
            style = MaterialTheme.typography.labelMedium
        )
        Row (
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(horizontal = 8.dp)
                .widthIn(max = 488.dp)
                .fillMaxWidth(),
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
        content()
    }
}
@Composable
fun NewCharacterStatsAdditionalContent(
    selectedCount: Int,
    selectionLimit: Int,
    proficiencyBonus: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(
                Res.string.selected_value,
                selectedCount, selectionLimit
            ),
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = stringResource(
                Res.string.proficiency_bonus_value,
                proficiencyBonus.toSignedString()
            ),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
