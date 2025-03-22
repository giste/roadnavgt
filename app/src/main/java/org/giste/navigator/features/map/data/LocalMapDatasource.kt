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
import java.io.File
import java.time.Instant
import javax.inject.Inject

private const val TAG = "LocalMapDatasource"

class LocalMapDatasource @Inject constructor(
    private val baseDir: File,
    @IoDispatcher private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    companion object {
        const val BASE_PATH = "/maps"
        const val MAP_EXTENSION = ".map"
    }

    suspend fun getAvailableMaps(region: MapRegion): List<LocalMap> {
        val regionDir = File(baseDir, "$BASE_PATH${region.localPath}")
        val maps = mutableListOf<LocalMap>()

        withContext(dispatcher) {
            if (regionDir.exists() && regionDir.isDirectory) {
                regionDir.walk()
                    .filter { it.name.endsWith(MAP_EXTENSION) }
                    .sortedBy { it.name }
                    .map {
                        LocalMap(
                            name = it.name.removeSuffix(MAP_EXTENSION),
                            path = "/${it.name}",
                            lastModified = Instant.ofEpochMilli(it.lastModified())
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