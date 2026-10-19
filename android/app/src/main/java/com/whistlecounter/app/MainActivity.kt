package com.whistlecounter.app

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import com.whistlecounter.app.data.WhistlePrefs
import com.whistlecounter.app.ui.WhistleApp
import com.whistlecounter.app.ui.theme.WhistleTheme

class MainActivity : ComponentActivity() {

    companion object {
        const val EXTRA_SHOW_ALARM = "com.whistlecounter.app.extra.SHOW_ALARM"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
        )
        WindowCompat.setDecorFitsSystemWindows(window, false)

        WhistlePrefs.load(this)
        handleAlarmIntent(intent)

        setContent {
            WhistleTheme {
                WhistleApp()
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleAlarmIntent(intent)
    }

    override fun onStop() {
        super.onStop()
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    private fun handleAlarmIntent(intent: Intent?) {
        if (intent?.getBooleanExtra(EXTRA_SHOW_ALARM, false) != true) return

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            @Suppress("DEPRECATION")
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD,
            )
        }

        runCatching {
            val keyguard = getSystemService(android.app.KeyguardManager::class.java)
            keyguard?.requestDismissKeyguard(this, null)
        }
    }
}
