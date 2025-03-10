/*
 * Copyright 2025 Giste Trappiste
 *
 * This program is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */

package org.giste.navigator.features.roadbook.ui

import android.graphics.Bitmap
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import coil3.compose.AsyncImage
import org.giste.navigator.R
import org.giste.navigator.features.roadbook.domain.Roadbook
import org.giste.navigator.features.roadbook.domain.RoadbookPage

@Composable
fun Roadbook(
    roadbook: Roadbook,
    onScroll: (Int, Int) -> Unit,
    roadbookScrollState: LazyListState,
    modifier: Modifier = Modifier,
) {
    when (roadbook) {
        is Roadbook.NotLoaded -> {
            Text(
                text = stringResource(R.string.roadbook_load_message),
                modifier = modifier
                    .fillMaxSize()
                    .wrapContentHeight(),
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                style = MaterialTheme.typography.displayMedium,
                textAlign = TextAlign.Center,
            )
        }

        is Roadbook.Loaded -> {
            val pages = roadbook.pages.collectAsLazyPagingItems()

            when (val loadState = pages.loadState.refresh) {
                is LoadState.Error -> {
                    Text(
                        text = loadState.error.message
                            ?: stringResource(R.string.roadbook_error_message),
                        modifier = modifier
                            .fillMaxSize()
                            .wrapContentHeight(),
                        color = MaterialTheme.colorScheme.onError,
                        style = MaterialTheme.typography.displaySmall,
                        textAlign = TextAlign.Center,
                    )
                }

                is LoadState.Loading -> {
                    Text(
                        text = stringResource(R.string.roadbook_loading_message),
                        modifier = modifier
                            .fillMaxSize()
                            .wrapContentHeight(),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.displayMedium,
                        textAlign = TextAlign.Center,
                    )
                }

                is LoadState.NotLoading -> {
                    RoadbookViewer(
                        pages = pages,
                        modifier = modifier,
                        onScrollFinish = onScroll,
                        scrollState = roadbookScrollState,
                    )
                }
            }
        }
    }
}

@Composable
fun RoadbookViewer(
    pages: LazyPagingItems<RoadbookPage>,
    modifier: Modifier = Modifier,
    onScrollFinish: (Int, Int) -> Unit = { _, _ -> },
    scrollState: LazyListState,
) {
    LaunchedEffect(scrollState.isScrollInProgress) {
        if (!scrollState.isScrollInProgress) {
            onScrollFinish(
                scrollState.firstVisibleItemIndex,
                scrollState.firstVisibleItemScrollOffset,
            )
        }
    }

    LazyColumn(
        modifier = modifier,
        state = scrollState,
    ) {
        items(
            count = pages.itemCount,
            key = pages.itemKey(),
            contentType = pages.itemContentType()
        ) { index ->
            val pdfPage = pages[index]
            pdfPage?.let {
                RoadbookPage(it.page)
            }
        }
    }
}

@Composable
private fun RoadbookPage(
    page: Bitmap,
    modifier: Modifier = Modifier,
) {
    AsyncImage(
        model = page,
        contentDescription = null,
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(page.width.toFloat() / page.height.toFloat())
            .drawWithContent { drawContent() },
        contentScale = ContentScale.FillWidth
    )
}
