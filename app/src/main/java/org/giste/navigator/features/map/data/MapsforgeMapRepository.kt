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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onSubscription
import kotlinx.coroutines.flow.update
import org.giste.navigator.features.map.domain.NewMapSource
import org.giste.navigator.features.map.domain.Region
import org.giste.navigator.utils.DownloadState
import java.nio.file.Path
import java.nio.file.attribute.FileTime
import javax.inject.Inject
import kotlin.io.path.createDirectories
import kotlin.io.path.deleteIfExists
import kotlin.io.path.inputStream
import kotlin.io.path.moveTo
import kotlin.io.path.outputStream
import kotlin.io.path.pathString
import kotlin.io.path.setLastModifiedTime

private const val TAG = "MapsforgeMapRepository"

class MapsforgeMapRepository @Inject constructor(
    private val mapsDir: Path,
    private val remoteMapDatasource: RemoteMapDatasource,
    private val localMapDatasource: LocalMapDatasource,
) {
    private val _maps = MutableStateFlow<List<NewMapSource>>(emptyList())
    private val maps = _maps.asStateFlow()
    private val fullAvailableMaps = mutableMapOf<String, NewMapSource>()

    fun getMaps(): Flow<List<NewMapSource>> {
        return maps.onSubscription {
            getInitialMaps()
        }
    }

    private suspend fun getInitialMaps() {
        val newMapSources = mutableListOf<NewMapSource>()

        Region.entries.forEach {
            val availableMaps: MutableMap<String, NewMapSource> = remoteMapDatasource
                .getAvailableMaps(it)
                .associate { it.id to it }
                .toMutableMap()
            fullAvailableMaps.putAll(availableMaps)
            val downloadedMaps: MutableMap<String, NewMapSource> = localMapDatasource
                .getDownloadedMaps(mapsDir, it)
                .associate { it.id to it }
                .toMutableMap()

            downloadedMaps.values.forEach { downloaded ->
                availableMaps[downloaded.id]?.let { available ->
                    // Available map for downloaded one
                    Log.d(TAG, "Marking map as downloaded $available")
                    newMapSources.add(
                        available.copy(
                            downloaded = true,
                            updatable = available.lastModified > downloaded.lastModified
                        )
                    )
                    availableMaps.remove(available.id)
                } ?: run {
                    Log.d(TAG, "Marking map as obsolete $downloaded")
                    // No available map for downloaded one
                    newMapSources.add(downloaded.copy(obsolete = true))
                }
            }
            // Add remaining available maps
            newMapSources.addAll(availableMaps.values)
        }

        _maps.update {
            newMapSources.toList()
        }
    }

    fun downloadMap(newMapSource: NewMapSource): Flow<DownloadState> {
        return flow {
            try {
                val tempFile = mapsDir.resolve("temp.map")
                val regionDir = mapsDir.resolve(newMapSource.region.path)
                val destination = regionDir.resolve(newMapSource.fileName)

                regionDir.createDirectories()
                remoteMapDatasource.downloadMap(newMapSource, tempFile).collect {
                    when (it) {
                        is DownloadState.Downloading -> emit(it)
                        is DownloadState.Failed -> {
                            tempFile.deleteIfExists()
                            emit(it)
                        }
                        is DownloadState.Finished -> {
                            tempFile.inputStream().use {
                                destination.outputStream().use {
                                    Log.d(TAG, "Moving temp file to ${destination.pathString}")
                                    tempFile.moveTo(destination, overwrite = true)
                                    destination.setLastModifiedTime(
                                        FileTime.from(newMapSource.lastModified)
                                    )
                                }
                            }
                            _maps.update {
                                val newMaps = _maps.value.toMutableList()
                                newMaps.remove(newMapSource)
                                newMaps.add(
                                    newMapSource.copy(
                                        downloaded = true,
                                        updatable = false,
                                        obsolete = false,
                                    )
                                )
                                newMaps.toList()
                            }
                            emit(it)
                        }
                    }
                }
            } catch (e: Exception) {
                emit(DownloadState.Failed(e))
            }
        }
    }

    fun removeMap(mapSource: NewMapSource) {
        val mapFile = mapsDir.resolve(mapSource.region.path)
            .resolve(mapSource.fileName)

        try {
            mapFile.deleteIfExists()

            val maps = _maps.value.toMutableList()
            maps.remove(mapSource)
            fullAvailableMaps[mapSource.id]?.let { maps.add(it.copy()) }
            _maps.update {
                maps.toList()
            }
        } catch(e: Exception) {
            // Log exception and continue
            Log.e(TAG, e.toString())
        }
    }
}