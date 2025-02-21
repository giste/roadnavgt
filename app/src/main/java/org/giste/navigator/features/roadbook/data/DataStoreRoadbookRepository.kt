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

package org.giste.navigator.features.roadbook.data

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.paging.Pager
import androidx.paging.PagingConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.giste.navigator.features.roadbook.domain.Roadbook
import org.giste.navigator.features.roadbook.domain.RoadbookRepository
import org.giste.navigator.features.roadbook.domain.Scroll
import javax.inject.Inject

private const val TAG = "DataStoreRoadbookRepository"

class DataStoreRoadbookRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val roadbookDatasource: RoadbookDatasource,
) : RoadbookRepository {
    companion object {
        val ROADBOOK_URI = stringPreferencesKey("ROADBOOK_URI")
        val ROADBOOK_PAGE_INDEX = intPreferencesKey("ROADBOOK_PAGE_INDEX")
        val ROADBOOK_PAGE_OFFSET = intPreferencesKey("ROADBOOK_PAGE_OFFSET")
    }

    override fun getRoadbook(): Flow<Roadbook> {
        return this.getUri()
            .distinctUntilChanged()
            .map { uri ->
                if (uri.isEmpty()) {
                    Roadbook.NotLoaded
                } else {
                    Roadbook.Loaded(
                        pages = Pager(
                            config = PagingConfig(pageSize = 5),
                            initialKey = 0,
                        ) {
                            Log.d(TAG, "Reading new pages")
                            RoadbookPagingSource(
                                roadbookDatasource
                            )
                        }.flow,
                        initialScroll = getScroll(),
                    )
                }
            }
    }

    override suspend fun loadRoadbook(uri: String) {
        // New roadbook, reset scroll
        saveScroll(Scroll())
        // Load roadbook
        roadbookDatasource.loadRoadbook(uri)
        // Save uri
        saveRoadbookUri(uri)

        Log.i(TAG, "Roadbook loaded for URI: $uri")
    }

    override suspend fun saveScroll(scroll: Scroll) {
        val preferences = dataStore.edit {
            it[ROADBOOK_PAGE_INDEX] = scroll.pageIndex
            it[ROADBOOK_PAGE_OFFSET] = scroll.pageOffset
        }

        Log.d(
            TAG,
            "Scroll saved: ${preferences[ROADBOOK_PAGE_INDEX]}, ${preferences[ROADBOOK_PAGE_OFFSET]}"
        )
    }

    private fun getUri(): Flow<String> {
        return dataStore.data.map { it[ROADBOOK_URI] ?: "" }
    }

    private suspend fun getScroll(): Scroll {
        return dataStore.data.map {
            Log.v(
                TAG,
                "getScroll = (${it[ROADBOOK_PAGE_INDEX]}, ${it[ROADBOOK_PAGE_OFFSET]})"
            )
            Scroll(
                pageIndex = it[ROADBOOK_PAGE_INDEX] ?: 0,
                pageOffset = it[ROADBOOK_PAGE_OFFSET] ?: 0
            )
        }.first()
    }

    private suspend fun saveRoadbookUri(uri: String) {
        val preferences = dataStore.edit { it[ROADBOOK_URI] = uri }

        Log.d(
            TAG,
            "Saved URI: ${preferences[ROADBOOK_URI]}"
        )
    }
}
