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

import android.util.Log
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import org.giste.navigator.features.location.domain.Location
import org.giste.navigator.features.location.domain.LocationRepository
import kotlin.math.roundToInt

private const val TAG = "GetTripsUseCase"

class GetTripsUseCase(
    private val tripRepository: TripRepository,
    private val locationRepository: LocationRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default,
) {
    private val scope = CoroutineScope(dispatcher)

    operator fun invoke(): Flow<Trip> = tripRepository.getTrips()
        .onStart {
            Log.i(TAG, "Starting location collection")
            scope.launch { startLocationCollection() }
        }
        .onCompletion { scope.cancel() }
        .flowOn(dispatcher)

    private suspend fun startLocationCollection() {
        var lastLocation: Location? = null

        locationRepository.getLocations()
            .onEach { location ->
                lastLocation?.let {
                    val distance = it.distanceTo(location).roundToInt()
                    tripRepository.addDistance(distance)
                    Log.v(TAG, "Added distance: $distance")
                }
                lastLocation = location
            }.collect()
    }
}