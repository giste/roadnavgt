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
import org.giste.navigator.features.map.domain.MapRegion
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.io.TempDir
import java.io.File

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LocalMapDatasourceTests {

    @Test
    fun `must find all maps in the directory`(@TempDir tempDir: File) = runTest {
        val mapDatasource = LocalMapDatasource(tempDir)
        val mapsDir = File(tempDir, "/maps${MapRegion.Europe.remotePath}")
        val expectedMaps = listOf("map1", "map2")
        mapsDir.mkdirs()
        expectedMaps.forEach {
            File(mapsDir, "${it}.map").createNewFile()
        }

        val actualMaps = mapDatasource.getAvailableMaps(MapRegion.Europe)

        assertEquals(expectedMaps, actualMaps.map { it.name })
    }
}