package com.whistlecounter.app.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PhoneAndroid
import androidx.compose.material.icons.outlined.VerifiedUser
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.whistlecounter.app.data.CounterRepository
import com.whistlecounter.app.data.CounterUiState
import com.whistlecounter.app.service.CounterController
import com.whistlecounter.app.ui.components.AudioMonitor
import com.whistlecounter.app.ui.components.BrandHeader
import com.whistlecounter.app.ui.components.ControlPanel
import com.whistlecounter.app.ui.components.CounterDial
import com.whistlecounter.app.ui.components.ErrorCard
import com.whistlecounter.app.ui.components.Eyebrow
import com.whistlecounter.app.ui.components.PrivacyNote
import com.whistlecounter.app.ui.components.ProofChip
import com.whistlecounter.app.ui.components.ReliabilityCard
import com.whistlecounter.app.ui.components.SafetyNote
import com.whistlecounter.app.ui.components.SectionHeading
import com.whistlecounter.app.ui.components.SiteFooter
import com.whistlecounter.app.ui.components.StatusPill
import com.whistlecounter.app.ui.components.StepCard
import com.whistlecounter.app.ui.theme.Border
import com.whistlecounter.app.ui.theme.Cream
import com.whistlecounter.app.ui.theme.InkStrong
import com.whistlecounter.app.ui.theme.Muted
import com.whistlecounter.app.ui.theme.Terracotta
import com.whistlecounter.app.ui.theme.TerracottaPale
import com.whistlecounter.app.util.BackgroundPermissions

@Composable
fun CounterScreen(state: CounterUiState) {
    val context = LocalContext.current
    var micGranted by remember { mutableStateOf(hasMicrophonePermission(context)) }

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        micGranted = hasMicrophonePermission(context)
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
    ) {
        val granted = hasMicrophonePermission(context)
        micGranted = granted
        if (granted) {
            CounterController.start(context)
        } else {
            CounterRepository.update {
                it.copy(
                    error = "Microphone permission is required to count whistles. Tap Open settings and allow Microphone.",
                )
            }
        }
    }

    val onStartListening: () -> Unit = {
        val missing = buildList {
            if (!hasMicrophonePermission(context)) {
                add(Manifest.permission.RECORD_AUDIO)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                add(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
        if (missing.isEmpty()) {
            CounterController.start(context)
        } else {
            permissionLauncher.launch(missing.toTypedArray())
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        BrandHeader(
            trailing = {
                if (state.isListening) LiveChip()
            },
        )

        Column(
            modifier = Modifier.padding(horizontal = 22.dp),
        ) {
            Spacer(Modifier.height(6.dp))
            Eyebrow("Your kitchen sidekick")
            Spacer(Modifier.height(12.dp))
            Text(
                text = "Keep cooking.",
                style = MaterialTheme.typography.displayLarge,
                color = InkStrong,
            )
            Text(
                text = "We'll count.",
                style = MaterialTheme.typography.displayLarge,
                color = Terracotta,
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = "A little help keeping track of pressure cooker whistles, so you can get on with everything else happening in the kitchen.",
                style = MaterialTheme.typography.bodyLarge,
                color = Muted,
            )
            Spacer(Modifier.height(14.dp))
            Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
                ProofChip(Icons.Outlined.VerifiedUser, "Audio stays on this device")
                ProofChip(Icons.Outlined.PhoneAndroid, "Works with the screen off")
            }
        }

        if (state.error != null) {
            Spacer(Modifier.height(14.dp))
            ErrorCard(
                message = state.error,
                onDismiss = { CounterController.clearError() },
                onOpenSettings = { BackgroundPermissions.openAppDetails(context) },
                modifier = Modifier.padding(horizontal = 18.dp),
            )
        }

        Spacer(Modifier.height(18.dp))

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp),
            shape = RoundedCornerShape(28.dp),
            color = Cream,
            border = BorderStroke(1.dp, Border),
            shadowElevation = 6.dp,
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                CounterDial(
                    count = state.whistleCount,
                    limit = state.whistleLimit,
                    isActive = state.isListening,
                    isWhistleDetected = state.isWhistleDetected,
                    isAlarmTriggered = state.isAlarmTriggered,
                )
                Spacer(Modifier.height(14.dp))
                StatusPill(
                    isActive = state.isListening,
                    isAlarmTriggered = state.isAlarmTriggered,
                )
                Spacer(Modifier.height(18.dp))
                ControlPanel(
                    isListening = state.isListening,
                    whistleLimit = state.whistleLimit,
                    isAlarmEnabled = state.alarmEnabled,
                    onStartListening = onStartListening,
                    onStopListening = { CounterController.stop(context) },
                    onReset = { CounterController.reset(context) },
                    onLimitChange = { CounterController.setLimit(context, it) },
                    onAlarmToggle = { CounterController.setAlarmEnabled(context, it) },
                    audioContent = {
                        AudioMonitor(
                            amplitude = state.currentAmplitude,
                            frequency = state.currentFrequency,
                            isActive = state.isListening,
                        )
                    },
                )
                Spacer(Modifier.height(16.dp))
                PrivacyNote()
            }
        }

        Spacer(Modifier.height(16.dp))

        ReliabilityCard(
            modifier = Modifier.padding(horizontal = 18.dp),
        )

        Column(
            modifier = Modifier.padding(horizontal = 22.dp),
        ) {
            Spacer(Modifier.height(34.dp))
            SectionHeading(
                eyebrow = "Three simple steps",
                title = "More hands for the rest of dinner.",
            )
            Spacer(Modifier.height(8.dp))
            StepCard(
                number = "01",
                title = "Choose your count",
                body = "Set the whistle target that suits your recipe and cooker.",
            )
            StepCard(
                number = "02",
                title = "Start listening",
                body = "Allow microphone access, then lock your phone or get on with other things. Listening continues in the background.",
            )
            StepCard(
                number = "03",
                title = "Get a nudge",
                body = "When your target is reached, your phone rings and vibrates so you can turn off the stove.",
            )

            Spacer(Modifier.height(20.dp))
            SafetyNote(
                title = "A helpful reminder, not a replacement for attention.",
                body = "Stay in the kitchen, follow your pressure cooker's instructions, and use your own judgement. Sound detection can miss a whistle or count another sound by mistake.",
            )

            Spacer(Modifier.height(30.dp))
        }

        SiteFooter()
    }
}

@Composable
private fun LiveChip() {
    val transition = rememberInfiniteTransition(label = "live")
    val alpha by transition.animateFloat(
        initialValue = 0.35f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "liveDot",
    )

    Surface(
        shape = CircleShape,
        color = TerracottaPale,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Box(
                Modifier
                    .size(6.dp)
                    .graphicsLayer { this.alpha = alpha }
                    .background(Terracotta, CircleShape),
            )
            Text(
                text = "LIVE",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 9.sp,
                    letterSpacing = 1.2.sp,
                ),
                color = Terracotta,
                textAlign = TextAlign.Center,
            )
        }
    }
}

private fun hasMicrophonePermission(context: android.content.Context): Boolean {
    return ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) ==
        PackageManager.PERMISSION_GRANTED
}
