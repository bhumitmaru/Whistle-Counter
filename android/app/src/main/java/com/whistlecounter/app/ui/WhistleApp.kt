package com.whistlecounter.app.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.whistlecounter.app.data.CounterRepository
import com.whistlecounter.app.service.CounterController
import com.whistlecounter.app.ui.components.AlarmOverlay
import com.whistlecounter.app.ui.components.AppBottomBar
import com.whistlecounter.app.ui.screens.AboutScreen
import com.whistlecounter.app.ui.screens.CounterScreen
import com.whistlecounter.app.ui.screens.FaqScreen
import com.whistlecounter.app.ui.screens.GuideScreen
import com.whistlecounter.app.ui.theme.Cream

@Composable
fun WhistleApp() {
    val context = LocalContext.current
    val state by CounterRepository.state.collectAsStateWithLifecycle()
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }

    BackHandler(enabled = selectedTab != 0 && !state.isAlarmTriggered) {
        selectedTab = 0
    }

    LaunchedEffect(state.isAlarmTriggered) {
        if (state.isAlarmTriggered) selectedTab = 0
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Cream),
    ) {
        Scaffold(
            containerColor = Cream,
            bottomBar = {
                AppBottomBar(
                    selectedTab = selectedTab,
                    onSelectTab = { selectedTab = it },
                    isListening = state.isListening,
                )
            },
        ) { innerPadding ->
            Crossfade(
                targetState = selectedTab,
                animationSpec = tween(180),
                label = "tabs",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            ) { tab ->
                when (tab) {
                    0 -> CounterScreen(state = state)
                    1 -> GuideScreen(onOpenCounter = { selectedTab = 0 })
                    2 -> FaqScreen(onOpenCounter = { selectedTab = 0 })
                    else -> AboutScreen(onOpenCounter = { selectedTab = 0 })
                }
            }
        }

        if (state.isAlarmTriggered) {
            AlarmOverlay(
                count = state.whistleCount,
                limit = state.whistleLimit,
                alarmEnabled = state.alarmEnabled,
                onDismiss = { CounterController.dismissAlarm(context) },
                onReset = { CounterController.reset(context) },
            )
        }
    }
}
