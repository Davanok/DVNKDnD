package com.davanok.dvnkdnd.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.davanok.dvnkdnd.domain.entities.character.CharacterBase
import com.davanok.dvnkdnd.domain.entities.dndEntities.EntityBase

@Composable
private fun BaseEntityImageContent(
    image: Any?,
    name: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        if (image != null)
            AsyncImage(
                modifier = Modifier.fillMaxSize(),
                model = image,
                contentDescription = null
            )
        else {
            val text = remember(name) {
                Regex("(?<!\\p{L})\\p{L}")
                    .findAll(name)
                    .take(2)
                    .joinToString("") { it.value.uppercase() }
                    .ifBlank { "*" }
            }
            Text(
                modifier = Modifier
                    .padding(4.dp)
                    .fillMaxSize(),
                text = text,
                style = MaterialTheme.typography.titleLarge,
                autoSize = TextAutoSize.StepBased()
            )
        }
    }
}

@Composable
fun BaseEntityImage(
    image: Any?,
    name: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        onClick = onClick,
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.primaryContainer
    ) {
        BaseEntityImageContent(
            image = image,
            name = name,
            modifier = Modifier.fillMaxSize()
        )
    }
}
@Composable
fun BaseEntityImage(
    image: Any?,
    name: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.primaryContainer
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (image != null)
                AsyncImage(
                    modifier = Modifier.fillMaxSize(),
                    model = image,
                    contentDescription = null
                )
            else
                Text(
                    text = name.firstOrNull()?.toString() ?: "*",
                    style = MaterialTheme.typography.titleLarge,
                    autoSize = TextAutoSize.StepBased()
                )
        }
    }
}

@Composable
fun BaseEntityImage(
    entity: EntityBase,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) = BaseEntityImage(
    image = entity.image,
    name = entity.name,
    onClick = onClick,
    modifier = modifier
)

@Composable
fun BaseEntityImage(
    entity: EntityBase,
    modifier: Modifier = Modifier
) = BaseEntityImage(
    image = entity.image,
    name = entity.name,
    modifier = modifier
)

@Composable
fun BaseEntityImage(
    character: CharacterBase,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) = BaseEntityImage(
    image = character.image,
    name = character.name,
    onClick = onClick,
    modifier = modifier
)

@Composable
fun BaseEntityImage(
    character: CharacterBase,
    modifier: Modifier = Modifier
) = BaseEntityImage(
    image = character.image,
    name = character.name,
    modifier = modifier
)