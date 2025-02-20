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

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationListener
import android.location.LocationManager
import android.util.Log
import androidx.core.content.ContextCompat.getSystemService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.giste.navigator.features.location.domain.Location
import org.giste.navigator.features.location.domain.LocationPermissionException
import org.giste.navigator.features.location.domain.LocationRepository
import org.giste.navigator.features.settings.domain.SettingsRepository
import javax.inject.Inject

private const val TAG = "LocationManagerLocationRepository"

class LocationManagerLocationRepository @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val context: Context,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : LocationRepository {
    private val locationManager: LocationManager by lazy {
        getSystemService(context, LocationManager::class.java) as LocationManager
    }

    override fun getLocations(): Flow<Location> = callbackFlow {
        val locationCallback = LocationListener { location ->
            Log.v(TAG, "Location from Manager: $location")

            launch(dispatcher) {
                with(location) {
                    send(
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
        }

        val settings = settingsRepository.getSettings().first()

        if (context.checkSelfPermission(
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && context.checkSelfPermission(
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            throw LocationPermissionException()
        }

        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            settings.locationMinTime,
            settings.locationMinDistance.toFloat(),
            locationCallback
        )

        awaitClose {
            // No one listens to flow anymore
            locationManager.removeUpdates(locationCallback)
        }
    }
}