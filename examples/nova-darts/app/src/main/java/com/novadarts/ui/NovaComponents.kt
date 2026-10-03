package com.novadarts.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun Eyebrow(text: String, modifier: Modifier = Modifier, color: Color = NovaPalette.muted) {
    Text(
        text = text.uppercase(),
        modifier = modifier,
        color = color,
        fontSize = 10.sp,
        lineHeight = 13.sp,
        letterSpacing = 1.7.sp,
        fontWeight = FontWeight.Bold,
    )
}

@Composable
fun BrandMark(modifier: Modifier = Modifier, compact: Boolean = false) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(if (compact) 32.dp else 38.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(NovaPalette.lime),
            contentAlignment = Alignment.Center,
        ) {
            Text("N", color = NovaPalette.ink, fontSize = 20.sp, fontWeight = FontWeight.Black)
        }
        Spacer(Modifier.width(10.dp))
        Column(verticalArrangement = Arrangement.Center) {
            Text(
                "NOVA",
                color = NovaPalette.text,
                fontSize = if (compact) 14.sp else 16.sp,
                lineHeight = 16.sp,
                letterSpacing = 2.1.sp,
                fontWeight = FontWeight.Black,
            )
            Eyebrow("DARTS / LOCAL", color = NovaPalette.muted)
        }
    }
}

@Composable
fun OfflinePill(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .clip(CircleShape)
            .background(Color(0xFF14211D))
            .border(1.dp, Color(0xFF294237), CircleShape)
            .padding(horizontal = 10.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.size(6.dp).clip(CircleShape).background(NovaPalette.mint))
        Spacer(Modifier.width(7.dp))
        Text("OFFLINE", color = NovaPalette.mint, fontSize = 9.sp, letterSpacing = 1.1.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun CoinPill(coins: Int, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .clip(CircleShape)
            .background(NovaPalette.panelRaised)
            .border(1.dp, NovaPalette.line, CircleShape)
            .padding(horizontal = 11.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text("✦", color = NovaPalette.lime, fontSize = 12.sp)
        Spacer(Modifier.width(6.dp))
        Text("$coins", color = NovaPalette.text, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun PrimaryAction(
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leading: String = "↗",
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(17.dp)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(58.dp)
            .clip(shape)
            .background(if (enabled) NovaPalette.lime else Color(0xFF39434A))
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 19.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            label.uppercase(),
            color = if (enabled) NovaPalette.ink else NovaPalette.muted,
            fontSize = 12.sp,
            letterSpacing = 1.15.sp,
            fontWeight = FontWeight.Black,
        )
        Text(leading, color = if (enabled) NovaPalette.ink else NovaPalette.muted, fontSize = 19.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun SecondaryAction(
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(15.dp)
    Box(
        modifier = modifier
            .height(50.dp)
            .clip(shape)
            .background(NovaPalette.panelRaised)
            .border(1.dp, NovaPalette.line, shape)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            label.uppercase(),
            color = if (enabled) NovaPalette.text else NovaPalette.muted,
            fontSize = 10.sp,
            letterSpacing = 1.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
fun InfoTag(text: String, modifier: Modifier = Modifier, tint: Color = NovaPalette.lime) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(tint.copy(alpha = 0.11f))
            .border(1.dp, tint.copy(alpha = 0.25f), RoundedCornerShape(8.dp))
            .padding(horizontal = 9.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(text.uppercase(), color = tint, fontSize = 9.sp, letterSpacing = 1.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun SectionTitle(
    title: String,
    trailing: String? = null,
    modifier: Modifier = Modifier,
    onTrailingClick: (() -> Unit)? = null,
) {
    Row(modifier = modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Eyebrow(title, modifier = Modifier.weight(1f), color = NovaPalette.soft)
        if (trailing != null) {
            Text(
                trailing.uppercase(),
                color = NovaPalette.lime,
                fontSize = 9.sp,
                letterSpacing = 1.sp,
                fontWeight = FontWeight.Bold,
                modifier = if (onTrailingClick != null) Modifier.clickable(onClick = onTrailingClick).padding(4.dp) else Modifier,
            )
        }
    }
}

@Composable
fun GlassPanel(
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(22.dp),
    content: @Composable () -> Unit,
) {
    Surface(
        modifier = modifier,
        shape = shape,
        color = NovaPalette.panel,
        border = BorderStroke(1.dp, NovaPalette.line.copy(alpha = 0.85f)),
        content = content,
    )
}

@Composable
fun RatingBar(label: String, value: Int, tint: Color, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Eyebrow(label, modifier = Modifier.weight(1f), color = NovaPalette.muted)
            Text("$value", color = NovaPalette.text, fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
        }
        Spacer(Modifier.height(7.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(5.dp)
                .clip(CircleShape)
                .background(NovaPalette.line),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(value.coerceIn(0, 100) / 100f)
                    .height(5.dp)
                    .clip(CircleShape)
                    .background(Brush.horizontalGradient(listOf(tint.copy(alpha = 0.62f), tint))),
            )
        }
    }
}

@Composable
fun MetricTile(
    label: String,
    value: String,
    footnote: String,
    accent: Color = NovaPalette.lime,
    modifier: Modifier = Modifier,
    minHeight: Dp = 118.dp,
) {
    Column(
        modifier = modifier
            .defaultMinSize(minHeight = minHeight)
            .clip(RoundedCornerShape(18.dp))
            .background(NovaPalette.panel)
            .border(1.dp, NovaPalette.line, RoundedCornerShape(18.dp))
            .padding(15.dp),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Eyebrow(label)
        Column {
            Text(value, color = accent, fontSize = 28.sp, lineHeight = 31.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace)
            Spacer(Modifier.height(3.dp))
            Text(footnote, color = NovaPalette.muted, fontSize = 10.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
        }
    }
}
