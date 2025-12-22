package com.davanok.dvnkdnd.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.more
import org.jetbrains.compose.resources.stringResource


@Composable
fun <T> FiniteTextField(
    value: T?,
    entities: List<T>,
    toString: (T) -> String,
    onSelected: (T?) -> Unit,
    modifier: Modifier = Modifier,
    onNeedMore: ((String) -> Unit)? = null,
    label: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    supportingText: @Composable (() -> Unit)? = null,
    singleLine: Boolean = true
) {
    var textFieldValue by remember(value) {
        mutableStateOf(
            value?.let {
                val string = toString(value)
                TextFieldValue(text = string, selection = TextRange(string.length))
            } ?: TextFieldValue()
        )
    }

    val filteredItems by remember(entities, value, value) {
        derivedStateOf {
            val query = textFieldValue.text.trim()
            if (value != null && toString(value) == query) {
                entities
            } else {
                entities.filter { toString(it).contains(query, ignoreCase = true) }
            }
        }
    }



    SelectableTextField(
        modifier = modifier,
        value = textFieldValue,
        onValueChange = { newValue ->
            textFieldValue = newValue
            val exactMatch = entities.firstOrNull {
                toString(it).equals(newValue.text, ignoreCase = true)
            }
            if (exactMatch != value)
                onSelected(exactMatch)

        },
        label = label,
        isError = isError,
        supportingText = supportingText,
        singleLine = singleLine
    ) { onDismiss ->
        filteredItems.forEach { entity ->
            val name = toString(entity)
            DropdownMenuItem(
                text = { Text(name) },
                onClick = {
                    onSelected(entity)
                    onDismiss()
                }
            )
        }
        if (onNeedMore != null) {
            DropdownMenuItem(
                text = {
                    Text(
                        text = stringResource(Res.string.more),
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.labelLarge
                    )
                },
                onClick = {
                    onNeedMore(textFieldValue.text)
                    onDismiss()
                },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.ArrowForward,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            )
        }
    }
}