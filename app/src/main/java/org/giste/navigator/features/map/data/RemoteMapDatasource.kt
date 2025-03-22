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
import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.network.parseGetRequest
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import org.giste.navigator.IoDispatcher
import org.giste.navigator.features.map.domain.MapRegion
import org.giste.navigator.features.map.domain.RemoteMap
import java.io.BufferedInputStream
import java.net.URL
import java.nio.file.Path
import java.nio.file.attribute.FileTime
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import kotlin.io.path.deleteIfExists
import kotlin.io.path.outputStream
import kotlin.io.path.setLastModifiedTime

private const val TAG = "RemoteMapDatasource"

class RemoteMapDatasource @Inject constructor(
    @IoDispatcher private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    companion object {
        const val BASE_URL =
            "https://ftp-stud.hs-esslingen.de/pub/Mirrors/download.mapsforge.org/maps/v5"
        const val DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm"
        const val MAP_EXTENSION = ".map"
    }

    suspend fun getAvailableMaps(region: MapRegion): List<RemoteMap> {
        val remoteUrl = "$BASE_URL${region.remotePath}"
        Log.d(TAG, "Get maps for $remoteUrl")
        val formatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)
        val maps: MutableList<RemoteMap> = mutableListOf()

        withContext(dispatcher) {
            Ksoup.parseGetRequest(remoteUrl)
                .getElementsByTag("a")
                .filter { it.ownText().contains(MAP_EXTENSION) }
                .forEach {
                    Log.d(TAG, "<a>: ${it.ownText()}")
                    it.parent()?.parent()?.let { parent ->
                        Log.d(TAG, "Parent: ${parent.text()}")

                        val name = it.text().removeSuffix(MAP_EXTENSION)
                        val url = "$remoteUrl/${it.attr("href")}"
                        val lastModified = LocalDateTime.parse(parent.child(2).text(), formatter)
                            .toInstant(ZoneOffset.ofHours(0))
                        val size = parent.child(3).text()

                        val remoteMap = RemoteMap(name, url, lastModified, size)
                        maps.add(remoteMap)

                        Log.d(TAG, "Found remote map $remoteMap")
                    }
                }
        }

        return maps
    }

    fun downloadMap(remoteMap: RemoteMap, destination: Path): Flow<DownloadState> {
        var bytesProcessed = 0

        return flow {
            emit(DownloadState.Downloading(0))

            try {
                val url = URL(remoteMap.url)
                val totalBytes = convertSize(remoteMap.size).toDouble()

                Log.d(TAG, "Bytes to download: $totalBytes")

                emit(DownloadState.Downloading(0))
                destination.deleteIfExists()
                url.openStream().use {
                    BufferedInputStream(it).use { input ->
                        destination.outputStream().use { output ->
                            val data = ByteArray(DEFAULT_BUFFER_SIZE)
                            var byteCount: Int
                            while (
                                input.read(data, 0, DEFAULT_BUFFER_SIZE)
                                    .also { byteCount = it } != -1
                            ) {
                                output.write(data, 0, byteCount)
                                bytesProcessed += byteCount
                                emit(
                                    DownloadState.Downloading(
                                        (bytesProcessed / totalBytes * 100).toInt()
                                    )
                                )
                            }
                        }
                    }
                }
                destination.setLastModifiedTime(FileTime.from(remoteMap.lastModified))
                emit(DownloadState.Finished)
            } catch (e: Exception) {
                Log.e(TAG, e.toString())
                emit(DownloadState.Failed(e))
            }
        }
            .flowOn(dispatcher)
            .distinctUntilChanged()
    }

    private fun convertSize(mapSize: String): Long {
        var multiplier = 1
        var sizeString = ""
        val suffix = mapSize.takeLast(1)

        when (suffix) {
            "K" -> multiplier = 1_024
            "M" -> multiplier = 1_048_576
            "G" -> multiplier = 1_073_741_824
        }

        sizeString = if (multiplier == 1) mapSize else mapSize.dropLast(1)

        return sizeString.toDouble().times(multiplier).toLong()
    }

    sealed class DownloadState {
        data class Downloading(val progress: Int) : DownloadState()
        object Finished : DownloadState()
        data class Failed(val exception: Exception) : DownloadState()
    }
}