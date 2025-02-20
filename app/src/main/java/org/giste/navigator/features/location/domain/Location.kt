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

import android.util.Log
import androidx.annotation.FloatRange
import kotlin.math.PI
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

private const val TAG = "Location"

data class Location(
    @FloatRange(from = -90.0, to = 90.0) val latitude: Double,
    @FloatRange(from = -180.0, to = 180.0) val longitude: Double,
    val altitude: Double? = null,
    @FloatRange(from = 0.0, to = 360.0) val bearing: Float? = null,
    val horizontalAccuracy: Float? = null,
    val verticalAccuracy: Float? = null,
) {
    fun distanceTo(otherLocation: Location): Double {
        val earthRadius = 6367.45
        val lat1 = toRad(this.latitude)
        val lat2 = toRad(otherLocation.latitude)
        val lon1 = toRad(this.longitude)
        val lon2 = toRad(otherLocation.longitude)
        val distance =
            earthRadius * acos(sin(lat1) * sin(lat2) + cos(lat1) * cos(lat2) * cos(lon2 - lon1)) * 1000

        if (this.altitude != null && otherLocation.altitude != null) {
            val distanceWithAltitude = sqrt(
                (this.altitude - otherLocation.altitude)
                    .pow(2)
                    .plus(distance.pow(2))
            )

            Log.d(TAG, "Distance with altitude: $distanceWithAltitude; Distance: $distance")

            return distanceWithAltitude
        }

        return distance
    }

    private fun toRad(degrees: Double): Double = degrees * PI / 180
}
