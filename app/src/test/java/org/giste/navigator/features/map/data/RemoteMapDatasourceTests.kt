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

import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.giste.navigator.features.map.data.NewRemoteMapDatasource.Companion.DATE_TIME_FORMAT
import org.giste.navigator.features.map.data.NewRemoteMapDatasource.DownloadState
import org.giste.navigator.features.map.data.NewRemoteMapDatasource.DownloadState.Downloading
import org.giste.navigator.features.map.data.NewRemoteMapDatasource.DownloadState.Finished
import org.giste.navigator.features.map.domain.Map
import org.giste.navigator.features.map.domain.NewMapSource
import org.giste.navigator.features.map.domain.Region
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import kotlin.io.path.getLastModifiedTime

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RemoteMapDatasourceTests {
    private val remoteMapDatasource = NewRemoteMapDatasource()

    @Test
    fun `must retrieve all maps of a region`() = runTest {
        val expectedMaps = listOf(
            Map.GREENLAND,
            Map.MEXICO,
            Map.US_MIDWEST,
            Map.US_NORTHEAST,
            Map.US_SOUTH,
            Map.US_WEST,
        )

        val actualMaps = remoteMapDatasource.getAvailableMaps(Region.NORTH_AMERICA)
            .getOrThrow()
            .map { it.map }

        assertEquals(expectedMaps, actualMaps)
    }

    @Test
    fun `download existing map`(@TempDir temporaryDir: Path) = runTest {
        val formatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)
        val mapToDownload = NewMapSource(
            map = Map.ILE_DE_CLIPPERTON,
            size = "432K",
            lastModified = LocalDateTime.parse("1970-01-01 00:00", formatter)
                .toInstant(ZoneOffset.ofHours(0)))
        val tempFile = temporaryDir.resolve(mapToDownload.map.path)

        val downloadStates = mutableListOf<DownloadState>()

        val job = launch {
            remoteMapDatasource.downloadMap(mapToDownload, tempFile).toList(downloadStates)
        }
        job.join()

        assertTrue(downloadStates.first() is Downloading)
        assertTrue(downloadStates.last() is Finished)
        assertEquals(mapToDownload.lastModified, tempFile.getLastModifiedTime().toInstant())
    }
}