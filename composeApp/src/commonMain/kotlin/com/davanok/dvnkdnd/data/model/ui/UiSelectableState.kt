package com.davanok.dvnkdnd.data.model.ui

data class UiSelectableState(
    val fixedSelection: Boolean,
    val selected: Boolean,
    val selectable: Boolean
) {
    companion object {
        val OfFalse = UiSelectableState(
            fixedSelection = false, selected = false, selectable = false
        )
    }
}