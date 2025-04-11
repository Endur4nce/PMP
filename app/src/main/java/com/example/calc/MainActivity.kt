package com.example.calc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        if (!checkScreenRequirements()) {
            showUnsupportedScreenDialog()
            return
        }
        val calculator = Calculator()
        setContent {
            var showCalculator by rememberSaveable { mutableStateOf(false) }
            if (showCalculator) {
                calculator.CalculatorScreen(onBackClick = { showCalculator = false } )
            } else {
                MainScreen(
                    onCalculatorClick = { showCalculator = true },
                    onExitClick = { finish() }
                )
            }
        }
    }

    private fun checkScreenRequirements(): Boolean {
        val metrics = resources.displayMetrics
        val width = metrics.widthPixels / metrics.density
        val height = metrics.heightPixels / metrics.density
        val screenSize = minOf(width, height)
        return screenSize >= 300f
    }

    private fun showUnsupportedScreenDialog() {
        // Здесь можно показать диалог о неподдерживаемом устройстве
    }
}