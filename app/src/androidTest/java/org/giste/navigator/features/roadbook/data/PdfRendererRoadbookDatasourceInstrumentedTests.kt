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

import android.content.Context
import android.net.Uri
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.test.runTest
import org.giste.navigator.test.R
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.io.File

private const val ROADBOOK_FILE = "roadbook.pdf"

@DisplayName("Instrumented tests for PdfRendererRoadbookDatasource")
class PdfRendererRoadbookDatasourceInstrumentedTests {
    private val testContext: Context = ApplicationProvider.getApplicationContext()
    private val filesDir = testContext.filesDir
    private val onePagePdfUri: String = uriFromResource(R.raw.one_page_pdf)
    private val tenPagesPdfUri: String = uriFromResource(R.raw.ten_pages_pdf)
    private lateinit var roadbookDatasource: RoadbookDatasource

    @BeforeEach
    fun beforeEach() {
        File(filesDir, ROADBOOK_FILE).delete()
        roadbookDatasource = PdfRendererRoadbookDatasource(testContext)
    }

    @Test
    fun internal_file_is_created_when_roadbook_is_loaded() = runTest {
        val internalFile = File(filesDir, ROADBOOK_FILE)
        assertFalse(internalFile.exists())

        roadbookDatasource.loadRoadbook(onePagePdfUri)

        assertTrue(internalFile.exists())
        assertTrue(internalFile.isFile)
    }

    @DisplayName("given a roadbook is loaded")
    @Nested
    inner class RoadbookLoaded {
        @BeforeEach
        fun beforeEach() = runTest {
            roadbookDatasource.loadRoadbook(tenPagesPdfUri)
        }

        @Test
        fun page_count_is_correct() = runTest {
            val actualPageCount = roadbookDatasource.getPageCount()

            assertEquals(10, actualPageCount)
        }

        @Test
        fun return_pages_of_requested_range() = runTest {
            val expectedPageIndexes = listOf(2, 3, 4, 5, 6)

            val actualPageIndexes = roadbookDatasource.loadPages(2, 5).map {
                it.index
            }

            assertEquals(expectedPageIndexes, actualPageIndexes)
        }

        @Test
        fun does_not_return_pages_before_first_page() = runTest {
            val expectedPageIndexes = listOf(0, 1, 2, 3)

            val actualPageIndexes = roadbookDatasource.loadPages(-2, 6).map {
                it.index
            }

            assertEquals(expectedPageIndexes, actualPageIndexes)
        }

        @Test
        fun does_not_return_pages_after_last_page() = runTest {
            val expectedPageIndexes = listOf(7, 8, 9)

            val actualPageIndexes = roadbookDatasource.loadPages(7, 6).map {
                it.index
            }

            assertEquals(expectedPageIndexes, actualPageIndexes)
        }
    }

    @DisplayName("given a roadbook is not loaded")
    @Nested
    inner class RoadbookNotLoaded {
        @Test
        fun page_list_is_empty() = runTest {
            val actualPageList = roadbookDatasource.loadPages(0, 2)

            assertTrue(actualPageList.isEmpty())
        }

        @Test
        fun has_zero_pages() = runTest {
            val actualPageCount = roadbookDatasource.getPageCount()

            assertEquals(0, actualPageCount)
        }
    }

    private fun uriFromResource(resourceId: Int): String {
        return Uri.parse("android.resource://org.giste.navigator.test/${resourceId}").toString()
    }
}