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
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onSubscription
import kotlinx.coroutines.launch
import org.giste.navigator.features.map.domain.MapRepository
import org.giste.navigator.features.map.domain.MapSource
import org.giste.navigator.features.map.domain.Region
import org.giste.navigator.util.DownloadState
import org.giste.navigator.util.SuspendLazy
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
) : MapRepository {
    private val availableMaps = SuspendLazy { getAvailableMaps() }
    private val downloadedMaps = SuspendLazy { getDownloadedMaps() }
    private val _maps = MutableSharedFlow<List<MapSource>>()
    private val maps = _maps.onSubscription { emit(getMapList()) }
    private val _sources = MutableSharedFlow<List<String>>()
    private val sources = _sources.onSubscription { emit(getSourceList()) }

    override fun getMaps(): Flow<List<MapSource>> {
        return maps
    }

    override fun getMapSources(): Flow<List<String>> {
        return sources
    }

    override fun downloadMap(mapSource: MapSource): Flow<DownloadState> {
        try {
            val tempFile = mapsDir.resolve("temp.map")
            val regionDir = mapsDir.resolve(mapSource.region.path)
            val destination = regionDir.resolve(mapSource.fileName)

            regionDir.createDirectories()

            return remoteMapDatasource.downloadMap(mapSource, tempFile)
                .map { downloadState ->
                    when (downloadState) {
                        is DownloadState.Downloading -> downloadState

                        is DownloadState.Failed -> {
                            tempFile.deleteIfExists()
                            downloadState
                        }

                        is DownloadState.Finished -> {
                            // Move temporal file to destination
                            tempFile.inputStream().use {
                                destination.outputStream().use {
                                    tempFile.moveTo(destination, overwrite = true)
                                    destination.setLastModifiedTime(
                                        FileTime.from(mapSource.lastModified)
                                    )
                                    Log.d(TAG, "Moving temp file to ${destination.pathString}")
                                }
                            }
                            availableMaps()[mapSource.id]?.let {
                                downloadedMaps()[mapSource.id] = it.copy(downloaded = true)
                            } ?: run {
                                throw Exception("Downloaded map should be available")
                            }
                            updateFlows()

                            downloadState
                        }
                    }
                }
        } catch (e: Exception) {
            Log.e(TAG, e.stackTraceToString())
            return flowOf(DownloadState.Failed(e))
        }
    }

    override suspend fun removeMap(mapSource: MapSource) {
        try {
            val mapFile = mapsDir.resolve(mapSource.region.path)
                .resolve(mapSource.fileName)

            mapFile.deleteIfExists()
            downloadedMaps().remove(mapSource.id)
            updateFlows()
        } catch (e: Exception) {
            // Log exception and continue
            Log.e(TAG, e.toString())
        }
    }

    private suspend fun updateFlows() {
        _maps.emit(getMapList())
        _sources.emit(getSourceList())
    }

    private suspend fun getMapList(): List<MapSource> {
        val mapSources = mutableListOf<MapSource>()

        downloadedMaps().values.forEach { downloaded ->
            availableMaps()[downloaded.id]?.let { available ->
                // Available map for downloaded one
                Log.d(TAG, "Marking map as downloaded $available")
                mapSources.add(
                    available.copy
                        (
                        downloaded = true,
                        updatable = available.lastModified > downloaded.lastModified
                    )
                )
            } ?: run {
                Log.d(TAG, "Marking map as obsolete $downloaded")
                // No available map for downloaded one
                mapSources.add(downloaded.copy(obsolete = true))
            }
        }
        // Add remaining available maps
        mapSources.addAll(
            availableMaps().filter { !downloadedMaps().keys.contains(it.key) }
                .map { it.value }
        )

        return mapSources
    }

    private suspend fun getDownloadedMaps(): MutableMap<String, MapSource> {
        val downloaded = mutableMapOf<String, MapSource>()
        coroutineScope {
            Region.entries.forEach {
                launch {
                    downloaded.putAll(
                        localMapDatasource.getDownloadedMaps(mapsDir, it)
                            .associate { it.id to it }
                    )
                }
            }
        }

        Log.d(TAG, "Loaded initial downloaded maps: ${downloaded.size}")
        Log.v(TAG, "Initial downloaded: $downloaded")

        return downloaded
    }

    private suspend fun getAvailableMaps(): Map<String, MapSource> {
        val availableMaps = mutableMapOf<String, MapSource>()

        //coroutineScope {
        Region.entries.forEach {
            //launch {
            availableMaps.putAll(
                remoteMapDatasource.getAvailableMaps(it)
                    .associate { it.id to it }
            )
            //}
        }
        //}

        Log.d(TAG, "Loaded initial available maps: ${availableMaps.size}")
        Log.v(TAG, "Initial available: $availableMaps")

        return availableMaps.toMap()
    }

    private suspend fun getSourceList(): List<String> {
        return downloadedMaps().values.map {
            mapsDir.resolve(it.region.path).resolve(it.fileName).pathString
        }
    }
}