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

import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.giste.navigator.features.trip.domain.Trip
import org.giste.navigator.features.trip.domain.TripRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import kotlin.math.absoluteValue

@DisplayName("Unit tests for GetLocationsUseCase")
@ExtendWith(MockKExtension::class)
class GetLocationsUseCaseTests {
    @MockK private lateinit var locationRepository: LocationRepository

    @Test
    fun `locations must generate distances`() = runTest {
        val tripRepository = FakeTripRepository()
        coEvery { locationRepository.getLocations() } returns TresCantosRoute.getLocations().asFlow()

        val getLocationsUseCase = GetLocationsUseCase(
            locationRepository = locationRepository,
            tripRepository = tripRepository,
        )

        val locations = getLocationsUseCase().toList()

        assertEquals(TresCantosRoute.getLocations().size, locations.size)
        assertTrue(TresCantosRoute.getDistance().minus(tripRepository.distance).absoluteValue < 10)
    }

    class FakeTripRepository : TripRepository {
        var distance: Int = 0

        override fun getTrip(): Flow<Trip> {
            TODO("Not yet implemented")
        }

        override suspend fun incrementPartial() {
            TODO("Not yet implemented")
        }

        override suspend fun decrementPartial() {
            TODO("Not yet implemented")
        }

        override suspend fun resetPartial() {
            TODO("Not yet implemented")
        }

        override suspend fun resetTrip() {
            TODO("Not yet implemented")
        }

        override suspend fun addDistance(distance: Int) {
            this.distance += distance
        }

        override suspend fun setPartial(partial: Int) {
            TODO("Not yet implemented")
        }

        override suspend fun setTotal(total: Int) {
            TODO("Not yet implemented")
        }

    }
}