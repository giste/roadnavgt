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

import android.util.Log
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.giste.navigator.IoDispatcher
import org.giste.navigator.features.map.domain.LocalMap
import org.giste.navigator.features.map.domain.MapRegion
import java.nio.file.Path
import java.nio.file.Paths
import javax.inject.Inject
import kotlin.io.path.exists
import kotlin.io.path.getLastModifiedTime
import kotlin.io.path.isDirectory
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.name
import kotlin.io.path.nameWithoutExtension
import kotlin.io.path.pathString

private const val TAG = "LocalMapDatasource"

class LocalMapDatasource @Inject constructor(
    private val baseDir: Path,
    @IoDispatcher private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    companion object {
        const val BASE_PATH = "maps"
    }

    suspend fun getAvailableMaps(region: MapRegion): List<LocalMap> {
        val baseDir = Paths.get(baseDir.pathString, BASE_PATH)
        val regionDir = Paths.get(baseDir.pathString, region.localDir)
        val maps = mutableListOf<LocalMap>()

        withContext(dispatcher) {
            if (regionDir.exists() && regionDir.isDirectory()) {
                regionDir.listDirectoryEntries("*.map")
                    .sortedBy { it.nameWithoutExtension }
                    .map {
                        LocalMap(
                            name = it.nameWithoutExtension,
                            path = it.name,
                            lastModified = it.getLastModifiedTime().toInstant()
                        )
                    }
                    .forEach {
                        Log.d(TAG, "Found map: $it")
                        maps.add(it)
                    }
            }
        }

        return maps
    }
}