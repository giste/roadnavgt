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

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.giste.navigator.features.trip.domain.Trip
import org.giste.navigator.features.trip.domain.TripRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

private const val TEST_DATASTORE: String = "test.preferences_pb"
private val TRIP_PARTIAL = intPreferencesKey("TRIP_PARTIAL")
private val TRIP_TOTAL = intPreferencesKey("TRIP_TOTAL")

@DisplayName("Unit tests for DataStoreTripRepository")
class DataStoreTripRepositoryTests {
    private lateinit var testDataStore: DataStore<Preferences>
    private lateinit var tripRepository: TripRepository

    @BeforeEach
    fun beforeEach(@TempDir temporaryFolder: File) = runTest {
        testDataStore = PreferenceDataStoreFactory.create(
            produceFile = { File(temporaryFolder, TEST_DATASTORE) },
        )

        tripRepository = DataStoreTripRepository(testDataStore)

        // Reset data store
        testDataStore.edit {
            it[TRIP_PARTIAL] = 0
            it[TRIP_TOTAL] = 0
        }
    }

    @DisplayName("Given minimum partial (0)")
    @Nested
    inner class PartialIsZero {
        @Test
        fun `incrementPartial() adds 10 meters`() = runTest {
            tripRepository.incrementPartial()

            val partial = testDataStore.data.map { it[TRIP_PARTIAL] }.first()
            Assertions.assertEquals(10, partial)
        }

        @Test
        fun `decrementPartial() does not change partial`() = runTest {
            tripRepository.decrementPartial()

            val partial = testDataStore.data.map { it[TRIP_PARTIAL] }.first()
            Assertions.assertEquals(0, partial)
        }
    }

    @DisplayName("Given maximum partial (>=999990)")
    @Nested
    inner class PartialIsMax {
        @BeforeEach
        fun beforeEach() = runTest {
            testDataStore.edit { it[TRIP_PARTIAL] = 999990 }
        }

        @Test
        fun `incrementPartial() does not change partial`() = runTest {
            tripRepository.incrementPartial()

            val partial = testDataStore.data.map { it[TRIP_PARTIAL] }.first()
            Assertions.assertEquals(999990, partial)
        }

        @Test
        fun `decrementPartial() subtract 10 meters`() = runTest {
            tripRepository.decrementPartial()

            val partial = testDataStore.data.map { it[TRIP_PARTIAL] }.first()
            Assertions.assertEquals(999980, partial)
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
        fun `resetPartial() sets partial to 0`() = runTest {
            tripRepository.resetPartial()

            val partial = testDataStore.data.map { it[TRIP_PARTIAL] }.first()
            Assertions.assertEquals(0, partial)
        }

        @Test
        fun `resetTrip() sets partial and total to 0`() = runTest {
            tripRepository.resetTrip()

            val partial = testDataStore.data.map { it[TRIP_PARTIAL] }.first()
            Assertions.assertEquals(0, partial)
            val total = testDataStore.data.map { it[TRIP_TOTAL] }.first()
            Assertions.assertEquals(0, total)
        }
    }

    @DisplayName("setPartial()")
    @Nested
    inner class SetPartialTests {
        @Test
        fun `updates partial when it's in range (0-999999)`() = runTest {
            tripRepository.setPartial(123450)

            val partial = testDataStore.data.map { it[TRIP_PARTIAL] }.first()
            Assertions.assertEquals(123450, partial)
        }

        @Test
        fun `set maximum when it's greater than 999990`() = runTest {
            tripRepository.setPartial(1000000)

            val partial = testDataStore.data.map { it[TRIP_PARTIAL] }.first()
            Assertions.assertEquals(999990, partial)
        }

        @Test
        fun `set minimum when it's less than zero`() = runTest {
            tripRepository.setPartial(-1)

            val partial = testDataStore.data.map { it[TRIP_PARTIAL] }.first()
            Assertions.assertEquals(0, partial)
        }
    }

    @DisplayName("addDistance()")
    @Nested
    inner class AddDistanceTests {
        @Test
        fun `distance is added to partial and total`() = runTest {
            tripRepository.addDistance(100)

            val partial = testDataStore.data.map { it[TRIP_PARTIAL] }.first()
            Assertions.assertEquals(100, partial)
            val total = testDataStore.data.map { it[TRIP_TOTAL] }.first()
            Assertions.assertEquals(100, total)
        }

        @Test
        fun `adding distance does not exceed maximum partial or total`() = runTest {
            testDataStore.edit {
                it[TRIP_PARTIAL] = 999900
                it[TRIP_TOTAL] = 9999900
            }

            tripRepository.addDistance(100)

            val partial = testDataStore.data.map { it[TRIP_PARTIAL] }.first()
            Assertions.assertEquals(999990, partial)
            val total = testDataStore.data.map { it[TRIP_TOTAL] }.first()
            Assertions.assertEquals(9999990, total)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `trips are emitted when datastore changes`() = runTest(UnconfinedTestDispatcher()) {
        val trip0 = Trip(0, 0)
        val trip1 = Trip(10, 10)
        val trip2 = Trip(20, 20)
        val trip3 = Trip(30, 30)
        val trips = mutableListOf<Trip>()

        val job = launch { tripRepository.getTrip().toList(trips) }
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
        advanceUntilIdle()
        job.cancel()

        Assertions.assertEquals(listOf(trip0, trip1, trip2, trip3), trips)
    }
}