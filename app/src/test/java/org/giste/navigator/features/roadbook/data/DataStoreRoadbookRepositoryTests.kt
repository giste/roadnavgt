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

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.giste.navigator.features.roadbook.domain.Roadbook
import org.giste.navigator.features.roadbook.domain.RoadbookRepository
import org.giste.navigator.features.roadbook.domain.Scroll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.io.TempDir
import java.io.File

private const val TEST_DATASTORE: String = "test.preferences_pb"

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DataStoreRoadbookRepositoryTests {
    @TempDir
    private lateinit var temporaryFolder: File
    private val roadbookDatasource: RoadbookDatasource = FakeRoadbookDatasource(50)
    private val testDataStore: DataStore<Preferences> = PreferenceDataStoreFactory.create(
        produceFile = { File(temporaryFolder, TEST_DATASTORE) },
    )
    private val repository: RoadbookRepository = DataStoreRoadbookRepository(
        dataStore = testDataStore,
        roadbookDatasource = roadbookDatasource,
    )

    @AfterEach
    fun afterEach() = runTest {
        // Clear datastore
        testDataStore.edit { it.clear() }
    }

    @Test
    fun `stores scroll in datastore when saved`() = runTest {
        val expectedScroll = Scroll(2, 50)

        repository.saveScroll(expectedScroll)

        val actualScroll = testDataStore.data.map {
            Scroll(
                pageIndex = it[DataStoreRoadbookRepository.ROADBOOK_PAGE_INDEX] ?: 0,
                pageOffset = it[DataStoreRoadbookRepository.ROADBOOK_PAGE_OFFSET] ?: 0
            )
        }.first()
        assertEquals(expectedScroll, actualScroll)
    }

    @Test
    fun `when new uri is loaded scroll is reset`() = runTest {
        val expectedScroll = Scroll(2, 50)
        testDataStore.edit {
            it[DataStoreRoadbookRepository.ROADBOOK_PAGE_INDEX] = expectedScroll.pageIndex
            it[DataStoreRoadbookRepository.ROADBOOK_PAGE_OFFSET] = expectedScroll.pageOffset
        }

        repository.loadRoadbook("new uri")

        val actualScroll = testDataStore.data.map {
            Scroll(
                pageIndex = it[DataStoreRoadbookRepository.ROADBOOK_PAGE_INDEX] ?: 0,
                pageOffset = it[DataStoreRoadbookRepository.ROADBOOK_PAGE_OFFSET] ?: 0
            )
        }.first()
        assertEquals(Scroll(), actualScroll)
    }

    @Test
    fun `when uri is empty roadbook is NotLoaded`() = runTest {
        val actualRoadbook = repository.getRoadbook().first()

        assertTrue(actualRoadbook is Roadbook.NotLoaded)
    }

    @Test
    fun `when uri is not empty roadbook is Loaded`() = runTest {
        testDataStore.edit {
            it[DataStoreRoadbookRepository.ROADBOOK_PAGE_INDEX] = 2
            it[DataStoreRoadbookRepository.ROADBOOK_PAGE_OFFSET] = 50
            it[DataStoreRoadbookRepository.ROADBOOK_URI] = "Non empty URI"
        }

        val actualRoadbook = repository.getRoadbook().first()

        assertTrue(actualRoadbook is Roadbook.Loaded)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `produces a new roadbook for each new URI`() = runTest {
        val roadbooks = mutableListOf<Roadbook>()

        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            repository.getRoadbook().toList(roadbooks)
        }
        repository.loadRoadbook("Uri 1")
        repository.loadRoadbook("uri 2")

        // Initial state is always NotLoaded
        assertEquals(3, roadbooks.count())
        assertTrue(roadbooks[0] is Roadbook.NotLoaded)
        assertTrue(roadbooks[1] is Roadbook.Loaded)
        assertTrue(roadbooks[2] is Roadbook.Loaded)
    }
}