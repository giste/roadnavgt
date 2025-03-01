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

import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.giste.navigator.features.location.domain.Location
import org.giste.navigator.features.location.domain.LocationRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

@ExtendWith(MockKExtension::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GetTripsUseCaseTests {
    private val locationRepository = mockk<LocationRepository>()
    private val tripRepository = mockk<TripRepository>()

    @BeforeEach
    fun beforeEach() {
        clearAllMocks()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `trip collection must start location collection and distance calculation`() = runTest {
        val tripFlow = MutableSharedFlow<Trip>()
        val locationFlow = MutableSharedFlow<Location>()
        coEvery { locationRepository.getLocations() } returns locationFlow
        coEvery { tripRepository.addDistance(any()) } returns Unit
        coEvery { tripRepository.getTrips() } returns tripFlow
        val getTripsUseCase = GetTripsUseCase(
            tripRepository,
            locationRepository,
            UnconfinedTestDispatcher(testScheduler)
        )
        val actualTrips = mutableListOf<Trip>()

        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            getTripsUseCase().toList(actualTrips)
        }
        //First location doesn't call addDistance because previous location is null
        locationFlow.emit(Location(0.0, 0.0))
        locationFlow.emit(Location(0.1, 0.1))
        locationFlow.emit(Location(0.2, 0.2))

        coVerify(exactly = 2) { tripRepository.addDistance(any()) }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @ParameterizedTest
    @MethodSource("provideRoute")
    fun `adds correct distance`(route: Route) = runTest {
        var actualDistance = 0
        val distanceSlot = slot<Int>()
        val tripFlow = MutableSharedFlow<Trip>()
        val locationFlow = MutableSharedFlow<Location>()
        coEvery { locationRepository.getLocations() } returns locationFlow
        coEvery { tripRepository.addDistance(capture(distanceSlot)) } answers {
            actualDistance += distanceSlot.captured
        }
        coEvery { tripRepository.getTrips() } returns tripFlow
        val getTripsUseCase = GetTripsUseCase(
            tripRepository,
            locationRepository,
            UnconfinedTestDispatcher(testScheduler)
        )
        val actualTrips = mutableListOf<Trip>()

        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            getTripsUseCase().toList(actualTrips)
        }

        route.getLocations().onEach { locationFlow.emit(it) }
        assertEquals(route.getDistance().div(10), actualDistance.div(10))
    }

    private fun provideRoute() = Stream.of(
        TresCantosRoute,
        NavacerradaRoute,
    )
}