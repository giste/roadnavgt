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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.giste.navigator.features.settings.domain.Settings
import org.giste.navigator.features.settings.domain.SettingsRepository
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
) : ViewModel() {
    val currentSettings = runBlocking { settingsRepository.getSettings().first() }
    val settingsState: StateFlow<Settings> = settingsRepository.getSettings()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = currentSettings,
        )

    fun onAction(uiAction: UiAction) = when(uiAction) {
        is UiAction.OnMapZoomLevelChange -> saveMapZoomLevel(uiAction.mapZoomLevel)
        is UiAction.OnPixelsToMoveRoadbookChange -> savePixelsToMoveRoadbook(uiAction.pixelsToMove)
        is UiAction.OnLocationMinTimeChange -> saveLocationMinTime(uiAction.locationMinTime)
        is UiAction.OnLocationMinDistanceChange ->
            saveLocationMinDistance(uiAction.locationMinDistance)
    }

    private fun saveMapZoomLevel(mapZoomLevel: Int) {
        viewModelScope.launch {
            settingsRepository.saveSettings(settingsState.value.copy(mapZoomLevel = mapZoomLevel))
        }
    }

    private fun savePixelsToMoveRoadbook(pixelsToMove: Int) {
        viewModelScope.launch {
            settingsRepository.saveSettings(settingsState.value.copy(pixelsToMoveRoadbook = pixelsToMove))
        }
    }

    private fun saveLocationMinTime(locationMinTime: Long) {
        viewModelScope.launch {
            settingsRepository.saveSettings(settingsState.value.copy(millisecondsBetweenLocations = locationMinTime))
        }
    }

    private fun saveLocationMinDistance(locationMinDistance: Int) {
        viewModelScope.launch {
            settingsRepository.saveSettings(
                settingsState.value.copy(metersBetweenLocations = locationMinDistance)
            )
        }
    }

    sealed class UiAction {
        data class OnMapZoomLevelChange(val mapZoomLevel: Int) : UiAction()
        data class OnPixelsToMoveRoadbookChange(val pixelsToMove: Int) : UiAction()
        data class OnLocationMinTimeChange(val locationMinTime: Long) : UiAction()
        data class OnLocationMinDistanceChange(val locationMinDistance: Int) : UiAction()
    }
}