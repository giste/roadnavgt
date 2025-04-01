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

package org.giste.navigator.features.map.data

import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.giste.navigator.features.map.data.RemoteMapDatasource.Companion.DATE_TIME_FORMAT
import org.giste.navigator.features.map.domain.NewMapSource
import org.giste.navigator.features.map.domain.Region
import org.giste.navigator.util.DownloadState.Finished
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import kotlin.io.path.createFile
import kotlin.io.path.exists
import kotlin.io.path.notExists
import kotlin.io.path.pathString

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MapsforgeMapRepositoryTests {
    private val remoteMapDatasource = mockk<RemoteMapDatasource>()
    private val localMapDatasource = mockk<LocalMapDatasource>()
    private val formatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)
    private val oldLastModified = LocalDateTime.parse("2025-03-01 00:00", formatter)
        .toInstant(ZoneOffset.ofHours(0))
    private val newLastModified = LocalDateTime.parse("2025-03-20 10:00", formatter)
        .toInstant(ZoneOffset.ofHours(0))
    private val availableMaps = mutableListOf<NewMapSource>()
    private val availableRegionSlot = slot<Region>()
    private val downloadedMaps = mutableListOf<NewMapSource>()
    private val downloadedRegionSlot = slot<Region>()
    private lateinit var mapsDir: Path

    @BeforeEach
    fun beforeEach(@TempDir tempDir: Path) {
        mapsDir = tempDir
        clearAllMocks()
        availableMaps.removeAll { true }
        downloadedMaps.removeAll { true }
        coEvery {
            remoteMapDatasource.getAvailableMaps(capture(availableRegionSlot))
        } answers {
            availableMaps.filter { it.region == availableRegionSlot.captured }
        }
        coEvery {
            localMapDatasource.getDownloadedMaps(mapsDir, capture(downloadedRegionSlot))
        } answers {
            downloadedMaps.filter { it.region == downloadedRegionSlot.captured }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `emits all available maps when there is no downloaded map`() = runTest {
        availableMaps.add(NewMapSource(Region.EUROPE, "spain.map", 0, oldLastModified))
        availableMaps.add(NewMapSource(Region.EUROPE, "portugal.map", 0, oldLastModified))
        availableMaps.add(NewMapSource(Region.EUROPE, "france.map", 0, oldLastModified))
        val mapRepository = MapsforgeMapRepository(
            mapsDir = mapsDir,
            remoteMapDatasource = remoteMapDatasource,
            localMapDatasource = localMapDatasource,
        )
        var actualMaps = emptyList<NewMapSource>()

        val job = launch {
            mapRepository.getMaps().collect { actualMaps = (it) }
        }
        advanceUntilIdle()
        job.cancel()

        assertEquals(availableMaps, actualMaps)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `mark downloaded map as updatable when available is new`() = runTest {
        availableMaps.add(NewMapSource(Region.EUROPE, "spain.map", 0, newLastModified))
        availableMaps.add(NewMapSource(Region.EUROPE, "portugal.map", 0, oldLastModified))
        availableMaps.add(NewMapSource(Region.EUROPE, "france.map", 0, oldLastModified))
        downloadedMaps.add(
            NewMapSource(
                Region.EUROPE,
                "spain.map",
                0,
                oldLastModified,
                downloaded = true
            )
        )
        val expectedMaps = listOf(
            NewMapSource(
                Region.EUROPE,
                "spain.map",
                0,
                newLastModified,
                downloaded = true,
                updatable = true
            ),
            NewMapSource(Region.EUROPE, "portugal.map", 0, oldLastModified),
            NewMapSource(Region.EUROPE, "france.map", 0, oldLastModified),
        )
        val mapRepository = MapsforgeMapRepository(
            mapsDir = mapsDir,
            remoteMapDatasource = remoteMapDatasource,
            localMapDatasource = localMapDatasource,
        )
        var actualMaps = emptyList<NewMapSource>()

        val job = launch {
            mapRepository.getMaps().collect { actualMaps = (it) }
        }
        advanceUntilIdle()
        job.cancel()

        assertEquals(expectedMaps, actualMaps)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `marks map as downloaded when local map exists`() = runTest {
        availableMaps.add(NewMapSource(Region.EUROPE, "spain.map", 0, oldLastModified))
        availableMaps.add(NewMapSource(Region.EUROPE, "portugal.map", 0, oldLastModified))
        availableMaps.add(NewMapSource(Region.EUROPE, "france.map", 0, oldLastModified))
        downloadedMaps.add(
            NewMapSource(
                Region.EUROPE,
                "spain.map",
                0,
                oldLastModified,
                downloaded = true
            )
        )
        val expectedMaps = listOf(
            NewMapSource(Region.EUROPE, "spain.map", 0, oldLastModified, downloaded = true),
            NewMapSource(Region.EUROPE, "portugal.map", 0, oldLastModified),
            NewMapSource(Region.EUROPE, "france.map", 0, oldLastModified),
        )
        val mapRepository = MapsforgeMapRepository(
            mapsDir = mapsDir,
            remoteMapDatasource = remoteMapDatasource,
            localMapDatasource = localMapDatasource,
        )
        var actualMaps = emptyList<NewMapSource>()

        val job = launch {
            mapRepository.getMaps().collect { actualMaps = (it) }
        }
        advanceUntilIdle()
        job.cancel()

        assertEquals(expectedMaps, actualMaps)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `mark map as obsolete when is downloaded and it is not available`() = runTest {
        availableMaps.add(NewMapSource(Region.EUROPE, "portugal.map", 0, oldLastModified))
        availableMaps.add(NewMapSource(Region.EUROPE, "france.map", 0, oldLastModified))
        downloadedMaps.add(
            NewMapSource(
                Region.EUROPE,
                "spain.map",
                0,
                oldLastModified,
                downloaded = true
            )
        )
        val expectedMaps = listOf(
            NewMapSource(
                Region.EUROPE,
                "spain.map",
                0,
                oldLastModified,
                downloaded = true,
                obsolete = true
            ),
            NewMapSource(Region.EUROPE, "portugal.map", 0, oldLastModified),
            NewMapSource(Region.EUROPE, "france.map", 0, oldLastModified),
        )
        val mapRepository = MapsforgeMapRepository(
            mapsDir = mapsDir,
            remoteMapDatasource = remoteMapDatasource,
            localMapDatasource = localMapDatasource,
        )
        var actualMaps = emptyList<NewMapSource>()

        val job = launch {
            mapRepository.getMaps().collect { actualMaps = (it) }
        }
        advanceUntilIdle()
        job.cancel()

        assertEquals(expectedMaps, actualMaps)
    }

    @Test
    fun `moves downloaded map to its destination`() = runTest {
        val tempFile = mapsDir.resolve("temp.map").createFile()
        assertTrue { tempFile.exists() }
        val newMapSource = NewMapSource(Region.EUROPE, "spain.map", 0, oldLastModified)
        val destination = mapsDir.resolve(newMapSource.region.path).resolve(newMapSource.fileName)
        coEvery { remoteMapDatasource.downloadMap(any(), any()) } returns flowOf(Finished)
        val mapRepository = MapsforgeMapRepository(
            mapsDir = mapsDir,
            remoteMapDatasource = remoteMapDatasource,
            localMapDatasource = localMapDatasource,
        )

        val job = launch {
            mapRepository.downloadMap(newMapSource).collect { println(it) }
        }
        job.join()

        assertTrue(tempFile.notExists())
        assertTrue(destination.exists())
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `marks map as available when is removed and available map exists`() = runTest {
        availableMaps.add(NewMapSource(Region.EUROPE, "spain.map", 0, oldLastModified))
        availableMaps.add(NewMapSource(Region.EUROPE, "portugal.map", 0, oldLastModified))
        availableMaps.add(NewMapSource(Region.EUROPE, "france.map", 0, oldLastModified))
        downloadedMaps.add(
            NewMapSource(
                Region.EUROPE,
                "spain.map",
                0,
                oldLastModified,
                downloaded = true
            )
        )
        val expectedMaps = listOf(
            NewMapSource(Region.EUROPE, "portugal.map", 0, oldLastModified),
            NewMapSource(Region.EUROPE, "france.map", 0, oldLastModified),
            NewMapSource(Region.EUROPE, "spain.map", 0, oldLastModified),
        )
        val mapRepository = MapsforgeMapRepository(
            mapsDir = mapsDir,
            remoteMapDatasource = remoteMapDatasource,
            localMapDatasource = localMapDatasource,
        )
        var actualMaps = emptyList<NewMapSource>()

        val job = launch {
            mapRepository.getMaps().collect { actualMaps = (it) }
        }
        advanceUntilIdle()
        mapRepository.removeMap(downloadedMaps.first())
        advanceUntilIdle()
        job.cancel()

        assertEquals(expectedMaps, actualMaps)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `removes map from list when it is removed and is obsolete`() = runTest {
        availableMaps.add(NewMapSource(Region.EUROPE, "portugal.map", 0, oldLastModified))
        availableMaps.add(NewMapSource(Region.EUROPE, "france.map", 0, oldLastModified))
        downloadedMaps.add(
            NewMapSource(
                Region.EUROPE,
                "spain.map",
                0,
                oldLastModified,
                downloaded = true,
                obsolete = true
            )
        )
        val expectedMaps = availableMaps
        val mapRepository = MapsforgeMapRepository(
            mapsDir = mapsDir,
            remoteMapDatasource = remoteMapDatasource,
            localMapDatasource = localMapDatasource,
        )
        var actualMaps = emptyList<NewMapSource>()

        val job = launch {
            mapRepository.getMaps().collect { actualMaps = (it) }
        }
        advanceUntilIdle()
        mapRepository.removeMap(downloadedMaps.first())
        advanceUntilIdle()
        job.cancel()

        assertEquals(expectedMaps, actualMaps)
    }

    @Test
    fun `gets downloaded map sources`() = runTest {
        downloadedMaps.add(NewMapSource(Region.EUROPE, "spain.map", 0, oldLastModified))
        downloadedMaps.add(NewMapSource(Region.EUROPE, "portugal.map", 0, oldLastModified))
        val expectedMapSources = listOf(
            mapsDir.resolve(Region.EUROPE.path).resolve("spain.map").pathString,
            mapsDir.resolve(Region.EUROPE.path).resolve("portugal.map").pathString,
        )
        val mapRepository = MapsforgeMapRepository(
            mapsDir = mapsDir,
            remoteMapDatasource = remoteMapDatasource,
            localMapDatasource = localMapDatasource,
        )

        val actualMapSources = mapRepository.getMapSources()

        assertEquals(expectedMapSources, actualMapSources)
    }
}