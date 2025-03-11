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

import android.Manifest
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.pressKey
import dagger.hilt.android.testing.HiltAndroidTest
import de.mannodermaus.junit5.compose.createAndroidComposeExtension
import de.mannodermaus.junit5.extensions.GrantPermissionExtension
import org.giste.navigator.MainActivity
import org.giste.navigator.features.trip.ui.TRIP_PARTIAL
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.RegisterExtension

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@HiltAndroidTest
class NavigatorTripInstrumentedTests {
    @OptIn(ExperimentalTestApi::class)
    @RegisterExtension
    @JvmField
    val extension = createAndroidComposeExtension<MainActivity>()

    @RegisterExtension
    @JvmField
    val permissions = GrantPermissionExtension.grant(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
    )

    @BeforeEach
    fun beforeEach() {
        extension.use {
            onNodeWithTag(RESET_TRIP).performClick()
        }
    }

    @Test
    fun partial_increments_by_one_when_increase_button_is_pressed() {
        extension.use {
            onNodeWithTag(INCREASE_PARTIAL).performClick()
            waitUntilAtLeastOneExists(hasText("%,.2f".format(0.01f)))
            onNodeWithTag(TRIP_PARTIAL).assertTextEquals("%,.2f".format(0.01f))
        }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun partial_increments_by_one_when_right_key_is_pressed() {
        extension.use {
            onNodeWithTag(NAVIGATION_CONTENT).performKeyInput {
                pressKey(Key.DirectionRight)
            }
            waitUntilAtLeastOneExists(hasText("%,.2f".format(0.01f)))
            onNodeWithTag(TRIP_PARTIAL).assertTextEquals("%,.2f".format(0.01f))
        }
    }

    @Test
    fun partial_decrements_by_one_when_decrease_button_is_pressed() {
        extension.use {
            onNodeWithTag(INCREASE_PARTIAL).performClick()
            onNodeWithTag(INCREASE_PARTIAL).performClick()
            waitUntilAtLeastOneExists(hasText("%,.2f".format(0.02f)))
            onNodeWithTag(TRIP_PARTIAL).assertTextEquals("%,.2f".format(0.02f))

            onNodeWithTag(DECREASE_PARTIAL).performClick()
            waitUntilAtLeastOneExists(hasText("%,.2f".format(0.01f)))
            onNodeWithTag(TRIP_PARTIAL).assertTextEquals("%,.2f".format(0.01f))
        }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun partial_decrements_by_one_when_left_key_is_pressed() {
        extension.use {
            onNodeWithTag(INCREASE_PARTIAL).performClick()
            onNodeWithTag(INCREASE_PARTIAL).performClick()
            waitUntilAtLeastOneExists(hasText("%,.2f".format(0.02f)))
            onNodeWithTag(TRIP_PARTIAL).assertTextEquals("%,.2f".format(0.02f))

            onNodeWithTag(NAVIGATION_CONTENT).performKeyInput {
                pressKey(Key.DirectionLeft)
            }
            waitUntilAtLeastOneExists(hasText("%,.2f".format(0.01f)))
            onNodeWithTag(TRIP_PARTIAL).assertTextEquals("%,.2f".format(0.01f))
        }
    }

    @Test
    fun partial_resets_when_reset_button_is_pressed() {
        extension.use {
            onNodeWithTag(INCREASE_PARTIAL).performClick()
            waitUntilAtLeastOneExists(hasText("%,.2f".format(0.01f)))
            onNodeWithTag(TRIP_PARTIAL).assertTextEquals("%,.2f".format(0.01f))

            onNodeWithTag(RESET_PARTIAL).performClick()
            waitUntilDoesNotExist(hasText("%,.2f".format(0.01f)))
            onNodeWithTag(TRIP_PARTIAL).assertTextEquals("%,.2f".format(0.0f))
        }
    }

//    @OptIn(ExperimentalTestApi::class)
//    @Test
//    fun partial_resets_when_F6_key_is_pressed() {
//        extension.use {
//            onNodeWithTag(INCREASE_PARTIAL).performClick()
//            waitUntilAtLeastOneExists(hasText("%,.2f".format(0.01f)))
//            onNodeWithTag(TRIP_PARTIAL).assertTextEquals("%,.2f".format(0.01f))
//
//            onNodeWithTag(NAVIGATION_CONTENT).performKeyInput {
//                pressKey(Key.F6)
//            }
//            waitUntilDoesNotExist(hasText("%,.2f".format(0.01f)))
//            onNodeWithTag(TRIP_PARTIAL).assertTextEquals("%,.2f".format(0.0f))
//        }
//    }
}