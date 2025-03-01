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

package org.giste.navigator.features.location.data

import android.annotation.SuppressLint
import android.location.LocationListener
import android.location.LocationManager
import android.util.Log
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.withContext
import org.giste.navigator.ApplicationScope
import org.giste.navigator.IoDispatcher
import org.giste.navigator.features.location.domain.Location
import org.giste.navigator.features.location.domain.LocationRepository
import org.giste.navigator.features.settings.domain.SettingsRepository
import javax.inject.Inject

private const val TAG = "ManagerLocationRepository"

@SuppressLint("MissingPermission")
class ManagerLocationRepository @Inject constructor(
    private val locationManager: LocationManager,
    private val settingsRepository: SettingsRepository,
    @ApplicationScope externalScope: CoroutineScope,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
) : LocationRepository {
    private val locationList: Flow<Location> = callbackFlow {
        val locationCallback = LocationListener { location ->
            Log.v(TAG, "Location from Manager: ${location.latitude}")
            with(location) {
                trySend(
                    Location(
                        latitude = latitude,
                        longitude = longitude,
                        altitude = if (hasAltitude()) altitude else null,
                        bearing = if (hasBearing()) bearing else null,
                        horizontalAccuracy = if (hasAccuracy()) accuracy else null,
                        verticalAccuracy = if (hasVerticalAccuracy()) verticalAccuracyMeters else null,
                    )
                )
            }
        }

        val settings = settingsRepository.getSettings().first()

        withContext(dispatcher) {
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                settings.locationMinTime,
                settings.locationMinDistance.toFloat(),
                locationCallback
            )
        }

        awaitClose {
            // No one listens to flow anymore
            locationManager.removeUpdates(locationCallback)
        }
    }.shareIn(
        scope = externalScope,
        started = SharingStarted.WhileSubscribed(5_000)
    )

    override fun getLocations(): Flow<Location> = locationList
}