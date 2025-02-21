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

package org.giste.navigator.features.trip.domain

import androidx.annotation.IntRange
import kotlinx.coroutines.flow.Flow

interface TripRepository {
    fun getTrip(): Flow<Trip> //TODO("Rename to getTrips()")
    suspend fun incrementPartial()
    suspend fun decrementPartial()
    suspend fun resetPartial()
    suspend fun resetTrip()
    suspend fun addDistance(@IntRange(from = 0, to = 99999) distance: Int)
    suspend fun setPartial(@IntRange(from = 0, to = 99999) partial: Int)
}