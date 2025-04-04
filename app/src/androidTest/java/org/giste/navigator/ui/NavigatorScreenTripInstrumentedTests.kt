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

package org.giste.navigator.ui

import android.icu.text.DecimalFormatSymbols
import android.icu.text.NumberFormat
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.pressKey
import de.mannodermaus.junit5.compose.createComposeExtension
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.giste.navigator.features.location.domain.Location
import org.giste.navigator.features.roadbook.domain.Roadbook
import org.giste.navigator.features.settings.domain.Settings
import org.giste.navigator.features.trip.domain.Trip
import org.giste.navigator.features.trip.ui.TRIP_PARTIAL
import org.giste.navigator.features.trip.ui.TRIP_TOTAL
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.RegisterExtension

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class NavigatorScreenTripInstrumentedTests {
    @OptIn(ExperimentalTestApi::class)
    @RegisterExtension
    @JvmField
    val extension = createComposeExtension()

    private val viewModel = mockk<NavigatorViewModel>()

    private val locationFlow = MutableStateFlow<Location?>(null)
    private val mapFlow = MutableStateFlow<List<String>>(emptyList())
    private val roadbookFlow = MutableStateFlow<Roadbook>(Roadbook.NotLoaded)
    private val settingsFlow = MutableStateFlow(Settings())
    private val tripFlow = MutableStateFlow(Trip())

    private val decimalFormatSymbols: DecimalFormatSymbols = DecimalFormatSymbols.getInstance()
    private val formatter = NumberFormat.getInstance(decimalFormatSymbols.locale)
        .apply {
            isGroupingUsed = true
            minimumFractionDigits = 2
            maximumFractionDigits = 2
        }

    @BeforeEach
    fun beforeEach() {
        clearAllMocks()
        locationFlow.update { null }
        mapFlow.update { emptyList() }
        roadbookFlow.update { Roadbook.NotLoaded }
        settingsFlow.update { Settings() }
        tripFlow.update { Trip() }
        every { viewModel.locationState } returns locationFlow.asStateFlow()
        every { viewModel.mapSourceState } returns mapFlow.asStateFlow()
        every { viewModel.roadbookState } returns roadbookFlow.asStateFlow()
        every { viewModel.settingsState } returns settingsFlow.asStateFlow()
        every { viewModel.tripState } returns tripFlow.asStateFlow()
        every { viewModel.onAction(any()) } returns Unit
    }

    @Test
    fun shows_correct_trip_values() {
        extension.use {
            setContent {
                NavigatorScreen(
                    viewModel = viewModel,
                    navigateToSettings = {},
                    navigateToMapManager = {},
                )
            }

            tripFlow.update { Trip(123450, 9876540) }
            waitForIdle()

            onNodeWithTag(TRIP_PARTIAL).assertTextEquals(formatter.format(123.45f))
            onNodeWithTag(TRIP_TOTAL).assertTextEquals(formatter.format(9876.54f))
        }
    }

    @Test
    fun increments_partial_when_increase_button_is_pressed() {
        extension.use {
            setContent {
                NavigatorScreen(
                    viewModel = viewModel,
                    navigateToSettings = {},
                    navigateToMapManager = {}
                )
            }

            onNodeWithTag(INCREASE_PARTIAL).performClick()

            verify {
                viewModel.onAction(NavigatorViewModel.UiAction.IncrementPartial)
            }
        }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun increments_partial_when_right_key_is_pressed() {
        extension.use {
            setContent {
                NavigatorScreen(
                    viewModel = viewModel,
                    navigateToSettings = {},
                    navigateToMapManager = {}
                )
            }

            onNodeWithTag(NAVIGATION_CONTENT).performKeyInput {
                pressKey(Key.DirectionRight)
            }

            verify {
                viewModel.onAction(NavigatorViewModel.UiAction.IncrementPartial)
            }
        }
    }

    @Test
    fun decrements_partial_when_decrease_button_is_pressed() {
        tripFlow.update { Trip(100, 100) }

        extension.use {
            setContent {
                NavigatorScreen(
                    viewModel = viewModel,
                    navigateToSettings = {},
                    navigateToMapManager = {}
                )
            }

            onNodeWithTag(DECREASE_PARTIAL).performClick()

            verify {
                viewModel.onAction(NavigatorViewModel.UiAction.DecrementPartial)
            }
        }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun decrements_partial_when_left_key_is_pressed() {
        tripFlow.update { Trip(100, 100) }

        extension.use {
            setContent {
                NavigatorScreen(
                    viewModel = viewModel,
                    navigateToSettings = {},
                    navigateToMapManager = {}
                )
            }

            onNodeWithTag(DECREASE_PARTIAL).performKeyInput {
                pressKey(Key.DirectionLeft)
            }

            verify {
                viewModel.onAction(NavigatorViewModel.UiAction.DecrementPartial)
            }
        }
    }

    @Test
    fun resets_partial_when_reset_button_is_pressed() {
        tripFlow.update { Trip(100, 100) }

        extension.use {
            setContent {
                NavigatorScreen(
                    viewModel = viewModel,
                    navigateToSettings = {},
                    navigateToMapManager = {}
                )
            }

            onNodeWithTag(RESET_PARTIAL).performClick()

            verify {
                viewModel.onAction(NavigatorViewModel.UiAction.ResetPartial)
            }
        }
    }

//    @OptIn(ExperimentalTestApi::class)
//    @Test
//    fun resets_partial_when_f6_key_is_pressed() {
//        tripFlow.update { Trip(100, 100) }
//
//        extension.use {
//            setContent { NavigatorScreen(viewModel = viewModel, navigateToSettings = {}) }
//
//            onNodeWithTag(RESET_PARTIAL).performKeyInput {
//                val key = Key.F5
//                pressKey(key)
//                println("Key: ${key.nativeKeyCode}")
//            }
//
//            verify {
//                viewModel.onAction(NavigatorViewModel.UiAction.ResetPartial)
//            }
//        }
//    }

    @Test
    fun resets_trip_when_rest_trip_button_is_pressed() {
        tripFlow.update { Trip(100, 100) }

        extension.use {
            setContent {
                NavigatorScreen(
                    viewModel = viewModel,
                    navigateToSettings = {},
                    navigateToMapManager = {}
                )
            }

            onNodeWithTag(RESET_TRIP).performClick()

            verify {
                viewModel.onAction(NavigatorViewModel.UiAction.ResetTrip)
            }
        }
    }
}