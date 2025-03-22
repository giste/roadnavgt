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
import org.giste.navigator.features.map.data.LocalMapDatasource.Companion.BASE_PATH
import org.giste.navigator.features.map.domain.MapRegion
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.createDirectories
import kotlin.io.path.createFile
import kotlin.io.path.pathString

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LocalMapDatasourceTests {

    @Test
    fun `must find all maps in the directory`(@TempDir tempDir: Path) = runTest {
        val mapDatasource = LocalMapDatasource(tempDir)
        val mapsDir = Paths.get(tempDir.pathString, BASE_PATH)
        val regionDir = Paths.get(mapsDir.pathString, MapRegion.Europe.localDir)
        val expectedMaps = listOf("map1", "map2")
        regionDir.createDirectories()
        expectedMaps.forEach {
            Paths.get(regionDir.pathString, "${it}.map").createFile()
        }

        val actualMaps = mapDatasource.getAvailableMaps(MapRegion.Europe)

        assertEquals(expectedMaps, actualMaps.map { it.name })
    }
}