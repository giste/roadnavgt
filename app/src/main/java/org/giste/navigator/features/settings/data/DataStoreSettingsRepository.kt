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

package org.giste.navigator.features.settings.data

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import org.giste.navigator.features.settings.domain.Settings
import org.giste.navigator.features.settings.domain.SettingsRepository
import javax.inject.Inject

private const val TAG = "DataStoreSettingsRepository"
private val LOCATION_MIN_TIME = longPreferencesKey("SETTINGS_LOCATION_MIN_TIME")
private val LOCATION_MIN_DISTANCE = intPreferencesKey("SETTINGS_LOCATION_MIN_DISTANCE")

class DataStoreSettingsRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) : SettingsRepository {
    override fun getSettings(): Flow<Settings> {
        return dataStore.data.map {
            Log.d(TAG, "Reading settings")

            Settings(
                locationMinTime = it[LOCATION_MIN_TIME] ?: 1_000L,
                locationMinDistance = it[LOCATION_MIN_DISTANCE] ?: 10,
            )
        }.distinctUntilChanged()
    }

    override suspend fun saveSettings(settings: Settings) {
        Log.d(TAG, "Saving $settings")

        dataStore.edit {
            it[LOCATION_MIN_TIME] = settings.locationMinTime
            it[LOCATION_MIN_DISTANCE] = settings.locationMinDistance
        }
    }
}