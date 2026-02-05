package com.davanok.dvnkdnd.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.error
import dvnkdnd.composeapp.generated.resources.loading
import dvnkdnd.composeapp.generated.resources.refresh
import org.jetbrains.compose.resources.stringResource

@Composable
fun PagedLazyColumn(
    entities: LazyPagingItems<*>,
    listState: LazyListState = rememberLazyListState(),
    modifier: Modifier = Modifier,
    content: LazyListScope.() -> Unit
) {
    val prependState = entities.loadState.prepend
    val refreshState = entities.loadState.refresh
    val appendState = entities.loadState.append
    LazyColumn(
        state = listState,
        modifier = modifier
    ) {
        if (prependState is LoadState.Loading)
            item { LoadingItem() }

        if (prependState is LoadState.Error)
            item {
                ErrorItem(
                    error = prependState.error,
                    onRetry = { entities.retry() }
                )
            }

        if (refreshState is LoadState.Error)
            item {
                ErrorItem(
                    error = refreshState.error,
                    onRetry = { entities.retry() }
                )
            }

        content()

        if (appendState is LoadState.Loading)
            item { LoadingItem() }

        if (appendState is LoadState.Error)
            item {
                ErrorItem(
                    error = appendState.error,
                    onRetry = { entities.retry() }
                )
            }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun LoadingItem() {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        LoadingIndicator()
        Spacer(Modifier.width(12.dp))
        Text(text = stringResource(Res.string.loading))
    }
}
@Composable
private fun ErrorItem(
    error: Throwable,
    onRetry: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 16.dp),
    ) {
        Text(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            text = error.message ?: stringResource(Res.string.error),
            textAlign = TextAlign.Center
        )

        TextButton(
            modifier = Modifier.align(Alignment.End),
            onClick = onRetry
        ) {
            Text(text = stringResource(Res.string.refresh))
        }
    }
}