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

import android.location.LocationListener
import android.location.LocationManager
import android.os.Looper
import android.util.Log
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.giste.navigator.features.location.domain.Location
import org.giste.navigator.features.trip.domain.TresCantosRoute
import org.giste.navigator.features.settings.domain.Settings
import org.giste.navigator.features.settings.domain.SettingsRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith

private const val TAG = "ManagerLocationRepositoryTests"

@ExtendWith(MockKExtension::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ManagerLocationRepositoryTests {
    private val settingRepository: SettingsRepository = mockk<SettingsRepository>().apply {
        coEvery { getSettings() } returns flowOf(Settings())
    }
    private val locationManager: LocationManager = mockk<LocationManager>().apply {
        coEvery { removeUpdates(any<LocationListener>()) } returns Unit
    }
    private val looper: Looper = mockk(relaxed = true)
    private val scope = CoroutineScope(SupervisorJob())

    @AfterEach
    fun afterEach() {
        clearAllMocks()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `emits one location on each new location from LocationManager`() = runTest {
        val listenerSlot = slot<LocationListener>()
        coEvery {
            locationManager.requestLocationUpdates(
                any<String>(), any<Long>(), any<Float>(), capture(listenerSlot), any<Looper>()
            )
        } returns Unit

        val locationRepository = ManagerLocationRepository(
            locationManager = locationManager,
            looper = looper,
            settingsRepository = settingRepository,
            externalScope = scope,
            dispatcher = UnconfinedTestDispatcher(testScheduler)
        )

        val actualLocations = mutableListOf<Location>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            locationRepository.getLocations().toList(actualLocations)
        }

        TresCantosRoute.getLocations().map {
            Log.d(TAG, "Route location: $it")
            mockk<android.location.Location>(relaxed = true).apply {
                every { latitude } returns it.latitude
                every { longitude } returns it.longitude
                every { altitude } returns it.altitude!!
                every { hasAltitude() } returns true
            }
        }.onEach {
            listenerSlot.captured.onLocationChanged(it)
        }

        assertEquals(TresCantosRoute.getLocations(), actualLocations)
    }
}