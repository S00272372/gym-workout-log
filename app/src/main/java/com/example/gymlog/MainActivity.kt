package com.example.gymlog

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.gymlog.data.ThemeMode
import com.example.gymlog.ui.theme.GymLogTheme
import com.example.gymlog.ui.viewmodel.AppViewModelProvider
import com.example.gymlog.ui.viewmodel.SettingsViewModel

class MainActivity : ComponentActivity() {

    //the theme choice is read here because the theme wraps the whole app
    private val settingsViewModel: SettingsViewModel by viewModels { AppViewModelProvider.Factory }

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            //changes by itself when the device is rotated
            val windowSizeClass = calculateWindowSizeClass(this)
            val themeMode by settingsViewModel.themeMode.collectAsState()
            val darkTheme = when (themeMode) {
                ThemeMode.System -> isSystemInDarkTheme()
                ThemeMode.Light -> false
                ThemeMode.Dark -> true
            }

            //keeps the status bar and navigation bar icons readable when the chosen theme is not the system theme
            DisposableEffect(darkTheme) {
                enableEdgeToEdge(
                    statusBarStyle = SystemBarStyle.auto(
                        Color.TRANSPARENT,
                        Color.TRANSPARENT
                    ) { darkTheme },
                    navigationBarStyle = SystemBarStyle.auto(
                        Color.argb(0xe6, 0xFF, 0xFF, 0xFF),
                        Color.argb(0x80, 0x1b, 0x1b, 0x1b)
                    ) { darkTheme }
                )
                onDispose {}
            }

            GymLogTheme(darkTheme = darkTheme) {
                GymLogApp(windowWidthSizeClass = windowSizeClass.widthSizeClass)
            }
        }
    }
}