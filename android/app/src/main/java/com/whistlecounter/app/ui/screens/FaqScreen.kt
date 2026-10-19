package com.whistlecounter.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.whistlecounter.app.ui.components.BrandHeader
import com.whistlecounter.app.ui.components.Eyebrow
import com.whistlecounter.app.ui.components.SiteFooter
import com.whistlecounter.app.ui.theme.Border
import com.whistlecounter.app.ui.theme.CreamDeep
import com.whistlecounter.app.ui.theme.Ink
import com.whistlecounter.app.ui.theme.InkStrong
import com.whistlecounter.app.ui.theme.Muted
import com.whistlecounter.app.ui.theme.MutedSoft
import com.whistlecounter.app.ui.theme.Terracotta
import com.whistlecounter.app.ui.theme.WarmWhite

private val questions = listOf(
    "Does it record or upload my audio?" to
        "No. Audio is analysed on your phone while listening is active. It is never recorded or uploaded.",
    "Why does it need microphone access?" to
        "The counter needs live audio input to check for whistle-like sounds. Android asks for your permission before granting access. You can stop listening at any time.",
    "Will it catch every whistle?" to
        "It may miss a whistle or mistake another sound for one. Phone microphones, distance, background noise, and room acoustics all affect detection. Treat the count as a reminder, not a guarantee.",
    "Can I lock my phone or use another app?" to
        "Yes. The counter runs as an Android foreground service, so listening continues when the screen is off or when you switch apps. A small ongoing notification lets you know it is running.",
    "Will the alarm ring if my phone is on silent?" to
        "The alarm plays through your phone's alarm audio stream and also vibrates, so it is not controlled by your media volume. Do Not Disturb and custom sound settings can still affect it, so check your alarm volume before you start.",
    "Why is there an ongoing notification?" to
        "Android requires a visible notification whenever an app uses the microphone in the background. It shows your progress and includes a Stop listening action.",
    "Why did listening stop on my phone?" to
        "Some phone brands apply extra battery limits to background apps. Open the reliability card on the counter and allow background activity. Also check that notifications are allowed, since Android shows the listening status there.",
    "How do I get help or send feedback?" to
        "Email Bhumit at marubhumit0312@gmail.com for help, questions, or feedback.",
)

@Composable
fun FaqScreen(onOpenCounter: () -> Unit) {
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
            Eyebrow("Good to know")
            Spacer(Modifier.height(14.dp))
            Text(
                text = "Questions, answered.",
                style = MaterialTheme.typography.headlineLarge,
                color = InkStrong,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(10.dp))
            Text(
                text = "A few practical details to help you use the counter with confidence.",
                style = MaterialTheme.typography.bodyLarge,
                color = Muted,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(22.dp))
        }

        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            questions.forEachIndexed { index, pair ->
                FaqItem(
                    question = pair.first,
                    answer = pair.second,
                    showTopBorder = index == 0,
                )
            }
        }

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp)
                .padding(top = 24.dp),
            shape = RoundedCornerShape(22.dp),
            color = CreamDeep,
        ) {
            Column(
                modifier = Modifier.padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "Ready to keep count?",
                    style = MaterialTheme.typography.headlineSmall,
                    color = InkStrong,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "Set a target and start listening whenever you're ready.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Muted,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = onOpenCounter,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = InkStrong,
                        contentColor = WarmWhite,
                    ),
                ) {
                    Text("Open the counter", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Rounded.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                    )
                }
            }
        }

        Spacer(Modifier.height(30.dp))
        SiteFooter()
    }
}

@Composable
private fun FaqItem(
    question: String,
    answer: String,
    showTopBorder: Boolean,
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    val rotation by animateFloatAsState(
        targetValue = if (expanded) 45f else 0f,
        label = "faqRotation",
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(role = Role.Button) { expanded = !expanded },
    ) {
        if (showTopBorder) HorizontalDivider(color = Border, thickness = 1.dp)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 17.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = question,
                style = MaterialTheme.typography.titleSmall,
                color = Ink,
                modifier = Modifier.weight(1f),
            )
            Spacer(Modifier.width(14.dp))
            Icon(
                imageVector = Icons.Rounded.Add,
                contentDescription = if (expanded) "Collapse answer" else "Expand answer",
                tint = Terracotta,
                modifier = Modifier
                    .size(21.dp)
                    .rotate(rotation),
            )
        }
        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically(),
        ) {
            Text(
                text = answer,
                style = MaterialTheme.typography.bodyMedium,
                color = Muted,
                modifier = Modifier
                    .padding(bottom = 18.dp)
                    .padding(end = 28.dp),
            )
        }
        HorizontalDivider(color = Border, thickness = 1.dp)
    }
}
