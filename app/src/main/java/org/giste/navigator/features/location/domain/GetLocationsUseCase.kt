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

package org.giste.navigator.features.location.domain

import kotlinx.coroutines.flow.onEach
import org.giste.navigator.features.trip.domain.TripRepository
import kotlin.math.roundToInt

class GetLocationsUseCase(
    private val locationRepository: LocationRepository,
    private val tripRepository: TripRepository,
) {
    private var lastLocation: Location? = null

    operator fun invoke() = locationRepository
        .getLocations()
        .onEach { location ->
            lastLocation?.let {
                val distance = it.distanceTo(location).roundToInt()
                tripRepository.addDistance(distance)
            }
            lastLocation = location
        }
}