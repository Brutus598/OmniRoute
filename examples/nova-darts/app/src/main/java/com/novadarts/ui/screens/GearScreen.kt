package com.novadarts.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.novadarts.data.GearCatalog
import com.novadarts.data.GearItem
import com.novadarts.data.GearSlot
import com.novadarts.data.PlayerProgress
import com.novadarts.ui.CoinPill
import com.novadarts.ui.Eyebrow
import com.novadarts.ui.GlassPanel
import com.novadarts.ui.NovaPalette
import com.novadarts.ui.RatingBar
import com.novadarts.ui.SectionTitle
import com.novadarts.ui.SecondaryAction

@Composable
fun GearScreen(
    progress: PlayerProgress,
    onEquipOrUnlock: (String) -> Unit,
) {
    var selectedSlot by remember { mutableStateOf(GearSlot.BARREL) }
    val ratings = GearCatalog.ratings(progress)
    val items = GearCatalog.forSlot(selectedSlot)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(top = 17.dp, bottom = 27.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Eyebrow("NOVA / ARMORY", color = NovaPalette.mint)
                Spacer(Modifier.height(5.dp))
                Text("BAUE DEIN\nSETUP.", color = NovaPalette.text, fontSize = 31.sp, lineHeight = 34.sp, fontWeight = FontWeight.Black)
            }
            CoinPill(progress.coins)
        }
        Spacer(Modifier.height(8.dp))
        Text("20 Teile. 5 Slots. Alles durch Offline-Spiel freischaltbar.", color = NovaPalette.muted, fontSize = 12.sp, lineHeight = 17.sp)
        Spacer(Modifier.height(18.dp))

        GlassPanel(shape = RoundedCornerShape(22.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Eyebrow("EQUIPPED / LOADOUT 01", color = NovaPalette.mint)
                        Spacer(Modifier.height(4.dp))
                        Text("NOVA / VECTOR", color = NovaPalette.text, fontSize = 17.sp, fontWeight = FontWeight.Black)
                    }
                    GearDartPreview(
                        modifier = Modifier.size(width = 82.dp, height = 57.dp),
                        accent = NovaPalette.lime,
                    )
                }
                Spacer(Modifier.height(15.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    RatingBar("PRECISION", ratings.precision, NovaPalette.lime, modifier = Modifier.weight(1f))
                    RatingBar("STABILITY", ratings.stability, NovaPalette.mint, modifier = Modifier.weight(1f))
                }
                Spacer(Modifier.height(13.dp))
                Text(
                    "Werte sind reine Loadout-Optik. Jeder Dart trifft exakt dort, wo du zielst.",
                    color = NovaPalette.muted,
                    fontSize = 10.sp,
                    lineHeight = 15.sp,
                )
            }
        }

        Spacer(Modifier.height(22.dp))
        SectionTitle("KOMPONENTEN")
        Spacer(Modifier.height(10.dp))
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            GearSlot.entries.forEach { slot ->
                SlotChip(slot, selected = slot == selectedSlot, onClick = { selectedSlot = slot })
            }
        }
        Spacer(Modifier.height(13.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Eyebrow("${selectedSlot.title} / ${items.size} DESIGNS", modifier = Modifier.weight(1f), color = NovaPalette.soft)
            Eyebrow("${items.count { it.id in progress.ownedGear }} OWNED", color = NovaPalette.mint)
        }
        Spacer(Modifier.height(10.dp))
        items.forEach { item ->
            GearItemCard(
                item = item,
                progress = progress,
                onClick = { onEquipOrUnlock(item.id) },
            )
            Spacer(Modifier.height(9.dp))
        }
        Spacer(Modifier.height(10.dp))
        Text(
            "Münzen gibt es nur für abgeschlossene Sessions. Keine Echtgeldkäufe, keine Werbung.",
            color = NovaPalette.muted,
            fontSize = 10.sp,
            lineHeight = 15.sp,
        )
    }
}

@Composable
private fun SlotChip(slot: GearSlot, selected: Boolean, onClick: () -> Unit) {
    val tint = if (selected) NovaPalette.lime else NovaPalette.soft
    Row(
        modifier = Modifier
            .clip(CircleShape)
            .background(if (selected) NovaPalette.lime.copy(alpha = 0.12f) else NovaPalette.panel)
            .border(1.dp, if (selected) NovaPalette.lime.copy(alpha = 0.45f) else NovaPalette.line, CircleShape)
            .clickable(onClick = onClick)
            .padding(horizontal = 13.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(slot.shortTitle, color = tint, fontSize = 9.sp, fontWeight = FontWeight.Black, letterSpacing = 0.8.sp)
        Spacer(Modifier.width(7.dp))
        Text(slot.title, color = tint, fontSize = 9.sp, letterSpacing = 0.8.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun GearItemCard(item: GearItem, progress: PlayerProgress, onClick: () -> Unit) {
    val owned = item.id in progress.ownedGear
    val equipped = progress.equipped[item.slot] == item.id
    val canAfford = progress.coins >= item.cost
    val accent = Color(item.accentArgb)

    GlassPanel(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(19.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 13.dp, vertical = 13.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(15.dp))
                    .background(accent.copy(alpha = 0.1f))
                    .border(1.dp, accent.copy(alpha = 0.18f), RoundedCornerShape(15.dp)),
                contentAlignment = Alignment.Center,
            ) {
                GearDartPreview(modifier = Modifier.size(42.dp), accent = accent, slot = item.slot)
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(item.name, color = NovaPalette.text, fontSize = 11.sp, fontWeight = FontWeight.Black, letterSpacing = 0.3.sp, maxLines = 1)
                    if (equipped) {
                        Spacer(Modifier.width(6.dp))
                        Text("●", color = NovaPalette.mint, fontSize = 8.sp)
                    }
                }
                Spacer(Modifier.height(4.dp))
                Text(item.detail, color = NovaPalette.muted, fontSize = 9.sp, lineHeight = 13.sp, maxLines = 2)
                Spacer(Modifier.height(5.dp))
                Text(
                    "P ${item.precision.toString().padStart(2, '0')}   S ${item.stability.toString().padStart(2, '0')}   G ${item.grip.toString().padStart(2, '0')}",
                    color = accent,
                    fontSize = 8.sp,
                    letterSpacing = 0.35.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
            Spacer(Modifier.width(8.dp))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                when {
                    equipped -> Text("AKTIV", color = NovaPalette.mint, fontSize = 8.sp, letterSpacing = 0.8.sp, fontWeight = FontWeight.Black)
                    owned -> SecondaryAction("EQUIP", modifier = Modifier.width(84.dp), onClick = onClick)
                    else -> {
                        Text("✦ ${item.cost}", color = if (canAfford) NovaPalette.lime else NovaPalette.muted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(5.dp))
                        SecondaryAction("KAUFEN", modifier = Modifier.width(84.dp), enabled = canAfford, onClick = onClick)
                    }
                }
            }
        }
    }
}

@Composable
private fun GearDartPreview(
    modifier: Modifier = Modifier,
    accent: Color,
    slot: GearSlot? = null,
) {
    Canvas(modifier = modifier) {
        val centerX = size.width / 2f
        val centerY = size.height / 2f
        val stroke = size.minDimension * 0.08f
        val dark = Color(0xFFB8C1CB)
        val shaftStart = Offset(centerX - size.width * 0.22f, centerY + size.height * 0.25f)
        val shaftEnd = Offset(centerX + size.width * 0.20f, centerY - size.height * 0.24f)
        val dx = shaftEnd.x - shaftStart.x
        val dy = shaftEnd.y - shaftStart.y
        val length = kotlin.math.sqrt(dx * dx + dy * dy)
        val unitX = dx / length
        val unitY = dy / length
        val perpendicularX = -unitY
        val perpendicularY = unitX

        drawLine(dark, shaftStart, shaftEnd, strokeWidth = stroke, cap = StrokeCap.Round)
        drawLine(
            color = accent,
            start = Offset(shaftStart.x + dx * 0.35f, shaftStart.y + dy * 0.35f),
            end = Offset(shaftStart.x + dx * 0.66f, shaftStart.y + dy * 0.66f),
            strokeWidth = stroke * 2.2f,
            cap = StrokeCap.Round,
        )
        drawLine(accent, shaftEnd, Offset(shaftEnd.x + unitX * size.width * 0.13f, shaftEnd.y + unitY * size.width * 0.13f), strokeWidth = stroke * 0.55f, cap = StrokeCap.Round)

        val tail = Offset(shaftStart.x - unitX * size.width * 0.17f, shaftStart.y - unitY * size.width * 0.17f)
        val wing = Path().apply {
            moveTo(tail.x, tail.y)
            lineTo(tail.x + perpendicularX * size.height * 0.24f - unitX * size.width * 0.03f, tail.y + perpendicularY * size.height * 0.24f - unitY * size.width * 0.03f)
            lineTo(tail.x - unitX * size.width * 0.11f, tail.y - unitY * size.width * 0.11f)
            lineTo(tail.x - perpendicularX * size.height * 0.24f - unitX * size.width * 0.03f, tail.y - perpendicularY * size.height * 0.24f - unitY * size.width * 0.03f)
            close()
        }
        drawPath(wing, accent)
        drawCircle(accent.copy(alpha = 0.34f), radius = stroke * 2.6f, center = Offset(centerX, centerY))

        if (slot == GearSlot.ACCESSORY) {
            drawCircle(accent.copy(alpha = 0.7f), radius = size.minDimension * 0.26f, center = Offset(centerX, centerY), style = Stroke(stroke * 0.75f))
        }
    }
}
