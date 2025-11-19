package com.davanok.dvnkdnd.ui.pages.characterFull.contents

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import com.davanok.dvnkdnd.data.model.dndEnums.Attributes
import com.davanok.dvnkdnd.data.model.dndEnums.skills
import com.davanok.dvnkdnd.data.model.entities.dndModifiers.AttributesGroup
import com.davanok.dvnkdnd.data.model.entities.dndModifiers.SkillsGroup
import com.davanok.dvnkdnd.data.model.util.calculateModifier
import com.davanok.dvnkdnd.ui.components.toSignedString
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.saving_throw
import dvnkdnd.composeapp.generated.resources.skills
import org.jetbrains.compose.resources.stringResource

private val StatItemMinWidth = 200.dp

@Composable
fun CharacterFullAttributesScreen(
    attributes: AttributesGroup,
    savingThrows: AttributesGroup,
    skills: SkillsGroup
) {
    var itemsMaxHeight by remember { mutableIntStateOf(0) }
    val density = LocalDensity.current
    LazyVerticalGrid(
        modifier = Modifier.fillMaxSize(),
        columns = GridCells.Adaptive(StatItemMinWidth),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(4.dp)
    ) {
        items(
            items = Attributes.entries,
            key = { it }
        ) { attribute ->
            AttributeItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .then(
                        if (itemsMaxHeight > 0) Modifier.height(density.run { itemsMaxHeight.toDp() })
                        else Modifier
                    )
                    .onGloballyPositioned {
                        val h = it.size.height
                        if (h > itemsMaxHeight) itemsMaxHeight = h
                    },
                attribute = attribute,
                attributeValue = attributes[attribute],
                savingThrowValue = savingThrows[attribute],
                skillsValues = skills
            )
        }
    }
}

@Composable
private fun AttributeItem(
    attribute: Attributes,
    attributeValue: Int,
    savingThrowValue: Int,
    skillsValues: SkillsGroup,
    modifier: Modifier = Modifier
) {
    val calculatedModifier = remember(attributeValue) { calculateModifier(attributeValue) }

    OutlinedCard(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            // header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(attribute.stringRes),
                    style = MaterialTheme.typography.headlineSmall,
                    maxLines = 1
                )
                Text(
                    modifier = Modifier,
                    text = calculatedModifier.toSignedString(),
                    style = MaterialTheme.typography.headlineSmall,
                    maxLines = 1
                )
            }
            // saving throw: value
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(Res.string.saving_throw))
                Text(
                    text = savingThrowValue.toSignedString(),
                    style = MaterialTheme.typography.titleMedium
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            // skills
            Text(
                text = stringResource(Res.string.skills),
                style = MaterialTheme.typography.labelMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                attribute.skills().fastForEach { skill ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(24.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = stringResource(skill.stringRes),
                            maxLines = 1
                        )
                        Text(
                            text = skillsValues[skill].toSignedString(),
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}