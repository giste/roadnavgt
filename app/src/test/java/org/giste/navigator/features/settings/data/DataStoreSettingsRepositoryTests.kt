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

package org.giste.navigator.features.settings.data

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.giste.navigator.features.settings.domain.Settings
import org.giste.navigator.features.settings.domain.SettingsRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.io.TempDir
import java.io.File

private const val TEST_DATASTORE: String = "test.preferences_pb"
private val LOCATION_MIN_TIME = longPreferencesKey("SETTINGS_LOCATION_MIN_TIME")
private val LOCATION_MIN_DISTANCE = intPreferencesKey("SETTINGS_LOCATION_MIN_DISTANCE")

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DataStoreSettingsRepositoryTests {
    @TempDir
    private lateinit var temporaryFolder: File
    private val testDataStore = PreferenceDataStoreFactory.create(
        produceFile = { File(temporaryFolder, TEST_DATASTORE) },
    )
    private val settingsRepository: SettingsRepository = DataStoreSettingsRepository(
        dataStore = testDataStore,
    )

    @AfterEach
    fun afterEach() = runTest {
        // Clear datastore
        testDataStore.edit { it.clear() }
    }

    @Test
    fun `store settings when saved`() = runTest {
        val expectedSettings = Settings(500L, 5)

        settingsRepository.saveSettings(expectedSettings)

        val actualSettings = testDataStore.data.map {
            Settings(
                locationMinTime = it[LOCATION_MIN_TIME] ?: 1_000L,
                locationMinDistance = it[LOCATION_MIN_DISTANCE] ?: 10,
            )
        }.first()
        Assertions.assertEquals(expectedSettings, actualSettings)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `returns new settings each time one is saved`() = runTest {
        val settings1 = Settings()
        val settings2 = Settings(100L, 1)
        val settings3 = Settings(200L, 2)
        val actualSettings = mutableListOf<Settings>()

        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            settingsRepository.getSettings().toList(actualSettings)
        }

        settingsRepository.saveSettings(settings2)
        settingsRepository.saveSettings(settings3)
        Assertions.assertEquals(listOf(settings1, settings2, settings3), actualSettings)
    }
}