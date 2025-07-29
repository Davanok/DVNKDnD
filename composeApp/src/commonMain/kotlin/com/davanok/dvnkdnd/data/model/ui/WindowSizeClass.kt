package com.davanok.dvnkdnd.data.model.ui

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import kotlin.jvm.JvmInline

fun WindowSizeClass.isCompact(): Boolean =
    widthSizeClass <= WindowWidthSizeClass.Compact || heightSizeClass <= WindowHeightSizeClass.Compact

class WindowSizeClass(
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
                    Small -> "Small"
                    Compact -> "Compact"
                    Medium -> "Medium"
                    Expanded -> "Expanded"
                    Large -> "Large"
                    ExtraLarge -> "Extra-large"
                    else -> ""
                }
    }

    companion object {
        val Small = WindowWidthSizeClass(0)
        val Compact = WindowWidthSizeClass(1)
        val Medium = WindowWidthSizeClass(2)
        val Expanded = WindowWidthSizeClass(3)
        val Large = WindowWidthSizeClass(4)
        val ExtraLarge = WindowWidthSizeClass(5)

        val AllSizeClassList = listOf(ExtraLarge, Large, Expanded, Medium, Compact, Small)

        private fun WindowWidthSizeClass.breakpoint(): Dp {
            return when {
                this == ExtraLarge -> 1600.dp
                this == Large -> 1200.dp
                this == Expanded -> 840.dp
                this == Medium -> 600.dp
                this == Compact -> 400.dp
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
                    Small -> "Small"
                    Compact -> "Compact"
                    Medium -> "Medium"
                    Expanded -> "Expanded"
                    Large -> "Large"
                    ExtraLarge -> "Extra-large"
                    else -> ""
                }
    }

    companion object {
        val Small = WindowHeightSizeClass(0)
        val Compact = WindowHeightSizeClass(1)
        val Medium = WindowHeightSizeClass(2)
        val Expanded = WindowHeightSizeClass(3)
        val Large = WindowHeightSizeClass(4)
        val ExtraLarge = WindowHeightSizeClass(5)

        val AllSizeClassList = listOf(ExtraLarge, Large, Expanded, Medium, Compact, Small)

        private fun WindowHeightSizeClass.breakpoint(): Dp {
            return when {
                this == ExtraLarge -> 1600.dp
                this == Large -> 1200.dp
                this == Expanded -> 900.dp
                this == Medium -> 480.dp
                this == Compact -> 320.dp
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