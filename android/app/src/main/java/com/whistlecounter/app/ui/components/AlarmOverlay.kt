package com.whistlecounter.app.ui.components

import androidx.activity.compose.BackHandler
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
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.RestartAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.whistlecounter.app.ui.theme.AlarmRed
import com.whistlecounter.app.ui.theme.AlarmSoft
import com.whistlecounter.app.ui.theme.Border
import com.whistlecounter.app.ui.theme.Cream
import com.whistlecounter.app.ui.theme.Ink
import com.whistlecounter.app.ui.theme.Muted
import com.whistlecounter.app.ui.theme.MutedSoft
import com.whistlecounter.app.ui.theme.Terracotta
import com.whistlecounter.app.ui.theme.WarmWhite

@Composable
fun AlarmOverlay(
    count: Int,
    limit: Int,
    alarmEnabled: Boolean,
    onDismiss: () -> Unit,
    onReset: () -> Unit,
) {
    val view = LocalView.current
    DisposableEffect(Unit) {
        view.keepScreenOn = true
        onDispose { view.keepScreenOn = false }
    }

    BackHandler { onDismiss() }

    val transition = rememberInfiniteTransition(label = "alarmPulse")
    val bellScale by transition.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(650, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "bellScale",
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Ink.copy(alpha = 0.62f))
            .padding(horizontal = 22.dp, vertical = 24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            color = Cream,
            shadowElevation = 24.dp,
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 26.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    modifier = Modifier
                        .size(88.dp)
                        .background(AlarmSoft, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Notifications,
                        contentDescription = null,
                        tint = AlarmRed,
                        modifier = Modifier
                            .size(42.dp)
                            .scale(bellScale),
                    )
                }

                Spacer(Modifier.height(20.dp))
                Text(
                    text = "Target reached!",
                    style = MaterialTheme.typography.headlineLarge,
                    color = AlarmRed,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Text(
                        text = count.toString(),
                        style = MaterialTheme.typography.headlineLarge,
                        color = Ink,
                    )
                    Text(
                        text = " of $limit whistles",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Muted,
                        modifier = Modifier.padding(bottom = 4.dp),
                    )
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    text = if (alarmEnabled) {
                        "The alarm is ringing on this phone. Tap below to silence it."
                    } else {
                        "Alarm sound is off, but your target is complete."
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = Muted,
                    textAlign = TextAlign.Center,
                )

                Spacer(Modifier.height(24.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AlarmRed,
                        contentColor = WarmWhite,
                    ),
                ) {
                    Icon(Icons.Rounded.Notifications, contentDescription = null, modifier = Modifier.size(19.dp))
                    Spacer(Modifier.size(9.dp))
                    Text("Dismiss alarm", style = MaterialTheme.typography.titleMedium)
                }

                Spacer(Modifier.height(10.dp))

                OutlinedButton(
                    onClick = onReset,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, Border),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.RestartAlt,
                        contentDescription = null,
                        tint = Muted,
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(Modifier.size(8.dp))
                    Text("Reset counter", color = Muted, style = MaterialTheme.typography.bodyMedium)
                }

                Spacer(Modifier.height(16.dp))
                Text(
                    text = "The alarm stops automatically after 5 minutes.",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = MutedSoft,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}
