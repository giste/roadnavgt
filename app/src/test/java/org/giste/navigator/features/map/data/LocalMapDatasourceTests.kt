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

import kotlinx.coroutines.test.runTest
import org.giste.navigator.features.map.data.RemoteMapDatasource.Companion.DATE_TIME_FORMAT
import org.giste.navigator.features.map.domain.MapSource
import org.giste.navigator.features.map.domain.Region
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path
import java.nio.file.attribute.FileTime
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import kotlin.io.path.createDirectories
import kotlin.io.path.createFile
import kotlin.io.path.setLastModifiedTime

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LocalMapDatasourceTests {
    @Test
    fun `must find all maps in the directory`(@TempDir tempDir: Path) = runTest {
        val mapDatasource = LocalMapDatasource()
        val regionDir = tempDir
            .resolve(Region.EUROPE.path)
            .createDirectories()
        val formatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)
        val lastModified = LocalDateTime.parse("1970-01-01 00:00", formatter)
            .toInstant(ZoneOffset.ofHours(0))
        val expectedMapSources = listOf(
            MapSource(Region.EUROPE, "spain.map", 0L, lastModified, true),
            MapSource(Region.EUROPE, "portugal.map", 0L, lastModified, true),
        )
        expectedMapSources.forEach {
            regionDir.resolve(it.fileName)
                .createFile()
                .setLastModifiedTime(FileTime.from(lastModified))
        }

        val actualMaps = mapDatasource.getDownloadedMaps(tempDir, Region.EUROPE)

        assertEquals(expectedMapSources, actualMaps)
    }
}