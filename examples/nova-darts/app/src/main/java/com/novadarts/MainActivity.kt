package com.novadarts

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.WindowCompat
import com.novadarts.ui.NovaDartsApp
import com.novadarts.ui.NovaDartsTheme
import com.novadarts.ui.NovaPalette

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = NovaPalette.ink.toArgb()
        window.navigationBarColor = NovaPalette.ink.toArgb()

        setContent {
            NovaDartsTheme {
                NovaDartsApp()
            }
        }
    }
}
