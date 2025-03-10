package org.giste.navigator.ui

import android.util.Log
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
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
import kotlinx.coroutines.launch
import org.giste.navigator.features.location.domain.Location
import org.giste.navigator.features.map.domain.MapSource
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
            mapSource = listOf(),
            roadbook = Roadbook.NotLoaded,
            settings = Settings(),
            trip = Trip(),
            onEvent = {},
            navigateToSettings = {}
        )
    }
}

@Composable
fun NavigatorScreen(
    viewModel: NavigatorViewModel = viewModel(),
    navigateToSettings: () -> Unit,
) {
    NavigatorContent(
        location = viewModel.locationState.collectAsStateWithLifecycle().value,
        mapSource = viewModel.mapSourceState.collectAsStateWithLifecycle().value,
        roadbook = viewModel.roadbookState.collectAsStateWithLifecycle().value,
        settings = viewModel.settingsState.collectAsStateWithLifecycle().value,
        trip = viewModel.tripState.collectAsStateWithLifecycle().value,
        onEvent = viewModel::onAction,
        navigateToSettings = navigateToSettings,
    )
}

@Composable
fun NavigatorContent(
    location: Location?,
    mapSource: List<MapSource>,
    roadbook: Roadbook,
    settings: Settings,
    trip: Trip,
    onEvent: (NavigatorViewModel.UiAction) -> Unit,
    navigateToSettings: () -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val focusRequester = remember { FocusRequester() }
    var scrollState = rememberLazyListState()
    val pixelsToScroll = 317

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    LaunchedEffect(roadbook) {
        if (roadbook is Roadbook.Loaded) {
            scrollState.scrollToItem(
                index = roadbook.initialScroll.pageIndex,
                scrollOffset = roadbook.initialScroll.pageOffset
            )
        }
    }

    Surface(
        modifier = Modifier
            .testTag(NAVIGATION_CONTENT)
            .focusable()
            .focusRequester(focusRequester)
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

                        NativeKeyEvent.KEYCODE_DPAD_UP -> {
                            coroutineScope.launch {
                                scrollState.animateScrollBy(pixelsToScroll.toFloat())
                            }
                            return@onKeyEvent true
                        }

                        NativeKeyEvent.KEYCODE_DPAD_DOWN -> {
                            coroutineScope.launch {
                                scrollState.animateScrollBy(-pixelsToScroll.toFloat())
                            }
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
    ) {
        NavigatorLandscapeScreen(
            locationState = location,
            mapSourceState = mapSource,
            roadbookState = roadbook,
            settings = settings,
            trip = trip,
            roadbookScrollState = scrollState,
            onEvent = onEvent,
            navigateToSettings = navigateToSettings,
        )
    }
}
