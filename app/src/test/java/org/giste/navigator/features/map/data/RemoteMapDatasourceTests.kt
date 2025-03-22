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
import org.giste.navigator.features.map.data.RemoteMapDatasource.Companion.DATE_TIME_FORMAT
import org.giste.navigator.features.map.data.RemoteMapDatasource.Companion.MAP_EXTENSION
import org.giste.navigator.features.map.data.RemoteMapDatasource.DownloadState.Downloading
import org.giste.navigator.features.map.data.RemoteMapDatasource.DownloadState.Failed
import org.giste.navigator.features.map.data.RemoteMapDatasource.DownloadState.Finished
import org.giste.navigator.features.map.domain.MapRegion
import org.giste.navigator.features.map.domain.RemoteMap
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
    private val remoteMapDatasource = RemoteMapDatasource()

    @Test
    fun `must retrieve all maps of a region`() = runTest {
        val expectedMapNames = listOf(
            "greenland",
            "mexico",
            "us-midwest",
            "us-northeast",
            "us-south",
            "us-west"
        )

        val actualMapNames = remoteMapDatasource.getAvailableMaps(MapRegion.NorthAmerica)
            .map { it.name }

        assertEquals(expectedMapNames, actualMapNames)
    }

    @Test
    fun `download existing map`(@TempDir temporaryDir: Path) = runTest {
        val availableMaps = remoteMapDatasource.getAvailableMaps(MapRegion.AustraliaOceania)
        val mapToDownload = availableMaps.first { it.name == "ile-de-clipperton" }
        val tempFile = temporaryDir.resolve("${mapToDownload.name}${MAP_EXTENSION}")

        val downloadStates = mutableListOf<RemoteMapDatasource.DownloadState>()

        val job = launch {
            remoteMapDatasource.downloadMap(mapToDownload, tempFile).toList(downloadStates)
        }
        job.join()

        assertTrue(downloadStates.first() is Downloading)
        assertTrue(downloadStates.last() is Finished)
        assertEquals(mapToDownload.lastModified, tempFile.getLastModifiedTime().toInstant())
    }

    @Test
    fun `must have error when downloading non existent map`(@TempDir temporaryDir: Path) = runTest {
        val tempFile = temporaryDir.resolve("no_existent.map")
        val formatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)
        val nonExistentMap = RemoteMap(
            name = "non_existent",
            url = "${RemoteMapDatasource.BASE_URL}/non_existent.map",
            lastModified = LocalDateTime.parse("1970-01-01 00:00", formatter)
                .toInstant(ZoneOffset.ofHours(0)),
            size = "500K"
        )
        val downloadStates = mutableListOf<RemoteMapDatasource.DownloadState>()

        val job = launch {
            remoteMapDatasource.downloadMap(nonExistentMap, tempFile).toList(downloadStates)
        }
        job.join()

        assertTrue(downloadStates.last() is Failed)
    }
}