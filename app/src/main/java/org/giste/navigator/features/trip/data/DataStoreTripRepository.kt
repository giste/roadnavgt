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

package org.giste.navigator.features.trip.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import org.giste.navigator.features.trip.domain.Trip
import org.giste.navigator.features.trip.domain.TripRepository
import javax.inject.Inject

private val TRIP_PARTIAL = intPreferencesKey("TRIP_PARTIAL")
private val TRIP_TOTAL = intPreferencesKey("TRIP_TOTAL")
private const val PARTIAL_MAX = 999990
private const val TOTAL_MAX = 9999990

class DataStoreTripRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) : TripRepository {
    override fun getTrip(): Flow<Trip> {
        return dataStore.data.map {
            Trip(
                partial = it[TRIP_PARTIAL] ?: 0,
                total = it[TRIP_TOTAL] ?: 0,
            )
        }.distinctUntilChanged()
    }

    override suspend fun incrementPartial() {
        dataStore.edit { it[TRIP_PARTIAL] = getSafePartial((it[TRIP_PARTIAL] ?: 0).plus(10)) }
    }

    override suspend fun decrementPartial() {
        dataStore.edit { it[TRIP_PARTIAL] = getSafePartial((it[TRIP_PARTIAL] ?: 0).minus(10)) }
    }

    override suspend fun resetPartial() {
        dataStore.edit { it[TRIP_PARTIAL] = 0 }
    }

    override suspend fun resetTrip() {
        dataStore.edit { it[TRIP_PARTIAL] = 0 }
        dataStore.edit { it[TRIP_TOTAL] = 0 }
    }

    override suspend fun addDistance(distance: Int) {
        dataStore.edit {
            it[TRIP_PARTIAL] = getSafePartial((it[TRIP_PARTIAL] ?: 0).plus(distance))
            it[TRIP_TOTAL] = getSafeTotal((it[TRIP_TOTAL] ?: 0).plus(distance))
        }
    }

    override suspend fun setPartial(partial: Int) {
        dataStore.edit { it[TRIP_PARTIAL] = getSafePartial(partial) }
    }

    private fun getSafePartial(partial: Int): Int{
        return partial.coerceAtLeast(0).coerceAtMost(PARTIAL_MAX)
    }

    private fun getSafeTotal(total: Int): Int{
        return total.coerceAtLeast(0).coerceAtMost(TOTAL_MAX)
    }
}