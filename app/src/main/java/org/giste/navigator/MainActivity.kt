package org.giste.navigator

import android.Manifest
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import dagger.hilt.android.AndroidEntryPoint
import org.giste.navigator.ui.ManagePermissions
import org.giste.navigator.ui.Navigator
import org.giste.navigator.ui.theme.NavigatorTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // Set landscape orientation for now
            this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

            val windowSize = currentWindowAdaptiveInfo().windowSizeClass
            NavigatorTheme(windowSize) {
                val multiplePermission = rememberMultiplePermissionsState(
                    permissions = listOf(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                    )
                )

                ManagePermissions(multiplePermission)

                if (multiplePermission.allPermissionsGranted) {
                    Navigator()
                }
            }
        }

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }
}

