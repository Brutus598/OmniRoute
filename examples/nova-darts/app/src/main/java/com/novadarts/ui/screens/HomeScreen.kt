package com.novadarts.ui.screens

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.novadarts.data.GearCatalog
import com.novadarts.data.PlayerProgress
import com.novadarts.game.GameMode
import com.novadarts.ui.BoardIllustration
import com.novadarts.ui.BrandMark
import com.novadarts.ui.CoinPill
import com.novadarts.ui.Eyebrow
import com.novadarts.ui.GlassPanel
import com.novadarts.ui.InfoTag
import com.novadarts.ui.NovaPalette
import com.novadarts.ui.OfflinePill
import com.novadarts.ui.PrimaryAction
import com.novadarts.ui.SectionTitle

@Composable
fun HomeScreen(
    progress: PlayerProgress,
    onStart: (GameMode) -> Unit,
    onOpenPlay: () -> Unit,
    onOpenGear: () -> Unit,
) {
    val level = (1 + progress.matches / 5).coerceAtMost(20)
    val equipped = GearCatalog.equippedItems(progress)
    val barrel = equipped.firstOrNull { it.slot.name == "BARREL" }?.name ?: "BASALT 90%"
    val flight = equipped.firstOrNull { it.slot.name == "FLIGHT" }?.name ?: "VECTOR 100"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(top = 16.dp, bottom = 22.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            BrandMark(compact = true)
            Spacer(Modifier.weight(1f))
            CoinPill(progress.coins)
            Spacer(Modifier.size(8.dp))
            OfflinePill()
        }

        Spacer(Modifier.height(26.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Eyebrow("NOVA LEAGUE / SPIELER 01", color = NovaPalette.mint)
            Spacer(Modifier.weight(1f))
            InfoTag("LVL ${level.toString().padStart(2, '0')}", tint = NovaPalette.lime)
        }
        Spacer(Modifier.height(9.dp))
        Text(
            "DEIN BOARD.\nDEIN MOMENT.",
            color = NovaPalette.text,
            fontSize = 32.sp,
            lineHeight = 34.sp,
            letterSpacing = -0.7.sp,
            fontWeight = FontWeight.Black,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "Scharf zielen. Ruhig bleiben. Bull treffen.",
            color = NovaPalette.muted,
            fontSize = 13.sp,
            lineHeight = 19.sp,
        )

        Spacer(Modifier.height(18.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(26.dp))
                .background(
                    Brush.linearGradient(
                        listOf(Color(0xFF1C2B2B), Color(0xFF131B21), Color(0xFF111720)),
                    ),
                )
                .border(1.dp, Color(0xFF314742), RoundedCornerShape(26.dp))
                .height(272.dp),
        ) {
            BoardIllustration(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 10.dp)
                    .size(160.dp),
            )
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .size(185.dp)
                    .background(Brush.horizontalGradient(listOf(Color.Transparent, Color(0x00111720), Color(0xBB111720)))),
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.73f)
                    .align(Alignment.CenterStart)
                    .padding(start = 19.dp, top = 19.dp, bottom = 18.dp),
                verticalArrangement = Arrangement.spacedBy(9.dp),
            ) {
                InfoTag("501 // DOUBLE OUT", tint = NovaPalette.mint)
                Text(
                    "DER ERSTE\nWURF ZÄHLT.",
                    color = NovaPalette.text,
                    fontSize = 23.sp,
                    lineHeight = 26.sp,
                    letterSpacing = -0.35.sp,
                    fontWeight = FontWeight.Black,
                )
                Text(
                    "Spiel lokal gegen die NOVA-KI.\nKein Login. Kein Netz.",
                    color = NovaPalette.soft,
                    fontSize = 11.sp,
                    lineHeight = 16.sp,
                )
                Spacer(Modifier.height(2.dp))
                PrimaryAction(
                    label = "MATCH STARTEN",
                    leading = "↗",
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { onStart(GameMode.CLASSIC_501) },
                )
            }
            Eyebrow(
                "01 / 03",
                modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
                color = NovaPalette.mint.copy(alpha = 0.76f),
            )
        }

        Spacer(Modifier.height(27.dp))
        SectionTitle("KURZ EINGESTIEGEN", "ALLE MODI", onTrailingClick = onOpenPlay)
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(11.dp)) {
            ModeShortcut(
                title = "AROUND\nTHE CLOCK",
                subtitle = "1 → 20 → BULL",
                accent = NovaPalette.mint,
                modifier = Modifier.weight(1f),
                onClick = { onStart(GameMode.AROUND_CLOCK) },
            )
            ModeShortcut(
                title = "BULL\nRUSH",
                subtitle = "30 PFEILE / HIGH SCORE",
                accent = NovaPalette.coral,
                modifier = Modifier.weight(1f),
                onClick = { onStart(GameMode.BULL_RUSH) },
            )
        }

        Spacer(Modifier.height(26.dp))
        SectionTitle("DEIN SETUP", "ARMORY ÖFFNEN", onTrailingClick = onOpenGear)
        Spacer(Modifier.height(12.dp))
        GlassPanel(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onOpenGear),
            shape = RoundedCornerShape(20.dp),
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 15.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF202A30))
                        .border(1.dp, NovaPalette.line, RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("➤", color = NovaPalette.lime, fontSize = 23.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.size(13.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("NOVA / LOADOUT 01", color = NovaPalette.text, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "$barrel  ·  $flight  ·  ${equipped.size} TEILE",
                        color = NovaPalette.muted,
                        fontSize = 9.sp,
                        letterSpacing = 0.3.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Text("↗", color = NovaPalette.lime, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(Modifier.height(24.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(Color(0xFF111820))
                .padding(horizontal = 15.dp, vertical = 13.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Eyebrow("LOKAL GESPEICHERT", color = NovaPalette.mint)
            Spacer(Modifier.weight(1f))
            Text("${progress.matches} MATCHES  ·  ${progress.wins} SIEGE", color = NovaPalette.muted, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
        }
    }
}

@Composable
private fun ModeShortcut(
    title: String,
    subtitle: String,
    accent: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Column(
        modifier = modifier
            .height(145.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(NovaPalette.panel)
            .border(1.dp, NovaPalette.line, RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .padding(15.dp),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(7.dp).clip(RoundedCornerShape(3.dp)).background(accent))
            Spacer(Modifier.size(7.dp))
            Eyebrow("SOLO / OFFLINE", color = NovaPalette.muted)
        }
        Column {
            Text(title, color = NovaPalette.text, fontSize = 16.sp, lineHeight = 18.sp, fontWeight = FontWeight.Black)
            Spacer(Modifier.height(5.dp))
            Text(subtitle, color = accent, fontSize = 8.sp, letterSpacing = 0.7.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
fun PlayScreen(onStart: (GameMode) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(top = 18.dp, bottom = 25.dp),
    ) {
        Eyebrow("NOVA / GAME SELECT", color = NovaPalette.mint)
        Spacer(Modifier.height(8.dp))
        Text("WÄHLE DEIN\nSPIEL.", color = NovaPalette.text, fontSize = 34.sp, lineHeight = 36.sp, fontWeight = FontWeight.Black)
        Spacer(Modifier.height(8.dp))
        Text("Drei Modi. Ein Board. Komplett offline.", color = NovaPalette.muted, fontSize = 13.sp)
        Spacer(Modifier.height(23.dp))

        ModeCard(
            mode = GameMode.CLASSIC_501,
            number = "01",
            accent = NovaPalette.lime,
            title = "501 DUELL",
            description = "Von 501 auf null — mit Double-out. Die NOVA-KI wirft nach deinem dritten Dart.",
            rule = "BEST OF 1 LEG  ·  DOUBLE OUT",
            onClick = { onStart(GameMode.CLASSIC_501) },
        )
        Spacer(Modifier.height(13.dp))
        ModeCard(
            mode = GameMode.AROUND_CLOCK,
            number = "02",
            accent = NovaPalette.mint,
            title = "AROUND THE CLOCK",
            description = "Triff die Segmente der Reihe nach: 1 bis 20. Zum Schluss wartet das Bull.",
            rule = "ROUTE 1 → 20 → BULL",
            onClick = { onStart(GameMode.AROUND_CLOCK) },
        )
        Spacer(Modifier.height(13.dp))
        ModeCard(
            mode = GameMode.BULL_RUSH,
            number = "03",
            accent = NovaPalette.coral,
            title = "BULL RUSH",
            description = "30 Würfe, freie Wahl. Sammle Punkte und schreib deinen lokalen Rekord.",
            rule = "30 DARTS  ·  HIGH SCORE",
            onClick = { onStart(GameMode.BULL_RUSH) },
        )
        Spacer(Modifier.height(19.dp))
        GlassPanel(shape = RoundedCornerShape(18.dp)) {
            Row(
                modifier = Modifier.padding(15.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                InfoTag("LOCAL", tint = NovaPalette.mint)
                Spacer(Modifier.size(11.dp))
                Text("Kein Account, kein Matchmaking, keine Werbung. Dein Fortschritt bleibt auf diesem Gerät.", color = NovaPalette.muted, fontSize = 11.sp, lineHeight = 16.sp)
            }
        }
    }
}

@Composable
private fun ModeCard(
    mode: GameMode,
    number: String,
    accent: Color,
    title: String,
    description: String,
    rule: String,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(
                Brush.linearGradient(
                    listOf(accent.copy(alpha = 0.12f), NovaPalette.panel, NovaPalette.panel),
                ),
            )
            .border(1.dp, accent.copy(alpha = 0.24f), RoundedCornerShape(22.dp))
            .clickable(onClick = onClick)
            .padding(18.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Eyebrow("MODE $number / ${mode.eyebrow}", modifier = Modifier.weight(1f), color = accent)
            Text("↗", color = accent, fontSize = 19.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(13.dp))
        Text(title, color = NovaPalette.text, fontSize = 21.sp, letterSpacing = -0.2.sp, fontWeight = FontWeight.Black)
        Spacer(Modifier.height(7.dp))
        Text(description, color = NovaPalette.muted, fontSize = 12.sp, lineHeight = 18.sp)
        Spacer(Modifier.height(15.dp))
        Eyebrow(rule, color = NovaPalette.soft)
    }
}
