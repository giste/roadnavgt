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
import org.giste.navigator.features.map.domain.Map
import org.giste.navigator.features.map.domain.Region
import java.nio.file.Path
import javax.inject.Inject
import kotlin.io.path.exists
import kotlin.io.path.fileSize
import kotlin.io.path.getLastModifiedTime
import kotlin.io.path.isDirectory
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.name

private const val TAG = "LocalMapDatasource"

class LocalMapDatasource @Inject constructor(
    private val baseDir: Path,
    @IoDispatcher private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    companion object {
        const val BASE_PATH = "maps/"
    }

    suspend fun getAvailableMaps(region: Region): Result<List<Map>> {
        val maps = mutableListOf<Map>()

        try {
            val regionDir = baseDir.resolve(BASE_PATH).resolve(region.path)

            withContext(dispatcher) {
                if (regionDir.exists() && regionDir.isDirectory()) {
                    regionDir.listDirectoryEntries("*.map")
                        .map { foundMap ->
                            Map(
                                region = region,
                                fileName = foundMap.name,
                                size = foundMap.fileSize(),
                                lastModified = foundMap.getLastModifiedTime().toInstant(),
                                downloaded = true,
                            )
                        }
                        .forEach {
                            Log.d(TAG, "Found map: $it")
                            maps.add(it)
                        }
                }
            }
        } catch (e: Exception) {
            return Result.failure(e)
        }

        return Result.success(maps)
    }
}