package com.whistlecounter.app.ui.screens

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
import androidx.compose.material.icons.outlined.BatterySaver
import androidx.compose.material.icons.outlined.GraphicEq
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material.icons.outlined.VerifiedUser
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.whistlecounter.app.ui.components.BrandHeader
import com.whistlecounter.app.ui.components.Eyebrow
import com.whistlecounter.app.ui.components.SiteFooter
import com.whistlecounter.app.ui.theme.Border
import com.whistlecounter.app.ui.theme.CardCream
import com.whistlecounter.app.ui.theme.Ink
import com.whistlecounter.app.ui.theme.InkStrong
import com.whistlecounter.app.ui.theme.Muted
import com.whistlecounter.app.ui.theme.Sage
import com.whistlecounter.app.ui.theme.SageDeep
import com.whistlecounter.app.ui.theme.SageSoft
import com.whistlecounter.app.ui.theme.Terracotta
import com.whistlecounter.app.ui.theme.TerracottaPale
import com.whistlecounter.app.ui.theme.WarmWhite

@Composable
fun GuideScreen(onOpenCounter: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        BrandHeader()

        Column(
            modifier = Modifier.padding(horizontal = 22.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(22.dp))
            Eyebrow("A simple kitchen tool")
            Spacer(Modifier.height(14.dp))
            Text(
                text = "How it works",
                style = MaterialTheme.typography.headlineLarge,
                color = InkStrong,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(10.dp))
            Text(
                text = "Whistle Counter uses your phone's microphone to help you keep track. Here's what happens when you press start.",
                style = MaterialTheme.typography.bodyLarge,
                color = Muted,
                textAlign = TextAlign.Center,
            )
        }

        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Spacer(Modifier.height(22.dp))
            IconStepCard(
                step = "STEP 01",
                icon = Icons.Outlined.Mic,
                title = "You choose when to listen",
                body = "Press Start listening and allow microphone access. The app uses the sound around your phone only while the counter is active.",
            )
            Spacer(Modifier.height(12.dp))
            IconStepCard(
                step = "STEP 02",
                icon = Icons.Outlined.GraphicEq,
                title = "The signal is checked on your phone",
                body = "A lightweight frequency check looks for strong sounds in the 2–4.5 kHz whistle range. Kitchen acoustics and phone microphones vary, so the count is an estimate.",
            )
            Spacer(Modifier.height(12.dp))
            IconStepCard(
                step = "STEP 03",
                icon = Icons.Outlined.NotificationsActive,
                title = "The counter keeps your place",
                body = "A detected whistle increments the count. When your target is reached, listening stops and your phone rings and vibrates.",
            )
        }

        Spacer(Modifier.height(18.dp))

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp),
            shape = RoundedCornerShape(18.dp),
            color = SageSoft,
        ) {
            Row(
                modifier = Modifier.padding(19.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Icon(
                    imageVector = Icons.Outlined.VerifiedUser,
                    contentDescription = null,
                    tint = SageDeep,
                    modifier = Modifier
                        .padding(top = 2.dp)
                        .size(21.dp),
                )
                Column {
                    Text(
                        text = "Your audio stays on your phone",
                        style = MaterialTheme.typography.titleMedium,
                        color = InkStrong,
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "The app analyses microphone input on your device. It does not record or upload audio. Listening ends when you press Stop or when your target is reached.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Sage,
                    )
                }
            }
        }

        Spacer(Modifier.height(18.dp))

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp),
            shape = RoundedCornerShape(18.dp),
            color = CardCream,
            border = BorderStroke(1.dp, Border),
        ) {
            Row(
                modifier = Modifier.padding(19.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .background(TerracottaPale, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.BatterySaver,
                        contentDescription = null,
                        tint = Terracotta,
                        modifier = Modifier.size(20.dp),
                    )
                }
                Column {
                    Text(
                        text = "Listening beyond the screen",
                        style = MaterialTheme.typography.titleMedium,
                        color = InkStrong,
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "The counter runs as an Android foreground service, so it keeps listening when the screen is off or you switch apps. A small ongoing notification stays visible while the microphone is active. For the most reliable results, allow background activity in the reliability card.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Muted,
                    )
                }
            }
        }

        Column(
            modifier = Modifier.padding(horizontal = 22.dp),
        ) {
            Spacer(Modifier.height(28.dp))
            Text(
                text = "A note about accuracy",
                style = MaterialTheme.typography.titleLarge,
                color = InkStrong,
            )
            Spacer(Modifier.height(7.dp))
            Text(
                text = "This is a helpful counter, not a certified cooking timer or safety device. Background noise, distance, room acoustics, and microphone differences can affect detection. Check the counter occasionally, stay nearby, and follow your cooker manufacturer's instructions.",
                style = MaterialTheme.typography.bodyMedium,
                color = Muted,
            )

            Spacer(Modifier.height(22.dp))
            Button(
                onClick = onOpenCounter,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = InkStrong,
                    contentColor = WarmWhite,
                ),
            ) {
                Text("Open the counter", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.size(8.dp))
                Icon(
                    imageVector = Icons.Rounded.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                )
            }
            Spacer(Modifier.height(30.dp))
        }

        SiteFooter()
    }
}

@Composable
private fun IconStepCard(
    step: String,
    icon: ImageVector,
    title: String,
    body: String,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = WarmWhite,
        border = BorderStroke(1.dp, Border),
    ) {
        Column(Modifier.padding(19.dp)) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(TerracottaPale, RoundedCornerShape(13.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Terracotta,
                    modifier = Modifier.size(21.dp),
                )
            }
            Spacer(Modifier.height(16.dp))
            Text(
                text = step,
                style = MaterialTheme.typography.labelSmall,
                color = Terracotta,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = InkStrong,
            )
            Spacer(Modifier.height(5.dp))
            Text(
                text = body,
                style = MaterialTheme.typography.bodyMedium,
                color = Muted,
            )
        }
    }
}
