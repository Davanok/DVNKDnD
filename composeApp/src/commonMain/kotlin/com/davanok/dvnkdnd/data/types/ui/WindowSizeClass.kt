package com.davanok.dvnkdnd.data.types.ui

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import kotlin.jvm.JvmInline

class WindowSizeClass private constructor(
    val widthSizeClass: WindowWidthSizeClass,
    val heightSizeClass: WindowHeightSizeClass
) {
    companion object {
        fun calculateFromSize(
            size: DpSize,
            supportedWidthSizeClasses: List<WindowWidthSizeClass> = WindowWidthSizeClass.AllSizeClassList,
            supportedHeightSizeClasses: List<WindowHeightSizeClass> = WindowHeightSizeClass.AllSizeClassList
        ): WindowSizeClass {
            val windowWidthSizeClass =
                WindowWidthSizeClass.fromWidth(size.width, supportedWidthSizeClasses)
            val windowHeightSizeClass =
                WindowHeightSizeClass.fromHeight(size.height, supportedHeightSizeClasses)
            return WindowSizeClass(windowWidthSizeClass, windowHeightSizeClass)
        }
    }
}

@JvmInline
value class WindowWidthSizeClass private constructor(private val value: Int) :
    Comparable<WindowWidthSizeClass> {

    override operator fun compareTo(other: WindowWidthSizeClass) =
        breakpoint().compareTo(other.breakpoint())

    override fun toString(): String {
        return "WindowWidthSizeClass." +
                when (this) {
                    Compact -> "Compact"
                    Medium -> "Medium"
                    Expanded -> "Expanded"
                    Large -> "Large"
                    ExtraLarge -> "Extra-large"
                    else -> ""
                }
    }

    companion object {
        val Compact = WindowWidthSizeClass(0)
        val Medium = WindowWidthSizeClass(1)
        val Expanded = WindowWidthSizeClass(2)
        val Large = WindowWidthSizeClass(3)
        val ExtraLarge = WindowWidthSizeClass(4)

        val AllSizeClassList = listOf(ExtraLarge, Large, Expanded, Medium, Compact)

        private fun WindowWidthSizeClass.breakpoint(): Dp {
            return when {
                this == ExtraLarge -> 1600.dp
                this == Large -> 1200.dp
                this == Expanded -> 840.dp
                this == Medium -> 600.dp
                else -> 0.dp
            }
        }

        internal fun fromWidth(
            width: Dp,
            supportedSizeClasses: List<WindowWidthSizeClass>
        ): WindowWidthSizeClass {
            require(width >= 0.dp) { "Width must not be negative" }
            require(supportedSizeClasses.isNotEmpty()) { "Must support at least one size class" }
            var smallestSupportedSizeClass = Compact
            AllSizeClassList.fastForEach {
                if (it in supportedSizeClasses) {
                    if (width >= it.breakpoint()) {
                        return it
                    }
                    smallestSupportedSizeClass = it
                }
            }
            return smallestSupportedSizeClass
        }
    }
}

@JvmInline
value class WindowHeightSizeClass private constructor(private val value: Int) :
    Comparable<WindowHeightSizeClass> {

    override operator fun compareTo(other: WindowHeightSizeClass) =
        breakpoint().compareTo(other.breakpoint())

    override fun toString(): String {
        return "WindowHeightSizeClass." +
                when (this) {
                    Compact -> "Compact"
                    Medium -> "Medium"
                    Expanded -> "Expanded"
                    Large -> "Large"
                    ExtraLarge -> "Extra-large"
                    else -> ""
                }
    }

    companion object {
        val Compact = WindowHeightSizeClass(0)
        val Medium = WindowHeightSizeClass(1)
        val Expanded = WindowHeightSizeClass(2)
        val Large = WindowHeightSizeClass(3)
        val ExtraLarge = WindowHeightSizeClass(4)

        val AllSizeClassList = listOf(ExtraLarge, Large, Expanded, Medium, Compact)

        private fun WindowHeightSizeClass.breakpoint(): Dp {
            return when {
                this == ExtraLarge -> 1600.dp
                this == Large -> 1200.dp
                this == Expanded -> 900.dp
                this == Medium -> 480.dp
                else -> 0.dp
            }
        }
        internal fun fromHeight(
            height: Dp,
            supportedSizeClasses: List<WindowHeightSizeClass>
        ): WindowHeightSizeClass {
            require(height >= 0.dp) { "Width must not be negative" }
            require(supportedSizeClasses.isNotEmpty()) { "Must support at least one size class" }
            var smallestSupportedSizeClass = Expanded
            AllSizeClassList.fastForEach {
                if (it in supportedSizeClasses) {
                    if (height >= it.breakpoint()) {
                        return it
                    }
                    smallestSupportedSizeClass = it
                }
            }
            return smallestSupportedSizeClass
        }
    }
}