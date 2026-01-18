package com.davanok.dvnkdnd.ui.pages.characterFull.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.RichTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.davanok.dvnkdnd.domain.entities.character.CharacterAttack
import com.davanok.dvnkdnd.domain.entities.dndEntities.ItemProperty
import com.davanok.dvnkdnd.ui.components.BaseEntityImage
import com.davanok.dvnkdnd.ui.components.FullScreenCard
import com.davanok.dvnkdnd.ui.components.text.buildDamagesString
import com.davanok.dvnkdnd.ui.components.text.buildName
import com.davanok.dvnkdnd.ui.components.toSignedString
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.attack_bonus_short_value
import dvnkdnd.composeapp.generated.resources.character_has_no_attacks
import org.jetbrains.compose.resources.stringResource

@Composable
fun CharacterAttacksScreen(
    attacks: List<CharacterAttack>,
    modifier: Modifier = Modifier,
) {
    if (attacks.isEmpty())
        FullScreenCard(modifier = modifier) {
            Text(text = stringResource(Res.string.character_has_no_attacks))
        }
    else
        LazyColumn(
            modifier = modifier,
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(
                items = attacks,
                key = { it.source.item.entity.id }
            ) { attack ->
                AttackCard(
                    attack = attack,
                    onClick = { },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
}

@Composable
private fun AttackCard(
    attack: CharacterAttack,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedCard(
        modifier = modifier,
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
        ) {
            Row {
                BaseEntityImage(
                    entity = attack.source.item.toDnDEntityMin(),
                    modifier = Modifier
                        .size(56.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text =  attack.source.item.entity.name,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = stringResource(
                            Res.string.attack_bonus_short_value,
                            attack.attackBonus.toSignedString()
                        ),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = buildDamagesString(attack.damages),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            if (!attack.source.item.item?.properties.isNullOrEmpty())
                ItemPropertiesRow(
                    attack.source.item.item.properties
                )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ItemPropertiesRow(
    properties: List<ItemProperty>,
    modifier: Modifier = Modifier,
) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        properties.forEach { property ->
            val propertyName = property.buildName()
            TooltipBox(
                positionProvider = TooltipDefaults.rememberTooltipPositionProvider(
                    TooltipAnchorPosition.Above
                ),
                tooltip = {
                    RichTooltip(
                        title = { Text(text = propertyName) }
                    ) {
                        Text(text = property.description)
                    }
                },
                state = rememberTooltipState()
            ) {
                AssistChip(
                    onClick = { /* noop */ },
                    label = {
                        Text(
                            text = propertyName,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                )
            }
        }
    }
}