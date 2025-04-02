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

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.giste.navigator.features.map.domain.NewMapRepository
import org.giste.navigator.features.map.domain.NewMapSource
import org.giste.navigator.ui.MapManagerViewModel.UiAction.OnDelete
import org.giste.navigator.ui.MapManagerViewModel.UiAction.OnDownload
import javax.inject.Inject

private const val TAG = "MapManagerViewModel"

@HiltViewModel
class MapManagerViewModel @Inject constructor(
    private val mapRepository: NewMapRepository,
) : ViewModel() {
    val mapsState: StateFlow<List<NewMapSource>> = mapRepository.getMaps()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList(),
        )

    fun onAction(uiAction: UiAction) {
        when (uiAction) {
            is OnDownload -> download(uiAction.map)
            is OnDelete -> delete(uiAction.map)
        }
    }

    private fun download(map: NewMapSource) {
        viewModelScope.launch { mapRepository.downloadMap(map).collect { Log.d(TAG, it.toString()) } }
    }

    private fun delete(map: NewMapSource) {
        viewModelScope.launch { mapRepository.removeMap(map) }
    }

    sealed class UiAction {
        data class OnDownload(val map: NewMapSource) : UiAction()
        data class OnDelete(val map: NewMapSource) : UiAction()
    }
}