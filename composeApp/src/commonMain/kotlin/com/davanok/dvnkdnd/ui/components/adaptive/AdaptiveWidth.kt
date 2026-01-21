package com.davanok.dvnkdnd.ui.components.adaptive

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.allVerticalHingeBounds
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.close_side_sheet
import org.jetbrains.compose.resources.stringResource

@Composable
private fun rememberMovableContentOf(content: @Composable () -> Unit) =
    remember(content) { movableContentOf(content) }

@Composable
fun AdaptiveWidth(
    singlePaneContent: @Composable () -> Unit,
    twoPaneContent: Pair<@Composable () -> Unit, @Composable () -> Unit>,
    supportPane: (@Composable () -> Unit)? = null,
    supportPaneTitle: @Composable () -> Unit = {},
    onHideSupportPane: () -> Unit = {},
    panesSpacing: Dp = 0.dp,
    windowAdaptiveInfo: WindowAdaptiveInfo = currentWindowAdaptiveInfo(),
    modifier: Modifier = Modifier
) {
    val movableSingle = rememberMovableContentOf(singlePaneContent)
    val movablePrimary = rememberMovableContentOf(twoPaneContent.first)
    val movableSecondary = rememberMovableContentOf(twoPaneContent.second)
    val movableSupport = supportPane?.let { rememberMovableContentOf(supportPane) }

    /**
     * BoxWithConstraints provides the actual rendered width of this composable.
     * This value reflects the real container size rather than the window size class.
     */
    BoxWithConstraints(modifier = modifier) {
        val containerWidthPx = constraints.maxWidth

        /**
         * Determine the adaptive layout configuration based on:
         * - current window adaptive info
         * - container width
         * - presence of an optional support pane
         */
        val layoutConfig by remember(
            windowAdaptiveInfo,
            containerWidthPx,
            supportPane != null
        ) {
            derivedStateOf {
                calculateAdaptiveConfig(
                    windowInfo = windowAdaptiveInfo,
                    containerWidthPx = containerWidthPx,
                    hasSupportPane = supportPane != null
                )
            }
        }

        /**
         * Render the adaptive layout according to the resolved configuration.
         */
        AdaptiveLayout(
            config = layoutConfig,
            panesSpacing = panesSpacing,
            singleContent = movableSingle,
            primaryContent = movablePrimary,
            secondaryContent = movableSecondary,
            tertiaryContent = movableSupport?.let {
                {
                    SupportPane(
                        title = supportPaneTitle,
                        onHideSupportPane = onHideSupportPane,
                        content = movableSupport
                    )
                }
            }
        )

        /**
         * Show the support pane as a modal sheet when it cannot be placed inline
         * in the current layout configuration.
         */
        val showModalSupport =
            movableSupport != null &&
                    (layoutConfig is AdaptiveConfig.Single ||
                            layoutConfig is AdaptiveConfig.Split)

        if (showModalSupport) {
            AdaptiveModalSheet(
                onDismissRequest = onHideSupportPane,
                title = supportPaneTitle,
                content = movableSupport
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SupportPane(
    title: @Composable () -> Unit,
    onHideSupportPane: () -> Unit,
    content: @Composable () -> Unit
) {
    Row {
        VerticalDivider()
        Column {
            TopAppBar(
                title = title,
                actions = {
                    IconButton(onClick = onHideSupportPane) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(Res.string.close_side_sheet)
                        )
                    }
                }
            )
            content()
        }
    }
}

/**
 * Low-level custom Layout responsible for measuring and placing panes.
 *
 * The constraints passed here originate from BoxWithConstraints,
 * so constraints.maxWidth represents the actual available width.
 */
@Composable
private fun AdaptiveLayout(
    config: AdaptiveConfig,
    panesSpacing: Dp,
    singleContent: @Composable () -> Unit,
    primaryContent: @Composable () -> Unit,
    secondaryContent: @Composable () -> Unit,
    tertiaryContent: (@Composable () -> Unit)?
) {
    Layout(
        content = {
            when (config) {
                is AdaptiveConfig.Single -> Box { singleContent() }
                is AdaptiveConfig.Split,
                is AdaptiveConfig.HingeSplit -> {
                    Box { primaryContent() }
                    Box { secondaryContent() }
                }
                is AdaptiveConfig.Triple,
                is AdaptiveConfig.HingeTriple -> {
                    Box { primaryContent() }
                    Box { secondaryContent() }
                    Box { tertiaryContent?.invoke() }
                }
            }
        }
    ) { measurables, constraints ->
        val width = constraints.maxWidth
        val height = constraints.maxHeight
        val spacingPx = panesSpacing.roundToPx()

        /**
         * Measures a composable using a fixed width and full available height.
         */
        fun measureFixed(measurable: Measurable, w: Int) =
            measurable.measure(
                Constraints.fixed(
                    width = w.coerceAtLeast(0),
                    height = height
                )
            )

        val placeables = when (config) {
            AdaptiveConfig.Single -> {
                listOf(
                    PlacedPane(
                        measureFixed(measurables[0], width),
                        x = 0
                    )
                )
            }

            AdaptiveConfig.Split -> {
                // Two equal-width columns with spacing in between
                val availableWidth = width - spacingPx
                val columnWidth = availableWidth / 2

                listOf(
                    PlacedPane(
                        measureFixed(measurables[0], columnWidth),
                        x = 0
                    ),
                    PlacedPane(
                        measureFixed(measurables[1], columnWidth),
                        x = columnWidth + spacingPx
                    )
                )
            }

            AdaptiveConfig.Triple -> {
                // Three equal-width columns with spacing
                val availableWidth = width - (spacingPx * 2)
                val columnWidth = availableWidth / 3

                listOf(
                    PlacedPane(
                        measureFixed(measurables[0], columnWidth),
                        x = 0
                    ),
                    PlacedPane(
                        measureFixed(measurables[1], columnWidth),
                        x = columnWidth + spacingPx
                    ),
                    PlacedPane(
                        measureFixed(measurables[2], columnWidth),
                        x = (columnWidth + spacingPx) * 2
                    )
                )
            }

            is AdaptiveConfig.HingeSplit -> {
                /**
                 * Layout based on a physical hinge (foldable devices).
                 * Left and right pane sizes are derived from hinge coordinates.
                 */
                val leftWidth = config.gapLeftPx
                val rightStartX = config.gapRightPx
                val rightWidth = width - rightStartX

                listOf(
                    PlacedPane(
                        measureFixed(measurables[0], leftWidth),
                        x = 0
                    ),
                    PlacedPane(
                        measureFixed(measurables[1], rightWidth),
                        x = rightStartX
                    )
                )
            }

            is AdaptiveConfig.HingeTriple -> {
                /**
                 * Multi-pane layout for devices with multiple vertical hinges.
                 */
                var currentX = 0

                measurables.mapIndexed { index, measurable ->
                    val paneWidth =
                        config.panelWidthsPx.getOrElse(index) { 0 }
                    val placeable =
                        PlacedPane(
                            measureFixed(measurable, paneWidth),
                            x = currentX
                        )

                    val gap =
                        config.gapWidthsPx.getOrElse(index) { 0 }
                    currentX += paneWidth + gap
                    placeable
                }
            }
        }

        layout(width, height) {
            placeables.forEach { (placeable, x) ->
                placeable.place(x, 0)
            }
        }
    }
}

private data class PlacedPane(
    val placeable: Placeable,
    val x: Int
)

@Immutable
private sealed interface AdaptiveConfig {

    /** Single-pane layout */
    data object Single : AdaptiveConfig

    /** Two-pane layout without physical hinge */
    data object Split : AdaptiveConfig

    /** Three-pane layout without physical hinge */
    data object Triple : AdaptiveConfig

    /**
     * Two-pane layout separated by a physical vertical hinge.
     * Coordinates are expressed in pixels.
     */
    data class HingeSplit(
        val gapLeftPx: Int,
        val gapRightPx: Int
    ) : AdaptiveConfig

    /**
     * Three-pane layout with multiple vertical hinges.
     * Pane widths and gap widths are expressed in pixels.
     */
    data class HingeTriple(
        val panelWidthsPx: List<Int>,
        val gapWidthsPx: List<Int>
    ) : AdaptiveConfig
}

private fun calculateAdaptiveConfig(
    windowInfo: WindowAdaptiveInfo,
    containerWidthPx: Int,
    hasSupportPane: Boolean
): AdaptiveConfig {
    val hingeBounds =
        windowInfo.windowPosture.allVerticalHingeBounds
    val windowSizeClass =
        windowInfo.windowSizeClass

    /**
     * Foldable device handling (hinge-aware layouts).
     */
    if (hingeBounds.isNotEmpty()) {
        val sortedBounds =
            hingeBounds.sortedBy { it.left }

        // Single vertical hinge (book mode)
        if (sortedBounds.size == 1) {
            val hinge = sortedBounds.first()
            return AdaptiveConfig.HingeSplit(
                gapLeftPx = hinge.left.toInt(),
                gapRightPx = hinge.right.toInt()
            )
        }

        // Multiple hinges (rare devices)
        if (sortedBounds.size >= 2 && hasSupportPane) {
            val widths = mutableListOf<Int>()
            val gaps = mutableListOf<Int>()
            var lastX = 0

            sortedBounds.take(2).forEach { hinge ->
                widths.add(hinge.left.toInt() - lastX)
                gaps.add((hinge.right - hinge.left).toInt())
                lastX = hinge.right.toInt()
            }

            widths.add(
                (containerWidthPx - lastX).coerceAtLeast(0)
            )

            return AdaptiveConfig.HingeTriple(widths, gaps)
        }

        // Fallback to a simple hinge split
        val hinge = sortedBounds.first()
        return AdaptiveConfig.HingeSplit(
            hinge.left.toInt(),
            hinge.right.toInt()
        )
    }

    /**
     * Flat (non-foldable) screen handling.
     */
    val isExpanded =
        windowSizeClass.isWidthAtLeastBreakpoint(
            WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND
        )

    val isMedium =
        windowSizeClass.isWidthAtLeastBreakpoint(
            WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND
        )

    return when {
        isExpanded && hasSupportPane -> AdaptiveConfig.Triple
        isMedium -> AdaptiveConfig.Split
        else -> AdaptiveConfig.Single
    }
}
