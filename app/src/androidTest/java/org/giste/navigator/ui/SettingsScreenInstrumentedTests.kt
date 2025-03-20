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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.click
import androidx.compose.ui.test.hasProgressBarRangeInfo
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import de.mannodermaus.junit5.compose.createComposeExtension
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.giste.navigator.features.settings.domain.Settings
import org.giste.navigator.ui.NavigatorDialogTags.DIALOG_ACCEPT
import org.giste.navigator.ui.NumberDialogTags.NUMBER_DIALOG_KEY_DELETE
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.RegisterExtension

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SettingsScreenInstrumentedTests {
    @OptIn(ExperimentalTestApi::class)
    @RegisterExtension
    @JvmField
    val extension = createComposeExtension()

    private val viewModel = mockk<SettingsViewModel>()

    private val decimalFormatSymbols: DecimalFormatSymbols = DecimalFormatSymbols.getInstance()
    private val formatter = NumberFormat.getInstance(decimalFormatSymbols.locale)
        .apply {
            isGroupingUsed = true
            minimumFractionDigits = 0
            maximumFractionDigits = 0
        }

    @BeforeEach
    fun beforeEach() {
        clearAllMocks()
        every { viewModel.settingsState } returns MutableStateFlow(Settings()).asStateFlow()
        every { viewModel.onAction(any()) } returns Unit
    }

    @Test
    fun displays_settings_correctly() {
        extension.use {
            setContent { SettingsScreen(settingsViewModel =  viewModel, navigateBack = {}) }

            waitUntilExactlyOneExists(
                hasProgressBarRangeInfo(
                    ProgressBarRangeInfo(
                        current = 19f,
                        range = 15f..20f,
                        steps = 4,
                    )
                )
            )
            waitUntilExactlyOneExists(hasText(formatter.format(317)))
            waitUntilExactlyOneExists(hasText(formatter.format(1_000)))
            waitUntilExactlyOneExists(hasText(formatter.format(10)))
        }
    }

    @Test
    fun saves_zoom_level_when_changed() {
        extension.use {
            setContent { SettingsScreen(settingsViewModel =  viewModel, navigateBack = {}) }

            onNode(hasProgressBarRangeInfo(ProgressBarRangeInfo(19f, 15f..20f, 4)))
                .performTouchInput { this.click(Offset(this.centerX - 1f, this.centerY)) }

            verify { viewModel.onAction(SettingsViewModel.UiAction.OnMapZoomLevelChange(17)) }
        }
    }

    @Test
    fun saves_pixels_to_move_when_changed() {
        extension.use {
            setContent { SettingsScreen(settingsViewModel =  viewModel, navigateBack = {}) }

            waitUntilExactlyOneExists(hasText("317"))
            onNodeWithText("317").performClick()

            waitUntilExactlyOneExists(hasTestTag(NUMBER_DIALOG_KEY_DELETE))

            onNodeWithTag(NUMBER_DIALOG_KEY_DELETE).performClick()
            onNodeWithTag(DIALOG_ACCEPT).performClick()
        }

        verify { viewModel.onAction(SettingsViewModel.UiAction.OnPixelsToMoveRoadbookChange(31)) }
    }

    @Test
    fun saves_min_distance_when_changed() {
        extension.use {
            setContent { SettingsScreen(settingsViewModel =  viewModel, navigateBack = {}) }

            val formatedDistance = formatter.format(10)

            waitUntilExactlyOneExists(hasText(formatedDistance))
            onNodeWithText(formatedDistance).performClick()

            waitUntilExactlyOneExists(hasTestTag(NUMBER_DIALOG_KEY_DELETE))

            onNodeWithTag(NUMBER_DIALOG_KEY_DELETE).performClick()
            onNodeWithTag(DIALOG_ACCEPT).performClick()
        }

        verify { viewModel.onAction(SettingsViewModel.UiAction.OnLocationMinDistanceChange(1)) }
    }

    @Test
    fun saves_min_time_when_changed() {
        extension.use {
            setContent { SettingsScreen(settingsViewModel =  viewModel, navigateBack = {}) }

            val formattedTime = formatter.format(1_000)

            waitUntilExactlyOneExists(hasText(formattedTime))
            onNodeWithText(formattedTime).performClick()

            waitUntilExactlyOneExists(hasTestTag(NUMBER_DIALOG_KEY_DELETE))

            onNodeWithTag(NUMBER_DIALOG_KEY_DELETE).performClick()
            onNodeWithTag(DIALOG_ACCEPT).performClick()
        }

        verify { viewModel.onAction(SettingsViewModel.UiAction.OnLocationMinTimeChange(100)) }
    }
}