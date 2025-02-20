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

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.giste.navigator.features.settings.domain.Settings
import org.giste.navigator.features.settings.domain.SettingsRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

private const val TEST_DATASTORE: String = "test.preferences_pb"
private val LOCATION_MIN_TIME = longPreferencesKey("SETTINGS_LOCATION_MIN_TIME")
private val LOCATION_MIN_DISTANCE = intPreferencesKey("SETTINGS_LOCATION_MIN_DISTANCE")

class DataStoreSettingsRepositoryTests {
    private lateinit var testDataStore: DataStore<Preferences>
    private lateinit var repository: SettingsRepository

    @BeforeEach
    fun beforeEach(@TempDir temporaryFolder: File){
        testDataStore = PreferenceDataStoreFactory.create(
            produceFile = { File(temporaryFolder, TEST_DATASTORE) },
        )

        repository = DataStoreSettingsRepository(
            dataStore = testDataStore,
        )
    }

    @Test
    fun `store settings when saved`() = runTest {
        val settings = Settings(500L, 5)

        repository.saveSettings(settings)

        val readSettings = testDataStore.data.map {
            Settings(
                locationMinTime = it[LOCATION_MIN_TIME] ?: 1_000L,
                locationMinDistance = it[LOCATION_MIN_DISTANCE] ?: 10,
            )
        }.first()
        Assertions.assertEquals(settings, readSettings)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `returns new settings each time one is saved`() = runTest {
        val settings1 = Settings()
        val settings2 = Settings(100L, 1)
        val settings3 = Settings(200L, 2)

        val settings = mutableListOf<Settings>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            repository.getSettings().toList(settings)
        }

        repository.saveSettings(settings2)
        repository.saveSettings(settings3)

        Assertions.assertEquals(listOf(settings1, settings2, settings3), settings)
    }
}