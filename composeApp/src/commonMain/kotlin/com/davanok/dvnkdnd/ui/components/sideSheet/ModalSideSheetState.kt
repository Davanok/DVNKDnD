package com.davanok.dvnkdnd.ui.components.sideSheet

import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.gestures.snapTo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable

@Stable
class SheetState(
    initialValue: SideSheetValue = SideSheetValue.Hidden,
) {
    val currentValue: SideSheetValue
        get() = anchoredDraggableState.currentValue


    val targetValue: SideSheetValue
        get() = anchoredDraggableState.targetValue


    val isVisible: Boolean
        get() = anchoredDraggableState.currentValue != SideSheetValue.Hidden
    val isHidden: Boolean
        get() = anchoredDraggableState.currentValue == SideSheetValue.Hidden


    fun requireOffset(): Float = anchoredDraggableState.requireOffset()


    suspend fun show() {
        animateTo(SideSheetValue.Expanded)
    }


    suspend fun hide() {
        animateTo(SideSheetValue.Hidden)
    }


    suspend fun animateTo(targetValue: SideSheetValue) {
        anchoredDraggableState.animateTo(targetValue)
    }


    suspend fun snapTo(targetValue: SideSheetValue) {
        anchoredDraggableState.snapTo(targetValue)
    }

    var anchoredDraggableState = AnchoredDraggableState(initialValue)

    val offset: Float
        get() = anchoredDraggableState.offset

    companion object {
        fun Saver() = Saver<SheetState, SideSheetValue>(
            save = { it.currentValue },
            restore = { savedValue ->
                SheetState(
                    savedValue,
                )
            })
    }
}

enum class SideSheetValue {
    /** The sheet is not visible. */
    Hidden,

    /** The sheet is visible at full width. */
    Expanded
}

@Composable
fun rememberSheetState(
    initialValue: SideSheetValue = SideSheetValue.Hidden,
    skipHiddenState: Boolean = false,
): SheetState {
    return rememberSaveable(
        skipHiddenState, saver = SheetState.Saver()
    ) {
        SheetState(initialValue = initialValue)
    }
}

@Composable
fun rememberModalSideSheetState(
) = rememberSheetState(
    initialValue = SideSheetValue.Hidden,
)

data class ModalSideSheetProperties(
    val shouldDismissOnBackPress: Boolean = true,
    val shouldDismissOnClickOutside: Boolean = true
)