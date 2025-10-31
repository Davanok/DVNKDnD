package com.davanok.dvnkdnd.ui.components.adaptive

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateSetOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateSet
import androidx.compose.ui.Modifier
import com.davanok.dvnkdnd.data.platform.supportsWindows
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.app_name
import org.jetbrains.compose.resources.stringResource

/**
 * Represents a support entry that can be shown either as an in-pane support
 * or as a separate window, depending platform support.
 */
data class SupportEntry(
    val title: String? = null,
    val ignoreWindows: Boolean = false,
    val content: @Composable () -> Unit
)

/**
 * Holds state of adaptive support contents keyed by T.
 * The decision to show as a window or in-pane
 * and platform capability (supportsWindows()).
 */
class AdaptiveContentState<T>(
    val useWindows: Boolean = supportsWindows(),
    private val contentProvider: (T) -> SupportEntry? = { null }
) {
    private val _contents: SnapshotStateSet<T> = mutableStateSetOf()

    /**
     * Show content for [key]. If the corresponding entry prefers windows and
     * the platform supports it, content is added (multiple allowed). Otherwise
     * the state behaves like a single-pane stack (replaces existing).
     */
    fun showContent(key: T) {
        if (useWindows) {
            _contents.filter { contentProvider(it)?.ignoreWindows == true }
            _contents.add(key)
        } else {
            _contents.clear()
            _contents.add(key)
        }
    }

    fun hideContent(key: T) = _contents.remove(key)
    fun clear() = _contents.clear()
    fun peek(): T? = _contents.firstOrNull()
    fun hasContent(): Boolean = _contents.isNotEmpty()
    fun isVisible(key: T) = _contents.contains(key)

    /** Returns first content's composable (or null). Useful for supportPane in single/multi-pane UI. */
    fun peekContentComposable(withIgnoreWindow: Boolean = false): (@Composable () -> Unit)? {
        if (!withIgnoreWindow) return _contents.firstOrNull()?.let { contentProvider(it)?.content }

        _contents.forEach { key ->
            val entry = contentProvider(key) ?: return@forEach
            if (entry.ignoreWindows) return entry.content
        }

        return null
    }

    /**
     * Returns list of pairs (key, SupportEntry) derived from current state.
     * Marked @Composable so it can use remember to avoid recreating mapping on every recomposition.
     */
    @Composable
    fun composableEntries(): List<Pair<T, SupportEntry>> =
        remember(_contents.toList()) {
            _contents.mapNotNull { t ->
                contentProvider(t)?.let { entry -> t to entry }
            }
        }

    @Composable
    fun composableContents(): List<@Composable () -> Unit> =
        composableEntries().map { it.second.content }
}

class ContentProviderBuilder<T> {
    private val entries = mutableMapOf<T, SupportEntry>()
    fun entry(key: T, title: String? = null, ignoreWindows: Boolean = false, content: @Composable () -> Unit) {
        entries[key] = SupportEntry(
            title = title,
            ignoreWindows = ignoreWindows,
            content = content
        )
    }
    fun build(): (T) -> SupportEntry? = { entries[it] }
}

fun <T> contentProvider(builder: ContentProviderBuilder<T>.() -> Unit): (T) -> SupportEntry? =
    ContentProviderBuilder<T>().also(builder).build()

@Composable
fun <T> rememberAdaptiveContentState(
    useWindows: Boolean = supportsWindows(),
    contentProvider: (T) -> SupportEntry? = { null }
) = remember(useWindows, contentProvider) { AdaptiveContentState(useWindows, contentProvider) }

@Composable
fun <T> AdaptiveContent(
    singlePaneContent: @Composable () -> Unit,
    twoPaneContent: Pair<@Composable () -> Unit, @Composable () -> Unit>,
    state: AdaptiveContentState<T> = rememberAdaptiveContentState(),
    modifier: Modifier = Modifier
) {
    // entries and platform capability are checked at composition time
    val entries = state.composableEntries()
    val showWindows = state.useWindows && supportsWindows()
    val showSupportPane = !showWindows || entries.any { it.second.ignoreWindows }

    // If windows are not used, show supportPane (first item) inside adaptive width
    AdaptiveWidth(
        modifier = modifier,
        singlePaneContent = singlePaneContent,
        twoPaneContent = twoPaneContent,
        supportPane = if (showSupportPane) state.peekContentComposable(showWindows) else null,
        onHideSupportPane = { state.clear() }
    )

    // When windows are available and entries prefer windows, open them as separate windows
    if (showWindows) {
        entries.forEach { (key, entry) ->
            if (entry.ignoreWindows) return@forEach
            MaybeWindow(
                onDismiss = { state.hideContent(key) },
                dialogElse = false,
                title = entry.title ?: stringResource(Res.string.app_name),
                content = entry.content
            )
        }
    }
}
