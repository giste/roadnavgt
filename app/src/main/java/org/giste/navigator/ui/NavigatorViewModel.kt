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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.giste.navigator.features.location.domain.Location
import org.giste.navigator.features.location.domain.LocationRepository
import org.giste.navigator.features.map.domain.MapSource
import org.giste.navigator.features.map.domain.MapRepository
import org.giste.navigator.features.roadbook.domain.Roadbook
import org.giste.navigator.features.roadbook.domain.RoadbookRepository
import org.giste.navigator.features.roadbook.domain.Scroll
import org.giste.navigator.features.settings.domain.Settings
import org.giste.navigator.features.settings.domain.SettingsRepository
import org.giste.navigator.features.trip.domain.Trip
import org.giste.navigator.features.trip.domain.TripRepository
import javax.inject.Inject

@HiltViewModel
class NavigatorViewModel @Inject constructor(
    locationRepository: LocationRepository,
    private val mapRepository: MapRepository,
    private val roadbookRepository: RoadbookRepository,
    private val settingsRepository: SettingsRepository,
    private val tripRepository: TripRepository,
) : ViewModel() {
    val locationState: StateFlow<Location?> = locationRepository.getLocations()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null,
        )

    private val maps: MutableStateFlow<List<MapSource>> = MutableStateFlow(listOf())
    val mapSourceState: StateFlow<List<MapSource>> = maps.asStateFlow()

    val roadbookState: StateFlow<Roadbook> = roadbookRepository.getRoadbook()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = Roadbook.NotLoaded,
        )

    val settingsState: StateFlow<Settings> = settingsRepository.getSettings()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = Settings(),
        )

    val tripState: StateFlow<Trip> = tripRepository.getTrips()
        .onStart {
            viewModelScope.launch {
                maps.update { mapRepository.getMaps() }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = Trip(),
        )

    fun onAction(action: UiAction) {
        when (action) {
            is UiAction.SetUri -> loadRoadbook(action.uri)
            is UiAction.SaveScroll -> saveScroll(Scroll(action.pageIndex, action.pageOffset))
            is UiAction.SaveSettings -> saveSettings(action.settings)
            is UiAction.IncrementPartial -> incrementPartial()
            is UiAction.DecrementPartial -> decrementPartial()
            is UiAction.ResetPartial -> resetPartial()
            is UiAction.ResetTrip -> resetTrip()
            is UiAction.SetPartial -> setPartial(action.partial)
        }
    }

    private fun loadRoadbook(uri: String) = viewModelScope.launch {
        roadbookRepository.loadRoadbook(uri)
    }

    private fun saveScroll(scroll: Scroll) = viewModelScope.launch {
        roadbookRepository.saveScroll(scroll)
    }

    private fun saveSettings(settings: Settings) = viewModelScope.launch {
        settingsRepository.saveSettings(settings)
    }

    private fun incrementPartial() = viewModelScope.launch {
        tripRepository.incrementPartial()
    }

    private fun decrementPartial() = viewModelScope.launch { tripRepository.decrementPartial() }

    private fun resetPartial() = viewModelScope.launch { tripRepository.resetPartial() }

    private fun resetTrip() = viewModelScope.launch { tripRepository.resetTrip() }

    private fun setPartial(partial: Int) {
        if (partial in 0..999_990) {
            viewModelScope.launch { tripRepository.setPartial(partial * 10) }
        } else {
            throw IllegalArgumentException(
                "Partial must represent a number between 0 and ${"%,.2f".format(999.99f)}"
            )
        }
    }

    sealed class UiAction {
        data class SetUri(val uri: String) : UiAction()
        data class SaveScroll(val pageIndex: Int, val pageOffset: Int) : UiAction()
        data object IncrementPartial : UiAction()
        data object DecrementPartial : UiAction()
        data object ResetPartial : UiAction()
        data object ResetTrip : UiAction()
        data class SetPartial(val partial: Int) : UiAction()
        data class SaveSettings(val settings: Settings) : UiAction()
    }
}