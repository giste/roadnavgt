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

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.giste.navigator.features.trip.domain.Trip
import org.giste.navigator.features.trip.domain.TripRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.io.TempDir
import java.io.File

private const val TEST_DATASTORE: String = "test.preferences_pb"
private const val PARTIAL_MAX = 999_990
private const val TOTAL_MAX = 9_999_990
private val TRIP_PARTIAL = intPreferencesKey("TRIP_PARTIAL")
private val TRIP_TOTAL = intPreferencesKey("TRIP_TOTAL")

@DisplayName("Unit tests for DataStoreTripRepository")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DataStoreTripRepositoryTests {
    @TempDir
    private lateinit var temporaryFolder: File
    private val testDataStore = PreferenceDataStoreFactory.create(
        produceFile = { File(temporaryFolder, TEST_DATASTORE) },
    )
    private val tripRepository: TripRepository = DataStoreTripRepository(testDataStore)

    @AfterEach
    fun afterEach(@TempDir temporaryFolder: File) = runTest {
        // Clear datastore
        testDataStore.edit { it.clear() }
    }

    @DisplayName("Given minimum partial (0)")
    @Nested
    inner class PartialIsZero {
        @Test
        fun `increment adds 10 meters to partial`() = runTest {
            tripRepository.incrementPartial()

            val actualPartial = testDataStore.data.map { it[TRIP_PARTIAL] }.first()
            assertEquals(10, actualPartial)
        }

        @Test
        fun `decrement does not change partial`() = runTest {
            tripRepository.decrementPartial()

            val actualPartial = testDataStore.data.map { it[TRIP_PARTIAL] }.first()
            assertEquals(0, actualPartial)
        }
    }

    @DisplayName("Given maximum partial (>=999990)")
    @Nested
    inner class PartialIsMax {
        @BeforeEach
        fun beforeEach() = runTest {
            testDataStore.edit { it[TRIP_PARTIAL] = PARTIAL_MAX }
        }

        @Test
        fun `increment does not change partial`() = runTest {
            tripRepository.incrementPartial()

            val actualPartial = testDataStore.data.map { it[TRIP_PARTIAL] }.first()
            assertEquals(PARTIAL_MAX, actualPartial)
        }

        @Test
        fun `decrement subtract 10 meters to partial `() = runTest {
            tripRepository.decrementPartial()

            val actualPartial = testDataStore.data.map { it[TRIP_PARTIAL] }.first()
            assertEquals(999980, actualPartial)
        }
    }

    @DisplayName("Given partial > 0 and total > 0")
    @Nested
    inner class PartialAndTotalGraterThanZero {
        @BeforeEach
        fun setup() = runTest {
            testDataStore.edit {
                it[TRIP_PARTIAL] = 123450
                it[TRIP_TOTAL] = 9876540
            }
        }

        @Test
        fun `reset partial sets it to 0`() = runTest {
            tripRepository.resetPartial()

            val partial = testDataStore.data.map { it[TRIP_PARTIAL] }.first()
            assertEquals(0, partial)
        }

        @Test
        fun `reset trip sets partial and total to 0`() = runTest {
            tripRepository.resetTrip()

            val partial = testDataStore.data.map { it[TRIP_PARTIAL] }.first()
            assertEquals(0, partial)
            val total = testDataStore.data.map { it[TRIP_TOTAL] }.first()
            assertEquals(0, total)
        }
    }

    @DisplayName("set partial")
    @Nested
    inner class SetPartialTests {
        @Test
        fun `updates partial when new value is in range (0-999_999)`() = runTest {
            val expectedPartial = 123_456

            tripRepository.setPartial(expectedPartial)

            val actualPartial = testDataStore.data.map { it[TRIP_PARTIAL] }.first()
            assertEquals(expectedPartial, actualPartial)
        }

        @Test
        fun `sets the maximum when new value is greater than 999990`() = runTest {
            tripRepository.setPartial(1_000_000)

            val partial = testDataStore.data.map { it[TRIP_PARTIAL] }.first()
            assertEquals(PARTIAL_MAX, partial)
        }

        @Test
        fun `sets the minimum when it's less than 0`() = runTest {
            tripRepository.setPartial(-1)

            val partial = testDataStore.data.map { it[TRIP_PARTIAL] }.first()
            assertEquals(0, partial)
        }
    }

    @DisplayName("add distance")
    @Nested
    inner class AddDistanceTests {
        @Test
        fun `distance is added to partial and total`() = runTest {
            val expectedTrip = Trip(100, 100)

            tripRepository.addDistance(100)

            val actualTrip = Trip(
                partial = testDataStore.data.map { it[TRIP_PARTIAL] ?: 0 }.first(),
                total = testDataStore.data.map { it[TRIP_TOTAL] ?: 0 }.first(),
            )
            assertEquals(expectedTrip, actualTrip)
        }

        @Test
        fun `adding distance does not exceed maximum partial or total`() = runTest {
            testDataStore.edit {
                it[TRIP_PARTIAL] = 999900
                it[TRIP_TOTAL] = 9999900
            }
            val expectedTrip = Trip(PARTIAL_MAX, TOTAL_MAX)

            tripRepository.addDistance(100)

            val actualTrip = Trip(
                partial = testDataStore.data.map { it[TRIP_PARTIAL] ?: 0 }.first(),
                total = testDataStore.data.map { it[TRIP_TOTAL] ?: 0 }.first(),
            )
            assertEquals(expectedTrip, actualTrip)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `trips are emitted when datastore changes`() = runTest(UnconfinedTestDispatcher()) {
        val trip0 = Trip(0, 0)
        val trip1 = Trip(10, 10)
        val trip2 = Trip(20, 20)
        val trip3 = Trip(30, 30)
        val expectedTrips = listOf(trip0, trip1, trip2, trip3)
        val actualTrips = mutableListOf<Trip>()

        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            tripRepository.getTrips().toList(actualTrips)
        }

        testDataStore.edit {
            it[TRIP_PARTIAL] = 10
            it[TRIP_TOTAL] = 10
        }
        testDataStore.edit {
            it[TRIP_PARTIAL] = 20
            it[TRIP_TOTAL] = 20
        }
        testDataStore.edit {
            it[TRIP_PARTIAL] = 30
            it[TRIP_TOTAL] = 30
        }

        assertEquals(expectedTrips, actualTrips)
    }
}