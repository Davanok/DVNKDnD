package com.davanok.dvnkdnd.ui.components.adaptive

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.adaptive.allVerticalHingeBounds
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
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
    threePaneContent: Triple<@Composable () -> Unit, @Composable () -> Unit, @Composable () -> Unit>? = null,
    modifier: Modifier = Modifier
) {
    val windowAdaptiveInfo = currentWindowAdaptiveInfo()
    val windowSizeClass = windowAdaptiveInfo.windowSizeClass
    val windowPosture = windowAdaptiveInfo.windowPosture

    val density = LocalDensity.current
    val screenWidthDp = windowSizeClass.minWidthDp
    val screenWidthPx = with(density) { screenWidthDp.dp.toPx() }

    when {
        windowPosture.allVerticalHingeBounds.isNotEmpty() -> {
            val bounds = windowPosture.allVerticalHingeBounds.sortedBy { it.left }

            // segments between hinges: left edge -> first.hinge.left, between hinges, last.hinge.right -> right edge
            val segmentsPx = mutableListOf<Float>()
            var start = 0f
            bounds.fastForEach { b ->
                val left = b.left
                segmentsPx.add((left - start).coerceAtLeast(0f))
                start = b.right
            }
            segmentsPx.add((screenWidthPx - start).coerceAtLeast(0f))

            if (segmentsPx.size <= 1 || screenWidthPx <= 0f) {
                singlePaneContent()
                return
            }

            // helper: hinge widths (between segments) in px, hinge i is between segment i and i+1
            val hingeWidthsPx = bounds.map { it.right - it.left }

            // Build groups of segment indices for panels.
            // If we have threePaneContent -> prefer 3 groups (merge middle if more than 3 segments).
            // If threePaneContent == null -> build 2 groups: choose hinge nearest center to align.
            if (threePaneContent != null && segmentsPx.size >= 3) {
                // groups: [0], [1..n-2], [n-1] when segments > 3; if exactly 3 -> [0],[1],[2]
                val n = segmentsPx.size
                val groups = if (n == 3) {
                    listOf(listOf(0), listOf(1), listOf(2))
                } else {
                    val middle = (1 until n - 1).toList()
                    listOf(listOf(0), middle, listOf(n - 1))
                }

                // compute group widths and spacer widths between groups
                val groupWidthsPx = groups.map { idxs -> idxs.sumOf { segmentsPx[it] } }
                val spacerWidthsPx = mutableListOf<Float>()
                for (g in 0 until groups.size - 1) {
                    val endIdx = groups[g].last()
                    val startNext = groups[g + 1].first()
                    // hinge indices between endIdx and startNext - 1 inclusive
                    val hingeRange = endIdx until startNext
                    val sumHinges = hingeRange.sumOf { hingeWidthsPx.getOrNull(it) ?: 0f }
                    spacerWidthsPx.add(sumHinges)
                }

                Row(modifier = modifier) {
                    val panes = listOf(threePaneContent.first, threePaneContent.second, threePaneContent.third)
                    for (i in panes.indices) {
                        val wDp = with(density) { groupWidthsPx[i].coerceAtLeast(0f).toDp() }
                        Box(modifier = Modifier.width(wDp)) { panes[i]() }
                        if (i < spacerWidthsPx.size) {
                            val sDp = with(density) { spacerWidthsPx[i].coerceAtLeast(0f).toDp() }
                            if (sDp.value > 0f) Spacer(modifier = Modifier.width(sDp))
                        }
                    }
                }
                return
            }

            // else: produce two panes. If there are multiple segments, choose hinge index that makes left ~ half (to align boundary),
            // else fallback to equal widths using weight.
            if (segmentsPx.size >= 2) {
                // cumulative sum of segment widths to find split point
                val total = segmentsPx.sum()
                var cum = 0f
                var splitSegmentIndex = 0 // split after this segment (i.e., left=0..splitSegmentIndex, right=splitSegmentIndex+1..end)
                for (i in 0 until segmentsPx.size - 1) { // don't consider splitting after last segment
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
                // spacer width: sum of hinge widths between the two groups (should be hinge at index = splitSegmentIndex)
                val spacerPx = if (splitSegmentIndex in hingeWidthsPx.indices) hingeWidthsPx[splitSegmentIndex] else 0f

                // render two panes: prefer twoPaneContent; if threePaneContent present but segments==2, use twoPaneContent too.
                Row(modifier = modifier) {
                    val leftDp = with(density) { leftPx.coerceAtLeast(0f).toDp() }
                    val rightDp = with(density) { rightPx.coerceAtLeast(0f).toDp() }
                    Box(modifier = Modifier.width(leftDp)) { twoPaneContent.first() }

                    val sDp = with(density) { spacerPx.coerceAtLeast(0f).toDp() }
                    if (sDp.value > 0f) Spacer(modifier = Modifier.width(sDp))

                    Box(modifier = Modifier.width(rightDp)) { twoPaneContent.second() }
                }
                return
            }

            // fallback
            singlePaneContent()
        }

        windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND) ->
            Row(modifier = modifier) {
                val entries = listOfNotNull(
                    threePaneContent?.first,
                    threePaneContent?.second,
                    threePaneContent?.third
                )
                if (entries.size == 3) {
                    entries.fastForEach { pane ->
                        Box(modifier = Modifier.weight(1f)) { pane() }
                    }
                } else {
                    // if no three pane, show two pane equal
                    Box(modifier = Modifier.weight(1f)) { twoPaneContent.first() }
                    Box(modifier = Modifier.weight(1f)) { twoPaneContent.second() }
                }
            }
        windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND) ->
            Row(modifier = modifier) {
                Box(modifier = Modifier.weight(1f)) { twoPaneContent.first() }
                Box(modifier = Modifier.weight(1f)) { twoPaneContent.second() }
            }
        else -> singlePaneContent()
    }
}
