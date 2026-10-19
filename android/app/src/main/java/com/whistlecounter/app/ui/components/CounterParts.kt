package com.whistlecounter.app.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.NotificationsOff
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.MicOff
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material.icons.rounded.RestartAlt
import androidx.compose.material.icons.rounded.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.whistlecounter.app.ui.theme.AlarmRed
import com.whistlecounter.app.ui.theme.Border
import com.whistlecounter.app.ui.theme.CardCream
import com.whistlecounter.app.ui.theme.Ink
import com.whistlecounter.app.ui.theme.InkStrong
import com.whistlecounter.app.ui.theme.Muted
import com.whistlecounter.app.ui.theme.MutedSoft
import com.whistlecounter.app.ui.theme.Sage
import com.whistlecounter.app.ui.theme.Terracotta
import com.whistlecounter.app.ui.theme.TerracottaLight
import com.whistlecounter.app.ui.theme.TerracottaPale
import com.whistlecounter.app.ui.theme.WarmWhite
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.roundToInt
import kotlin.math.sin

@Composable
fun CounterDial(
    count: Int,
    limit: Int,
    isActive: Boolean,
    isWhistleDetected: Boolean,
    isAlarmTriggered: Boolean,
    modifier: Modifier = Modifier,
) {
    val pulseScale by animateFloatAsState(
        targetValue = if (isWhistleDetected) 1.045f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow,
        ),
        label = "dialScale",
    )

    Box(
        modifier = modifier.size(250.dp),
        contentAlignment = Alignment.Center,
    ) {
        if (isActive) {
            PulseRings()
        }

        Box(
            modifier = Modifier
                .size(232.dp)
                .graphicsLayer {
                    scaleX = pulseScale
                    scaleY = pulseScale
                }
                .shadow(
                    elevation = 16.dp,
                    shape = CircleShape,
                    ambientColor = Terracotta.copy(alpha = 0.16f),
                    spotColor = Terracotta.copy(alpha = 0.2f),
                )
                .background(
                    brush = Brush.radialGradient(
                        listOf(WarmWhite, CardCream),
                    ),
                    shape = CircleShape,
                )
                .border(
                    width = if (isAlarmTriggered) 2.dp else 1.dp,
                    color = if (isAlarmTriggered) AlarmRed.copy(alpha = 0.55f) else Border,
                    shape = CircleShape,
                )
                .semantics {
                    contentDescription = "$count whistles counted, target $limit"
                    liveRegion = LiveRegionMode.Polite
                },
            contentAlignment = Alignment.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = count.toString(),
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontSize = 90.sp,
                        lineHeight = 92.sp,
                        fontWeight = FontWeight.Light,
                        letterSpacing = (-4).sp,
                        brush = if (isAlarmTriggered) {
                            SolidColor(AlarmRed)
                        } else {
                            Brush.linearGradient(listOf(Terracotta, TerracottaLight))
                        },
                    ),
                )
                Text(
                    text = if (count == 1) "WHISTLE" else "WHISTLES",
                    style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 2.4.sp),
                    color = MutedSoft,
                    modifier = Modifier.padding(top = 2.dp),
                )
                Row(
                    modifier = Modifier.padding(top = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Target ",
                        style = MaterialTheme.typography.bodySmall,
                        color = MutedSoft,
                    )
                    Text(
                        text = limit.toString(),
                        style = MaterialTheme.typography.titleSmall,
                        color = if (count >= limit) Terracotta else Muted,
                    )
                }
            }
        }
    }
}

@Composable
private fun PulseRings() {
    val transition = rememberInfiniteTransition(label = "pulse")
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "pulsePhase",
    )
    val secondPhase = (phase + 0.5f) % 1f

    Canvas(Modifier.size(250.dp)) {
        val minSide = size.minDimension
        listOf(phase, secondPhase).forEach { p ->
            val radius = minSide * (0.42f + 0.09f * p)
            val alpha = (1f - p) * 0.16f
            drawCircle(
                color = Terracotta.copy(alpha = alpha),
                radius = radius,
                style = Stroke(width = 2.dp.toPx()),
            )
        }
    }
}

@Composable
fun StatusPill(
    isActive: Boolean,
    isAlarmTriggered: Boolean,
    modifier: Modifier = Modifier,
) {
    val text = when {
        isAlarmTriggered -> "Target reached!"
        isActive -> "Listening..."
        else -> "Ready"
    }
    val dotColor = when {
        isAlarmTriggered -> AlarmRed
        isActive -> Terracotta
        else -> MutedSoft.copy(alpha = 0.4f)
    }
    val bg = when {
        isAlarmTriggered -> AlarmRed.copy(alpha = 0.1f)
        isActive -> TerracottaPale
        else -> CardCream
    }

    Surface(
        modifier = modifier,
        shape = CircleShape,
        color = bg,
        border = BorderStroke(1.dp, if (isActive || isAlarmTriggered) Color.Transparent else Border),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 15.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Box(
                Modifier
                    .size(7.dp)
                    .background(dotColor, CircleShape),
            )
            Text(
                text = text,
                style = MaterialTheme.typography.bodySmall,
                color = if (isAlarmTriggered) AlarmRed else Muted,
            )
        }
    }
}

@Composable
fun AudioMonitor(
    amplitude: Float,
    frequency: Int?,
    isActive: Boolean,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = CardCream,
        border = BorderStroke(1.dp, Border),
    ) {
        Column(Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "AUDIO MONITOR",
                    style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.3.sp),
                    color = MutedSoft,
                )
                Spacer(Modifier.weight(1f))
                Text(
                    text = if (isActive && frequency != null) "$frequency Hz" else "—",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                    ),
                    color = if (isActive) Terracotta else MutedSoft,
                )
            }
            Spacer(Modifier.height(10.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                horizontalArrangement = Arrangement.spacedBy(3.dp),
                verticalAlignment = Alignment.Bottom,
            ) {
                repeat(20) { index ->
                    val centerDistance = abs(index - 10) / 10f
                    val multiplier = 1f - centerDistance * 0.6f
                    val variation = 0.85f + sin(index * 1.7f) * 0.12f
                    val heightFraction = if (isActive) {
                        max(0.08f, amplitude * multiplier * variation)
                    } else {
                        0.13f
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(max(4f, heightFraction * 54f).dp)
                            .clip(CircleShape)
                            .background(
                                if (isActive) {
                                    Terracotta.copy(alpha = 0.45f + heightFraction * 0.55f)
                                } else {
                                    MutedSoft.copy(alpha = 0.18f)
                                },
                            ),
                    )
                }
            }

            Spacer(Modifier.height(10.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = "LEVEL",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                    color = MutedSoft,
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(5.dp)
                        .clip(CircleShape)
                        .background(Border),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(if (isActive) amplitude.coerceIn(0f, 1f) else 0f)
                            .height(5.dp)
                            .clip(CircleShape)
                            .background(if (amplitude > 0.5f) Terracotta else Terracotta.copy(alpha = 0.55f)),
                    )
                }
                Text(
                    text = "${(if (isActive) amplitude else 0f).times(100).roundToInt()}%",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 11.sp,
                        fontFeatureSettings = "tnum",
                    ),
                    color = MutedSoft,
                    textAlign = TextAlign.End,
                    modifier = Modifier.width(34.dp),
                )
            }
        }
    }
}

@Composable
fun ControlPanel(
    isListening: Boolean,
    whistleLimit: Int,
    isAlarmEnabled: Boolean,
    modifier: Modifier = Modifier,
    onStartListening: () -> Unit,
    onStopListening: () -> Unit,
    onReset: () -> Unit,
    onLimitChange: (Int) -> Unit,
    onAlarmToggle: (Boolean) -> Unit,
    audioContent: @Composable () -> Unit = {},
) {
    val haptic = LocalHapticFeedback.current

    Column(modifier = modifier.fillMaxWidth()) {
        Button(
            onClick = if (isListening) onStopListening else onStartListening,
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isListening) InkStrong else Terracotta,
                contentColor = WarmWhite,
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 2.dp,
                pressedElevation = 0.dp,
            ),
        ) {
            Icon(
                imageVector = if (isListening) Icons.Rounded.MicOff else Icons.Rounded.Mic,
                contentDescription = null,
                modifier = Modifier.size(21.dp),
            )
            Spacer(Modifier.width(10.dp))
            Text(
                text = if (isListening) "Stop listening" else "Start listening",
                style = MaterialTheme.typography.titleMedium,
            )
        }

        Spacer(Modifier.height(14.dp))

        audioContent()

        Spacer(Modifier.height(14.dp))

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = CardCream,
            border = BorderStroke(1.dp, Border),
        ) {
            Column(Modifier.padding(horizontal = 17.dp, vertical = 16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(
                            text = "Whistle limit",
                            style = MaterialTheme.typography.titleSmall,
                            color = Ink,
                        )
                        Text(
                            text = "Alert when reached",
                            style = MaterialTheme.typography.bodySmall,
                            color = MutedSoft,
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        StepperButton(
                            icon = Icons.Rounded.Remove,
                            description = "Decrease whistle target",
                            enabled = whistleLimit > 1,
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onLimitChange(whistleLimit - 1)
                            },
                        )
                        Text(
                            text = whistleLimit.toString(),
                            style = MaterialTheme.typography.titleLarge.copy(fontSize = 21.sp),
                            color = Ink,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.width(28.dp),
                        )
                        StepperButton(
                            icon = Icons.Rounded.Add,
                            description = "Increase whistle target",
                            enabled = whistleLimit < 20,
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onLimitChange(whistleLimit + 1)
                            },
                        )
                    }
                }

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 15.dp),
                    color = Border.copy(alpha = 0.8f),
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .background(if (isAlarmEnabled) TerracottaPale else Border.copy(alpha = 0.5f), CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = if (isAlarmEnabled) Icons.Rounded.Notifications else Icons.Rounded.NotificationsOff,
                            contentDescription = null,
                            tint = if (isAlarmEnabled) Terracotta else MutedSoft,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                    Spacer(Modifier.width(11.dp))
                    Column(Modifier.weight(1f)) {
                        Text(
                            text = "Alarm sound",
                            style = MaterialTheme.typography.titleSmall,
                            color = Ink,
                        )
                        Text(
                            text = "Play when limit is reached",
                            style = MaterialTheme.typography.bodySmall,
                            color = MutedSoft,
                        )
                    }
                    Switch(
                        checked = isAlarmEnabled,
                        onCheckedChange = onAlarmToggle,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = WarmWhite,
                            checkedTrackColor = Terracotta,
                            checkedBorderColor = Terracotta,
                            uncheckedThumbColor = WarmWhite,
                            uncheckedTrackColor = Border,
                            uncheckedBorderColor = Border,
                        ),
                    )
                }

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = Border.copy(alpha = 0.8f),
                )

                TextButton(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onReset()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.RestartAlt,
                        contentDescription = null,
                        tint = Muted,
                        modifier = Modifier.size(17.dp),
                    )
                    Spacer(Modifier.width(7.dp))
                    Text(
                        text = "Reset counter",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Muted,
                    )
                }
            }
        }
    }
}

@Composable
private fun StepperButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    description: String,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(38.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(WarmWhite)
            .border(
                width = 1.dp,
                color = if (enabled) Border else Border.copy(alpha = 0.5f),
                shape = RoundedCornerShape(12.dp),
            )
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick)
            .semantics { contentDescription = description },
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (enabled) Ink else MutedSoft.copy(alpha = 0.4f),
            modifier = Modifier.size(18.dp),
        )
    }
}

@Composable
fun PrivacyNote(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Icon(
            imageVector = Icons.Rounded.VolumeUp,
            contentDescription = null,
            tint = Sage,
            modifier = Modifier
                .padding(top = 1.dp)
                .size(15.dp),
        )
        Text(
            text = "Audio is analysed on this phone only. Nothing is recorded or uploaded.",
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
            color = MutedSoft,
        )
    }
}
