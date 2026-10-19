package com.whistlecounter.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.outlined.BatterySaver
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material.icons.outlined.PhoneAndroid
import androidx.compose.material.icons.outlined.VerifiedUser
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.whistlecounter.app.ui.theme.AlarmRed
import com.whistlecounter.app.ui.theme.AlarmSoft
import com.whistlecounter.app.ui.theme.Border
import com.whistlecounter.app.ui.theme.Ink
import com.whistlecounter.app.ui.theme.Muted
import com.whistlecounter.app.ui.theme.Sage
import com.whistlecounter.app.ui.theme.SageDeep
import com.whistlecounter.app.ui.theme.SageSoft
import com.whistlecounter.app.ui.theme.Terracotta
import com.whistlecounter.app.util.BackgroundPermissions

@Composable
fun ReliabilityCard(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var batteryOk by remember { mutableStateOf(BackgroundPermissions.isBatteryOptimizationIgnored(context)) }
    var fullScreenOk by remember { mutableStateOf(BackgroundPermissions.canUseFullScreenIntent(context)) }
    var notificationsOk by remember { mutableStateOf(BackgroundPermissions.notificationsEnabled(context)) }

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        batteryOk = BackgroundPermissions.isBatteryOptimizationIgnored(context)
        fullScreenOk = BackgroundPermissions.canUseFullScreenIntent(context)
        notificationsOk = BackgroundPermissions.notificationsEnabled(context)
    }

    val allReady = batteryOk && fullScreenOk && notificationsOk

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = SageSoft,
    ) {
        Column(Modifier.padding(horizontal = 17.dp, vertical = 17.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .background(Color.White.copy(alpha = 0.55f), CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.BatterySaver,
                        contentDescription = null,
                        tint = SageDeep,
                        modifier = Modifier.size(20.dp),
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Screen-off listening",
                        style = MaterialTheme.typography.titleSmall,
                        color = Ink,
                    )
                    Spacer(Modifier.height(3.dp))
                    Text(
                        text = "Lock your phone or switch apps — the counter keeps listening in the background.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Sage,
                    )
                }
            }

            Spacer(Modifier.height(13.dp))

            if (allReady) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Icon(
                        imageVector = Icons.Outlined.CheckCircle,
                        contentDescription = null,
                        tint = SageDeep,
                        modifier = Modifier.size(17.dp),
                    )
                    Text(
                        text = "Background listening is ready",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                        color = SageDeep,
                    )
                }
            } else {
                if (!batteryOk) {
                    RequirementRow(
                        icon = Icons.Outlined.PhoneAndroid,
                        label = "Allow background activity",
                        action = "Allow",
                        onAction = { BackgroundPermissions.openBatteryOptimizationSettings(context) },
                    )
                }
                if (!notificationsOk) {
                    RequirementRow(
                        icon = Icons.Outlined.NotificationsActive,
                        label = "Turn on notifications",
                        action = "Open",
                        onAction = { BackgroundPermissions.openNotificationSettings(context) },
                    )
                }
                if (!fullScreenOk) {
                    RequirementRow(
                        icon = Icons.Outlined.VerifiedUser,
                        label = "Allow alarm on lock screen",
                        action = "Allow",
                        onAction = { BackgroundPermissions.openFullScreenIntentSettings(context) },
                    )
                }
            }

            Spacer(Modifier.height(10.dp))
            Text(
                text = "Android shows a small ongoing notification while the microphone is active. Some phone brands add extra battery limits; if listening stops, allow Whistle Counter in your phone's battery settings.",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                color = Sage.copy(alpha = 0.9f),
            )
        }
    }
}

@Composable
private fun RequirementRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    action: String,
    onAction: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = SageDeep,
            modifier = Modifier.size(16.dp),
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = SageDeep,
            modifier = Modifier.weight(1f),
        )
        TextButton(
            onClick = onAction,
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 0.dp),
            modifier = Modifier.height(30.dp),
        ) {
            Text(
                text = action,
                style = MaterialTheme.typography.labelMedium,
                color = Terracotta,
            )
        }
    }
}

@Composable
fun ErrorCard(
    message: String,
    onDismiss: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = AlarmSoft,
        border = BorderStroke(1.dp, AlarmRed.copy(alpha = 0.18f)),
    ) {
        Column(Modifier.padding(horizontal = 15.dp, vertical = 13.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                Icon(
                    imageVector = Icons.Outlined.ErrorOutline,
                    contentDescription = null,
                    tint = AlarmRed,
                    modifier = Modifier
                        .padding(top = 1.dp)
                        .size(18.dp),
                )
                Spacer(Modifier.width(9.dp))
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodySmall,
                    color = AlarmRed,
                    modifier = Modifier.weight(1f),
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ) {
                TextButton(onClick = onOpenSettings) {
                    Text("Open settings", style = MaterialTheme.typography.labelMedium, color = Terracotta)
                }
                TextButton(onClick = onDismiss) {
                    Text("Dismiss", style = MaterialTheme.typography.labelMedium, color = Muted)
                }
            }
        }
    }
}
