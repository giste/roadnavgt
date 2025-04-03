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

package org.giste.navigator.features.map.domain

import kotlinx.coroutines.flow.Flow
import org.giste.navigator.util.DownloadState

interface NewMapRepository {
    fun getMaps(): Flow<List<NewMapSource>>
    suspend fun getMapSources(): Flow<List<String>>
    fun downloadMap(newMapSource: NewMapSource): Flow<DownloadState>
    suspend fun removeMap(mapSource: NewMapSource)
}