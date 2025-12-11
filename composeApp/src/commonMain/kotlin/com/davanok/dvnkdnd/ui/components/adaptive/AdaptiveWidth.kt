package com.davanok.dvnkdnd.ui.components.adaptive

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.adaptive.allVerticalHingeBounds
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.util.fastForEach
import androidx.window.core.layout.WindowSizeClass

private fun Iterable<Int>.sumOf(selector: (Int) -> Float): Float {
    var result = 0f
    forEach { result += selector(it) }
    return result
}

@Composable
fun AdaptiveWidth(
    singlePaneContent: @Composable () -> Unit,
    twoPaneContent: Pair<@Composable () -> Unit, @Composable () -> Unit>,
    supportPane: (@Composable () -> Unit)? = null,
    supportPaneTitle: @Composable () -> Unit = {  },
    onHideSupportPane: () -> Unit = {  },
    panesSpacing: Dp = 0.dp,
    modifier: Modifier = Modifier
) {
    val windowAdaptiveInfo = currentWindowAdaptiveInfo(true)
    val windowSizeClass = windowAdaptiveInfo.windowSizeClass
    val windowPosture = windowAdaptiveInfo.windowPosture

    val density = LocalDensity.current
    val screenWidthDp = windowSizeClass.minWidthDp
    val screenWidthPx = with(density) { screenWidthDp.dp.toPx() }

    // We'll compute whether we actually render supportPane inline
    var willShowInlineSupport: Boolean

    when {
        windowPosture.allVerticalHingeBounds.isNotEmpty() -> {
            val bounds = windowPosture.allVerticalHingeBounds.sortedBy { it.left }

            val segmentsPx = mutableListOf<Float>()
            var start = 0f
            bounds.fastForEach { b ->
                val left = b.left
                segmentsPx.add((left - start).coerceAtLeast(0f))
                start = b.right
            }
            segmentsPx.add((screenWidthPx - start).coerceAtLeast(0f))

            val hingeWidthsPx = bounds.map { it.right - it.left }

            when {
                segmentsPx.size <= 1 || screenWidthPx <= 0f -> {
                    willShowInlineSupport = false
                    singlePaneContent()
                }

                supportPane != null && segmentsPx.size >= 3 -> {
                    willShowInlineSupport = true
                    val n = segmentsPx.size
                    val groups = if (n == 3) listOf(listOf(0), listOf(1), listOf(2))
                    else listOf(listOf(0), (1 until n - 1).toList(), listOf(n - 1))

                    val groupWidthsPx = groups.map { idxs -> idxs.sumOf { segmentsPx[it] } }
                    val spacerWidthsPx = mutableListOf<Float>()
                    for (g in 0 until groups.size - 1) {
                        val endIdx = groups[g].last()
                        val startNext = groups[g + 1].first()
                        val hingeRange = endIdx until startNext
                        val sumHinges = hingeRange.sumOf { hingeWidthsPx.getOrNull(it) ?: 0f }
                        spacerWidthsPx.add(sumHinges)
                    }

                    Row(modifier = modifier) {
                        // order: main left, main middle, support (edge/right)
                        val panes = listOf(twoPaneContent.first, twoPaneContent.second, supportPane)
                        panes.forEachIndexed { index, pane ->
                            val wDp = with(density) { groupWidthsPx[index].coerceAtLeast(0f).toDp() }

                            Box(modifier = Modifier.width(wDp)) { pane.invoke() }

                            if (index < spacerWidthsPx.size) {
                                val sDp =
                                    with(density) { spacerWidthsPx[index].coerceAtLeast(0f).toDp() }

                                Spacer(modifier = Modifier.width(max(panesSpacing, sDp)))
                            }
                        }
                    }
                }

                segmentsPx.size >= 2 -> {
                    willShowInlineSupport = false
                    val total = segmentsPx.sum()
                    var cum = 0f
                    var splitSegmentIndex = 0
                    for (i in 0 until segmentsPx.size - 1) {
                        cum += segmentsPx[i]
                        if (cum >= total / 2f) {
                            splitSegmentIndex = i
                            break
                        }
                    }

                    val leftIdxs = 0..splitSegmentIndex
                    val rightIdxs = (splitSegmentIndex + 1) until segmentsPx.size
                    val leftPx = leftIdxs.sumOf { segmentsPx[it] }
                    val rightPx = rightIdxs.sumOf { segmentsPx[it] }
                    val spacerPx =
                        if (splitSegmentIndex in hingeWidthsPx.indices) hingeWidthsPx[splitSegmentIndex] else 0f

                    Row(modifier = modifier) {
                        val leftDp = with(density) { leftPx.coerceAtLeast(0f).toDp() }
                        val rightDp = with(density) { rightPx.coerceAtLeast(0f).toDp() }
                        Box(modifier = Modifier.width(leftDp)) { twoPaneContent.first() }

                        val sDp = with(density) { spacerPx.coerceAtLeast(0f).toDp() }

                        Spacer(modifier = Modifier.width(max(panesSpacing, sDp)))

                        Box(modifier = Modifier.width(rightDp)) { twoPaneContent.second() }
                    }
                }

                else -> {
                    willShowInlineSupport = false
                    singlePaneContent()
                }
            }
        }

        windowSizeClass.isWidthAtLeastBreakpoint(WIDTH_DP_LARGE_LOWER_BOUND) -> {
            // expanded breakpoint: if supportPane present -> render it inline on the right edge
            willShowInlineSupport = true

            Row(
                modifier = modifier,
                horizontalArrangement = Arrangement.spacedBy(panesSpacing)
            ) {
                Box(modifier = Modifier.weight(1f)) { twoPaneContent.first() }
                Box(modifier = Modifier.weight(1f)) { twoPaneContent.second() }
                AnimatedVisibility(
                    visible = supportPane != null
                ) {
                    Box(modifier = Modifier.weight(1f)) { supportPane?.invoke() }
                }
            }
        }

        windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND) -> {
            willShowInlineSupport = false
            Row(
                modifier = modifier,
                horizontalArrangement = Arrangement.spacedBy(panesSpacing)
            ) {
                Box(modifier = Modifier.weight(1f)) { twoPaneContent.first() }
                Box(modifier = Modifier.weight(1f)) { twoPaneContent.second() }
            }
        }

        else -> {
            willShowInlineSupport = false
            singlePaneContent()
        }
    }

    if (supportPane != null && !willShowInlineSupport)
        AdaptiveModalSheet(
            onDismissRequest = onHideSupportPane,
            title = supportPaneTitle,
            content = supportPane
        )
}