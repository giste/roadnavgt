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

package org.giste.navigator.features.roadbook.data

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import org.giste.navigator.features.roadbook.domain.RoadbookPage
import java.io.FileNotFoundException
import java.io.IOException

private const val TAG = "RoadbookPagingSource"
private const val PDF_STARTING_PAGE_INDEX = 0

class RoadbookPagingSource(
    private val roadbookDatasource: RoadbookDatasource,
) : PagingSource<Int, RoadbookPage>() {
    override fun getRefreshKey(state: PagingState<Int, RoadbookPage>): Int {
        Log.d(TAG, "Refreshing key")

        // Roadbook only invalidates when a new one is loaded, return always first page
        return PDF_STARTING_PAGE_INDEX
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, RoadbookPage> {
        val position = params.key ?: PDF_STARTING_PAGE_INDEX

        try {
            val bitmaps = roadbookDatasource.loadPages(position, params.loadSize)
            val pageCount = roadbookDatasource.getPageCount()
            val prevKey = if (position == PDF_STARTING_PAGE_INDEX) {
                null
            } else {
                safeStart(position - params.loadSize)
            }
            val nextKey = if ((position + params.loadSize) >= pageCount) {
                null
            } else {
                safeEnd(position + params.loadSize)
            }

            Log.d(
                TAG,
                "position: $position; nextKey: $nextKey; prevKey: $prevKey; count: $pageCount"
            )

            return LoadResult.Page(bitmaps, prevKey, nextKey)

        } catch (e: IOException) {
            return LoadResult.Error(e)
        } catch (e: SecurityException) {
            return LoadResult.Error(e)
        } catch (e: FileNotFoundException) {
            return LoadResult.Error(e)
        }
    }

    /**
     * Indicates that jumping is supported by the `PagingSource`.
     */
    override val jumpingSupported: Boolean = true

    private fun safeStart(startPosition: Int): Int =
        startPosition.coerceAtLeast(PDF_STARTING_PAGE_INDEX)

    private fun safeEnd(endPosition: Int): Int = endPosition.coerceAtMost(roadbookDatasource.getPageCount())
}