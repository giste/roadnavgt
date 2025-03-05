package org.giste.navigator.ui

import android.util.Log
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.NativeKeyEvent
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.nativeKeyCode
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import org.giste.navigator.features.location.domain.Location
import org.giste.navigator.features.map.domain.Map
import org.giste.navigator.features.roadbook.domain.Roadbook
import org.giste.navigator.features.settings.domain.Settings
import org.giste.navigator.features.trip.domain.Trip
import org.giste.navigator.ui.theme.NavigatorTheme

const val NAVIGATION_CONTENT = "NAVIGATION_CONTENT"

@Preview(
    showBackground = true,
    device = "spec:width=1200px,height=1920px,dpi=360, isRound=false, orientation=landscape"
)
@Composable
fun NavigatorPreview() {
    NavigatorTheme {
        NavigatorContent(
            location = null,
            mapState = listOf(),
            roadbookState = Roadbook.NotLoaded,
            settings = Settings(),
            trip = Trip(),
            onEvent = {},
        )
    }
}

@Composable
fun NavigatorScreen(
    viewModel: NavigatorViewModel = viewModel()
) {
    NavigatorContent(
        location = viewModel.locationState.collectAsStateWithLifecycle().value,
        mapState = viewModel.mapState.collectAsStateWithLifecycle().value,
        roadbookState = viewModel.roadbookState.collectAsStateWithLifecycle().value,
        settings = viewModel.settingsState.collectAsStateWithLifecycle().value,
        trip = viewModel.tripState.collectAsStateWithLifecycle().value,
        onEvent = viewModel::onAction,
    )
}

@Composable
fun NavigatorContent(
    location: Location?,
    mapState: List<Map>,
    roadbookState: Roadbook,
    settings: Settings,
    trip: Trip,
    onEvent: (NavigatorViewModel.UiAction) -> Unit,
) {
    Scaffold(
        modifier = Modifier
            .testTag(NAVIGATION_CONTENT)
            .fillMaxSize()
            .onKeyEvent {
                if (it.type == KeyEventType.KeyUp) {
                    when (it.key.nativeKeyCode) {
                        NativeKeyEvent.KEYCODE_DPAD_RIGHT -> {
                            onEvent(NavigatorViewModel.UiAction.IncrementPartial)
                            return@onKeyEvent true
                        }

                        NativeKeyEvent.KEYCODE_DPAD_LEFT -> {
                            onEvent(NavigatorViewModel.UiAction.DecrementPartial)
                            return@onKeyEvent true
                        }

                        NativeKeyEvent.KEYCODE_F6 -> {
                            onEvent(NavigatorViewModel.UiAction.ResetPartial)
                            return@onKeyEvent true
                        }

                        else -> {
                            Log.d(
                                "NavigationContent",
                                "KeyEvent( ${it.key.nativeKeyCode}) up not processed"
                            )
                            return@onKeyEvent false
                        }
                    }
                } else {
                    Log.d(
                        "NavigationContent",
                        "KeyEvent(${it.type}, ${it.key.nativeKeyCode}) not processed"
                    )
                    false
                }
            },
    ) { innerPadding ->
        NavigatorLandscapeScreen(
            locationState = location,
            mapState = mapState,
            roadbookState = roadbookState,
            settings = settings,
            trip = trip,
            onEvent = onEvent,
            modifier = Modifier
                .padding(innerPadding)
                // Consume this insets so that it's not applied again
                // when using safeDrawing in the hierarchy below
                .consumeWindowInsets(innerPadding)
        )
    }
}
