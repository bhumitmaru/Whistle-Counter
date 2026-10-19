package com.whistlecounter.app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.OpenInNew
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
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.whistlecounter.app.ui.components.BrandHeader
import com.whistlecounter.app.ui.components.Eyebrow
import com.whistlecounter.app.ui.components.PotIllustration
import com.whistlecounter.app.ui.components.SiteFooter
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
fun AboutScreen(onOpenCounter: () -> Unit) {
    val uriHandler = LocalUriHandler.current

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
            Eyebrow("About Whistle Counter")
            Spacer(Modifier.height(14.dp))
            Text(
                text = "A little less",
                style = MaterialTheme.typography.headlineLarge,
                color = InkStrong,
                textAlign = TextAlign.Center,
            )
            Text(
                text = "keeping track.",
                style = MaterialTheme.typography.headlineLarge,
                color = Terracotta,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = "Pressure cooker whistles are easy to lose count of when you're cooking, helping, and doing three other things. This small tool is here to take one thing off your mental list.",
                style = MaterialTheme.typography.bodyLarge,
                color = Muted,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(8.dp))
            PotIllustration(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(190.dp),
            )
        }

        Column(modifier = Modifier.padding(horizontal = 22.dp)) {
            Text(
                text = "Made for the rhythm of a real kitchen",
                style = MaterialTheme.typography.titleLarge,
                color = InkStrong,
            )
            Spacer(Modifier.height(7.dp))
            Text(
                text = "Set the number you're watching for, start the counter, and let it listen while you work. No account is needed. The counter only listens after you choose to start it, and audio never leaves your phone.",
                style = MaterialTheme.typography.bodyMedium,
                color = Muted,
            )

            Spacer(Modifier.height(20.dp))
            Text(
                text = "Designed to assist, not decide",
                style = MaterialTheme.typography.titleLarge,
                color = InkStrong,
            )
            Spacer(Modifier.height(7.dp))
            Text(
                text = "Every stove, cooker, room, and phone sounds a little different. Whistle Counter can be useful, but it cannot guarantee that every whistle is heard correctly. Keep an eye on your cooking and follow the guidance that came with your pressure cooker.",
                style = MaterialTheme.typography.bodyMedium,
                color = Muted,
            )

            Spacer(Modifier.height(22.dp))
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = SageSoft,
            ) {
                Column(Modifier.padding(19.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .background(WarmWhite.copy(alpha = 0.6f), CircleShape),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Email,
                                contentDescription = null,
                                tint = SageDeep,
                                modifier = Modifier.size(19.dp),
                            )
                        }
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Say hello",
                                style = MaterialTheme.typography.titleMedium,
                                color = InkStrong,
                            )
                            Text(
                                text = "Questions and feedback are welcome.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Sage,
                            )
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    ContactRow(
                        label = "marubhumit0312@gmail.com",
                        onClick = { uriHandler.openUri("mailto:marubhumit0312@gmail.com") },
                    )
                    Spacer(Modifier.height(8.dp))
                    ContactRow(
                        label = "Bhumit Maru on LinkedIn",
                        onClick = { uriHandler.openUri("https://www.linkedin.com/in/bhumitmaru/") },
                    )
                }
            }

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
                Text("Try the counter", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Rounded.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                )
            }

            Spacer(Modifier.height(18.dp))
            Text(
                text = "Version 1.0.0  ·  Made with care for busy kitchens",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                color = Muted,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(30.dp))
        }

        SiteFooter()
    }
}

@Composable
private fun ContactRow(label: String, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = WarmWhite.copy(alpha = 0.7f),
        border = BorderStroke(1.dp, Sage.copy(alpha = 0.18f)),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 13.dp, vertical = 11.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = SageDeep,
            )
            Icon(
                imageVector = Icons.Outlined.OpenInNew,
                contentDescription = null,
                tint = Terracotta,
                modifier = Modifier.size(16.dp),
            )
        }
    }
}
