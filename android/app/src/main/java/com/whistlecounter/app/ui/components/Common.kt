package com.whistlecounter.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.VerifiedUser
import androidx.compose.material.icons.rounded.LocalFireDepartment
import com.whistlecounter.app.ui.theme.Border
import com.whistlecounter.app.ui.theme.BorderSoft
import com.whistlecounter.app.ui.theme.CardCream
import com.whistlecounter.app.ui.theme.Cream
import com.whistlecounter.app.ui.theme.Ink
import com.whistlecounter.app.ui.theme.InkStrong
import com.whistlecounter.app.ui.theme.Muted
import com.whistlecounter.app.ui.theme.MutedSoft
import com.whistlecounter.app.ui.theme.Peach
import com.whistlecounter.app.ui.theme.Sage
import com.whistlecounter.app.ui.theme.SagePale
import com.whistlecounter.app.ui.theme.SageSoft
import com.whistlecounter.app.ui.theme.Terracotta
import com.whistlecounter.app.ui.theme.TerracottaBright
import com.whistlecounter.app.ui.theme.TerracottaPale
import com.whistlecounter.app.ui.theme.WarmWhite

@Composable
fun BrandHeader(
    modifier: Modifier = Modifier,
    trailing: @Composable (() -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .background(TerracottaBright, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Rounded.LocalFireDepartment,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp),
            )
        }
        Spacer(Modifier.width(10.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Whistle ",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.7).sp,
                ),
                color = InkStrong,
            )
            Text(
                text = "Counter",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Normal,
                    letterSpacing = (-0.7).sp,
                ),
                color = MutedSoft,
            )
        }
        Spacer(Modifier.weight(1f))
        trailing?.invoke()
    }
}

@Composable
fun Eyebrow(text: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(9.dp),
    ) {
        Box(
            modifier = Modifier
                .size(15.dp)
                .background(TerracottaPale, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                Modifier
                    .size(7.dp)
                    .background(TerracottaBright, CircleShape),
            )
        }
        Text(
            text = text.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = Terracotta,
        )
    }
}

@Composable
fun SectionHeading(
    eyebrow: String,
    title: String,
    body: String? = null,
    modifier: Modifier = Modifier,
    centered: Boolean = true,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = if (centered) Alignment.CenterHorizontally else Alignment.Start,
    ) {
        Eyebrow(eyebrow)
        Spacer(Modifier.height(12.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            color = InkStrong,
            textAlign = if (centered) TextAlign.Center else TextAlign.Start,
        )
        if (body != null) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = body,
                style = MaterialTheme.typography.bodyMedium,
                color = Muted,
                textAlign = if (centered) TextAlign.Center else TextAlign.Start,
            )
        }
    }
}

@Composable
fun ProofChip(icon: ImageVector, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Sage,
            modifier = Modifier.size(15.dp),
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
            color = MutedSoft,
        )
    }
}

@Composable
fun WarmCard(
    modifier: Modifier = Modifier,
    color: Color = CardCream,
    borderColor: Color = Border,
    content: @Composable () -> Unit,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        color = color,
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor),
    ) {
        content()
    }
}

@Composable
fun StepCard(
    number: String,
    title: String,
    body: String,
) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 15.dp),
    ) {
        HorizontalDivider(color = Border, thickness = 1.dp)
        Spacer(Modifier.height(15.dp))
        Text(
            text = number,
            style = MaterialTheme.typography.labelSmall,
            color = TerracottaBright,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
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

@Composable
fun SafetyNote(
    title: String,
    body: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = SageSoft,
    ) {
        Row(
            Modifier.padding(18.dp),
            horizontalArrangement = Arrangement.spacedBy(13.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(SagePale, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Outlined.VerifiedUser,
                    contentDescription = null,
                    tint = Sage,
                    modifier = Modifier.size(19.dp),
                )
            }
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    color = InkStrong,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = body,
                    style = MaterialTheme.typography.bodySmall,
                    color = Sage,
                )
            }
        }
    }
}

@Composable
fun SiteFooter(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(com.whistlecounter.app.ui.theme.FooterGreen)
            .padding(horizontal = 22.dp, vertical = 24.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .background(TerracottaBright, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Rounded.LocalFireDepartment,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(17.dp),
                )
            }
            Spacer(Modifier.width(9.dp))
            Text(
                text = "Whistle Counter",
                style = MaterialTheme.typography.titleMedium,
                color = WarmWhite,
            )
        }
        Spacer(Modifier.height(10.dp))
        Text(
            text = "A simple helper for a busy kitchen. Stay nearby and follow your cooker's instructions.",
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFFC1C7BF),
        )
        Spacer(Modifier.height(14.dp))
        HorizontalDivider(color = com.whistlecounter.app.ui.theme.FooterLine)
        Spacer(Modifier.height(12.dp))
        Text(
            text = "Built by Bhumit Maru  ·  Local audio only  ·  No account needed",
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
            color = Color(0xFFAAB3AA),
        )
    }
}

/**
 * A small decorative pressure cooker, drawn once with Canvas so no image asset
 * or network request is needed.
 */
@Composable
fun PotIllustration(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Canvas(Modifier.fillMaxWidth().height(170.dp)) {
            val w = size.width
            val h = size.height
            val cx = w / 2f
            val sunRadius = minOf(w, h) * 0.36f
            drawCircle(
                Brush.radialGradient(
                    listOf(Peach, Color(0xFFF7E7D6)),
                    center = Offset(cx, h * 0.52f),
                    radius = sunRadius * 1.2f,
                ),
                radius = sunRadius,
                center = Offset(cx, h * 0.52f),
            )
            drawCircle(
                color = Terracotta.copy(alpha = 0.13f),
                radius = sunRadius * 0.9f,
                center = Offset(cx, h * 0.52f),
                style = Stroke(width = 1.dp.toPx()),
            )

            // Steam
            val steamColor = Terracotta.copy(alpha = 0.33f)
            for (i in 0..2) {
                val x = cx - 18.dp.toPx() + i * 17.dp.toPx()
                val path = Path().apply {
                    moveTo(x, h * 0.24f)
                    quadraticBezierTo(x - 7.dp.toPx(), h * 0.18f, x, h * 0.1f)
                }
                drawPath(path, steamColor, style = Stroke(width = 2.dp.toPx()))
            }

            // Handles
            val bodyTop = h * 0.48f
            val bodyHeight = h * 0.35f
            val bodyWidth = w * 0.56f
            val bodyLeft = cx - bodyWidth / 2f
            drawRoundRect(
                color = Color(0xFF3C4E44),
                topLeft = Offset(bodyLeft - 15.dp.toPx(), bodyTop + 12.dp.toPx()),
                size = Size(22.dp.toPx(), 12.dp.toPx()),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(6.dp.toPx()),
            )
            drawRoundRect(
                color = Color(0xFF3C4E44),
                topLeft = Offset(bodyLeft + bodyWidth - 7.dp.toPx(), bodyTop + 12.dp.toPx()),
                size = Size(22.dp.toPx(), 12.dp.toPx()),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(6.dp.toPx()),
            )

            // Body
            drawRoundRect(
                brush = Brush.horizontalGradient(
                    listOf(Color(0xFF76857B), Color(0xFF43574D), Color(0xFF293A32), Color(0xFF64766A)),
                    startX = bodyLeft,
                    endX = bodyLeft + bodyWidth,
                ),
                topLeft = Offset(bodyLeft, bodyTop),
                size = Size(bodyWidth, bodyHeight),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(14.dp.toPx(), 22.dp.toPx()),
            )
            drawRoundRect(
                color = Color(0xFFD8D0C3),
                topLeft = Offset(bodyLeft, bodyTop),
                size = Size(bodyWidth, 8.dp.toPx()),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx()),
            )

            // Lid
            drawOval(
                color = Color(0xFFA9A89C),
                topLeft = Offset(bodyLeft - 4.dp.toPx(), bodyTop - 10.dp.toPx()),
                size = Size(bodyWidth + 8.dp.toPx(), 15.dp.toPx()),
            )
            drawRoundRect(
                color = Color(0xFF525F55),
                topLeft = Offset(cx - 12.dp.toPx(), bodyTop - 18.dp.toPx()),
                size = Size(24.dp.toPx(), 8.dp.toPx()),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx()),
            )
            drawRoundRect(
                color = Color(0xFF313C35),
                topLeft = Offset(cx - 2.dp.toPx(), bodyTop - 25.dp.toPx()),
                size = Size(4.dp.toPx(), 9.dp.toPx()),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(2.dp.toPx()),
            )

            // Shine
            drawRoundRect(
                color = Color.White.copy(alpha = 0.2f),
                topLeft = Offset(bodyLeft + 12.dp.toPx(), bodyTop + 14.dp.toPx()),
                size = Size(3.dp.toPx(), bodyHeight * 0.55f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(2.dp.toPx()),
            )
        }
    }
}
