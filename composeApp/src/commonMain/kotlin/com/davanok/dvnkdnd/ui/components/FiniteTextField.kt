package com.davanok.dvnkdnd.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.cls
import dvnkdnd.composeapp.generated.resources.more
import io.github.aakira.napier.Napier
import org.jetbrains.compose.resources.stringResource
import kotlin.collections.component1
import kotlin.collections.component2


@Composable
fun <T> FiniteTextField(
    value: T? = null,
    entities: List<T>,
    toString: (T) -> String,
    onSelected: (T?) -> Unit,
    modifier: Modifier = Modifier,
    onNeedMore: ((String) -> Unit)? = null,
    label: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    supportingText: @Composable (() -> Unit)? = null
) {
    var text by remember { mutableStateOf("") }
    val entitiesMap = remember(entities) {
        entities.associateWith(toString)
    }
    val filteredItems by remember(entities, text) {
        derivedStateOf {
            entitiesMap.filterValues { it.startsWith(text, ignoreCase = true) }
        }
    }

    SelectableTextField(
        modifier = modifier,
        value = if (value == null) text else toString(value),
        onValueChange = { newText ->
            text = newText
            val match = entitiesMap.entries
                .firstOrNull { it.value.equals(newText, ignoreCase = true) }
                ?.key
            if (match != value) onSelected(match)
        },
        label = label,
        isError = isError,
        supportingText = supportingText
    ) {
        filteredItems.forEach { (key, name) ->
            item(
                text = { Text(name) },
                onClick = {
                    text = name
                    if (value != key) onSelected(key)
                }
            )
        }
        if (onNeedMore != null)
            item(
                text = {
                    Text(stringResource(Res.string.more), color = MaterialTheme.colorScheme.primary)
                       },
                onClick = { onNeedMore(text) },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.ArrowForward,
                        contentDescription = stringResource(Res.string.more)
                    )
                }
            )
    }
}