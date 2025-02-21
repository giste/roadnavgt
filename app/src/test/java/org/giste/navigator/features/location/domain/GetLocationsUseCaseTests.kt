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

import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.giste.navigator.features.trip.domain.Trip
import org.giste.navigator.features.trip.domain.TripRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream
import kotlin.math.absoluteValue

@DisplayName("Unit tests for GetLocationsUseCase")
@ExtendWith(MockKExtension::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GetLocationsUseCaseTests {
    private val locationRepository: LocationRepository = mockk()
    private lateinit var tripRepository: FakeTripRepository
    private lateinit var getLocationsUseCase: GetLocationsUseCase

    @BeforeEach
    fun beforeEach() {
        clearAllMocks()
        tripRepository = FakeTripRepository()
        getLocationsUseCase = GetLocationsUseCase(
            locationRepository = locationRepository,
            tripRepository = tripRepository,
        )
    }

    @ParameterizedTest
    @MethodSource("routeProvider")
    fun `locations must generate correct distances`(route: Route) = runTest {
        coEvery { locationRepository.getLocations() } returns route.getLocations().asFlow()

        val locations = getLocationsUseCase().toList()

        assertEquals(route.getLocations().size, locations.size)
        assertTrue(route.getDistance().minus(tripRepository.distance).absoluteValue < 10)
    }

    private fun routeProvider() = Stream.of(
        TresCantosRoute,
        NavacerradaRoute,
    )

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
    }
}