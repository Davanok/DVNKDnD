package com.davanok.dvnkdnd.ui.pages.newEntity

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.davanok.dvnkdnd.data.model.ui.WindowWidthSizeClass
import com.davanok.dvnkdnd.ui.components.adaptive.LocalAdaptiveInfo
import com.davanok.dvnkdnd.ui.navigation.Route
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.ability
import dvnkdnd.composeapp.generated.resources.background
import dvnkdnd.composeapp.generated.resources.character
import dvnkdnd.composeapp.generated.resources.cls
import dvnkdnd.composeapp.generated.resources.custom
import dvnkdnd.composeapp.generated.resources.fantasy
import dvnkdnd.composeapp.generated.resources.fighter
import dvnkdnd.composeapp.generated.resources.fireball
import dvnkdnd.composeapp.generated.resources.homebrew
import dvnkdnd.composeapp.generated.resources.item
import dvnkdnd.composeapp.generated.resources.main
import dvnkdnd.composeapp.generated.resources.race
import dvnkdnd.composeapp.generated.resources.rogue
import dvnkdnd.composeapp.generated.resources.spell
import dvnkdnd.composeapp.generated.resources.sword
import dvnkdnd.composeapp.generated.resources.tiefling
import dvnkdnd.composeapp.generated.resources.urchin
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun NewEntityScreen(
    onNavigate: (Route) -> Unit
) {
    val adaptiveInfo = LocalAdaptiveInfo.current
    val windowSizeClass = adaptiveInfo.windowSizeClass
    val isCompact = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact

    if (isCompact) CompactContent(
        onItemClick = { onNavigate(it.route) }
    )
    else ExpandedContent(
        onItemClick = { onNavigate(it.route) }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CompactContent(
    onItemClick: (EntityItem) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
    ) {
        EntityGroup.entries.forEach { group ->
            item(
                key = group.name
            ) {
                Text(
                    modifier = Modifier.padding(bottom = 8.dp),
                    text = stringResource(group.title),
                    style = MaterialTheme.typography.headlineMedium
                )
            }
            items(
                items = group.items,
                key = { it.ordinal }
            ) { item ->
                ListItem(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onItemClick(item) },
                    headlineContent = {
                        Text(
                            text = stringResource(item.title)
                        )
                                      },
                    leadingContent = {
                        Image(
                            modifier = Modifier.size(56.dp),
                            painter = painterResource(item.image),
                            contentDescription = stringResource(item.title)
                        )
                    }
                )
            }
        }
    }
}
@Composable
fun measureTextWidth(text: String, style: TextStyle): Dp {
    val textMeasurer = rememberTextMeasurer()
    val widthInPixels = textMeasurer.measure(text, style).size.width
    return with(LocalDensity.current) { widthInPixels.toDp() }
}

@Composable
fun calculateMaxTextWidth(text: Iterable<StringResource>, style: TextStyle): Dp {
    val maxSize = text
        .map { stringResource(it) }
        .maxWithOrNull(compareBy { it.length }) !!
    return measureTextWidth(maxSize, style)
}

@Composable
private fun ExpandedContent(
    onItemClick: (EntityItem) -> Unit
) {
    var maxSize by remember { mutableStateOf(Int.MIN_VALUE) }
    val textMaxWidth = calculateMaxTextWidth(
        EntityItem.entries.map { it.title },
        MaterialTheme.typography.headlineMedium
    )

    LazyVerticalGrid(
        columns = GridCells.Adaptive(textMaxWidth + 32.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        EntityGroup.entries.forEach { group ->
            item(
                span = { GridItemSpan(maxLineSpan) },
                key = group.name
            ) {
                Text(
                    text = stringResource(group.title),
                    style = MaterialTheme.typography.headlineMedium
                )
            }
            items(
                items = group.items,
                key = { it.ordinal }
            ) { item ->
                OutlinedCard(
                    onClick = { onItemClick(item) },
                ) {
                    Image(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CardDefaults.shape),
                        painter = painterResource(item.image),
                        contentDescription = stringResource(item.title),
                    )
                    Text(
                        modifier = Modifier
                            .padding(vertical = 8.dp, horizontal = 16.dp)
                            .onSizeChanged { if (maxSize < it.width) maxSize = it.width },
                        text = stringResource(item.title),
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
            }
        }
    }
}


private enum class EntityItem(
    val title: StringResource,
    val image: DrawableResource,
    val route: Route
) {
    Character(Res.string.character, Res.drawable.fighter, Route.New.Character),
    Item(Res.string.item, Res.drawable.sword, Route.New.Item),
    Spell(Res.string.spell, Res.drawable.fireball, Route.New.Spell),
    Ability(Res.string.ability, Res.drawable.fantasy, Route.New.Ability),
    Class(Res.string.cls, Res.drawable.rogue, Route.New.Class),
    Race(Res.string.race, Res.drawable.tiefling, Route.New.Race),
    Background(Res.string.background, Res.drawable.urchin, Route.New.Background);

    companion object {
        val main = listOf(Character, Item)
        val custom = listOf(Spell, Ability)
        val homebrew = listOf(Class, Race, Background)
    }
}

private enum class EntityGroup(
    val title: StringResource,
    val items: List<EntityItem>
) {
    Main(Res.string.main, EntityItem.main),
    Custom(Res.string.custom, EntityItem.custom),
    Homebrew(Res.string.homebrew, EntityItem.homebrew)
}