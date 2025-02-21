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

import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.testing.TestPager
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.test.runTest
import org.giste.navigator.features.MainDispatcherExtension
import org.giste.navigator.features.roadbook.domain.RoadbookPage
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith

@DisplayName("Unit tests for RoadbookPagingSource")
@ExtendWith(MockKExtension::class)
@ExtendWith(MainDispatcherExtension::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RoadbookPagingSourceTests {

    @DisplayName("Given a roadbook with multiple pages")
    @Nested
    inner class MultiplePageRoadbook {
        private val roadbookDatasource: RoadbookDatasource = FakeRoadbookDatasource(20)
        private val pagingSource: RoadbookPagingSource = RoadbookPagingSource(roadbookDatasource)
        private val pager: TestPager<Int, RoadbookPage> = TestPager(
            config = PagingConfig(
                pageSize = 3,
                maxSize = 10,
            ),
            pagingSource = pagingSource,
        )

        @Test
        fun `refresh returns first pages`() = runTest {
            val expectedPageIndexes = listOf(0, 1, 2, 3, 4, 5, 6, 7, 8) // 3 blocks of 3 pages

            val actualPages = pager.refresh() as PagingSource.LoadResult.Page

            assertEquals(expectedPageIndexes, actualPages.data.map { it.index })
        }

        @Test
        fun `when last page is reached returns no more than last page`() = runTest {
            val expectedPageIndexes = listOf(18, 19)

            val actualPages = with(pager) {
                refresh() // 0..8 pages
                append() // 9..11
                append() // 12..14
                append() // 15..17
                append() // 18..19
            } as PagingSource.LoadResult.Page

            assertEquals(expectedPageIndexes, actualPages.data.map { it.index })
        }

        @Test
        fun `when first page is reached returns no less than first page`() = runTest {
            val expectedPageIndexes = listOf(0, 1, 2)

            val actualPages = with(pager) {
                refresh() // Pages 0..8
                append() // 9..11
                append() // 12..14
                append() // 15..17
                append() // 18..19
                prepend() // 7..9
                prepend() // 4..5
                prepend() // 1..3
                prepend() // 0..2
            } as PagingSource.LoadResult.Page

            assertEquals(expectedPageIndexes, actualPages.data.map { it.index })
        }
    }

    @DisplayName("Given a single page roadbook")
    @Nested
    inner class SinglePageRoadbook {
        private val roadbookDatasource: RoadbookDatasource = FakeRoadbookDatasource(1)
        private val pagingSource: RoadbookPagingSource = RoadbookPagingSource(roadbookDatasource)
        private val pager: TestPager<Int, RoadbookPage> = TestPager(
            config = PagingConfig(
                pageSize = 3,
                maxSize = 10,
            ),
            pagingSource = pagingSource,
        )

        @Test
        fun `refresh returns only one page`() = runTest {
            val expectedPageIndexes = listOf(0)

            val actualPages = pager.refresh() as PagingSource.LoadResult.Page

            assertEquals(expectedPageIndexes, actualPages.data.map { it.index })
        }
    }
}
