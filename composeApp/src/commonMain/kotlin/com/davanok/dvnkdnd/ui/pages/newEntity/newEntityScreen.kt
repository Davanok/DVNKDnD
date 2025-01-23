package com.davanok.dvnkdnd.ui.pages.newEntity

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.davanok.dvnkdnd.ui.navigation.Route
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.ability
import dvnkdnd.composeapp.generated.resources.background
import dvnkdnd.composeapp.generated.resources.character
import dvnkdnd.composeapp.generated.resources.cls
import dvnkdnd.composeapp.generated.resources.fighter
import dvnkdnd.composeapp.generated.resources.sword
import dvnkdnd.composeapp.generated.resources.spell
import dvnkdnd.composeapp.generated.resources.rogue
import dvnkdnd.composeapp.generated.resources.item
import dvnkdnd.composeapp.generated.resources.race
import dvnkdnd.composeapp.generated.resources.tiefling
import dvnkdnd.composeapp.generated.resources.fireball
import dvnkdnd.composeapp.generated.resources.urchin
import dvnkdnd.composeapp.generated.resources.fantasy
import dvnkdnd.composeapp.generated.resources.custom
import dvnkdnd.composeapp.generated.resources.homebrew
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource


private enum class NewEntityNavigationItems(
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
fun NewEntityScreen(
    onNavigate: (Route) -> Unit
) {
    val textWidth = calculateMaxTextWidth(
        NewEntityNavigationItems.entries.map { it.title },
        MaterialTheme.typography.headlineMedium
    )
    LazyVerticalGrid(
        modifier = Modifier.fillMaxSize(),
        columns = GridCells.Adaptive(minSize = textWidth + 40.dp)
            // text width + grid item padding * 2 + card text padding * 2
    ) {
        items(
            items = NewEntityNavigationItems.main,
            key = { it.ordinal }
        ) { item ->
            LazyGridItem(
                modifier = Modifier
                    .padding(4.dp),
                item = item,
                onClick = { onNavigate(it.route) }
            )
        }
        item(
            key = "custom",
            span = { GridItemSpan(maxLineSpan) }
        ) {
            HeaderItem(
                modifier = Modifier.padding(4.dp),
                text = Res.string.custom
            )
        }
        items(
            items = NewEntityNavigationItems.custom,
            key = { it.ordinal }
        ) { item ->
            LazyGridItem(
                modifier = Modifier
                    .padding(4.dp),
                item = item,
                onClick = { onNavigate(it.route) }
            )
        }
        item(
            key = "homebrew",
            span = { GridItemSpan(maxLineSpan) }
        ) {
            HeaderItem(
                modifier = Modifier.padding(4.dp),
                text = Res.string.homebrew
            )
        }
        items(
            items = NewEntityNavigationItems.homebrew,
            key = { it.ordinal }
        ) { item ->
            LazyGridItem(
                modifier = Modifier
                    .padding(4.dp),
                item = item,
                onClick = { onNavigate(it.route) }
            )
        }
    }
}

@Composable
private fun LazyGridItem(
    item: NewEntityNavigationItems,
    onClick: (NewEntityNavigationItems) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedCard(
        modifier = modifier,
        onClick = { onClick(item) }
    ) {
        Image(
            modifier = Modifier
                .clip(CardDefaults.outlinedShape)
                .aspectRatio(1f)
                .fillMaxSize(),
            painter = painterResource(item.image),
            contentDescription = stringResource(item.title)
        )
        Text(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            text = stringResource(item.title),
            style = MaterialTheme.typography.headlineMedium
        )
    }
}

@Composable
private fun HeaderItem(
    text: StringResource,
    modifier: Modifier = Modifier
) {
    Text(
        modifier = modifier,
        text = stringResource(text),
        style = MaterialTheme.typography.headlineMedium
    )
}