package com.davanok.dvnkdnd.ui.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.MenuItemColors
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collection.mutableVectorOf
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectableTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    label: (@Composable () -> Unit)? = null,
    placeholder: (@Composable () -> Unit)? = null,
    dropdownMenuContent: ExposedDropdownMenuScope.() -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            modifier = modifier.menuAnchor(MenuAnchorType.PrimaryEditable),
            value = value,
            onValueChange = onValueChange,
            label = label,
            placeholder = placeholder,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expanded
                )
            }
        )
        val content = rememberStateOfContent(dropdownMenuContent)
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            content = {
                content.value.items.forEach { item ->
                    DropdownMenuItem(
                        item.text,
                        {
                            item.onClick()
                            expanded = false
                        },
                        item.modifier,
                        item.leadingIcon,
                        item.trailingIcon,
                        item.enabled,
                        item.colors?: MenuDefaults.itemColors(),
                        item.contentPadding,
                        item.interactionSource
                    )
                }
            }
        )
    }
}

@Composable
private fun rememberStateOfContent(
    content: ExposedDropdownMenuScope.() -> Unit
): State<ExposedDropdownMenuScope> {
    val latestContent = rememberUpdatedState(content)
    return remember {
        derivedStateOf { ExposedDropdownMenuScope().apply(latestContent.value) }
    }
}

class ExposedDropdownMenuScope {
    data class ExposedDropdownMenuItem(
        val text: @Composable () -> Unit,
        val onClick: () -> Unit,
        val modifier: Modifier,
        val leadingIcon: @Composable (() -> Unit)?,
        val trailingIcon: @Composable (() -> Unit)?,
        val enabled: Boolean,
        val colors: MenuItemColors?,
        val contentPadding: PaddingValues,
        val interactionSource: MutableInteractionSource?,
    )
    val items = mutableVectorOf<ExposedDropdownMenuItem>()
    fun item(
        text: @Composable () -> Unit,
        onClick: () -> Unit,
        modifier: Modifier = Modifier,
        leadingIcon: @Composable (() -> Unit)? = null,
        trailingIcon: @Composable (() -> Unit)? = null,
        enabled: Boolean = true,
        colors: MenuItemColors? = null,
        contentPadding: PaddingValues = MenuDefaults.DropdownMenuItemContentPadding,
        interactionSource: MutableInteractionSource? = null,
    ) {
        items.add(
            ExposedDropdownMenuItem(
                text,
                onClick,
                modifier,
                leadingIcon,
                trailingIcon,
                enabled,
                colors,
                contentPadding,
                interactionSource
            )
        )
    }
}