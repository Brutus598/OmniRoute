package com.novadarts.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.novadarts.game.AimPoint
import com.novadarts.game.DartGame
import com.novadarts.game.DartHit
import com.novadarts.game.DartScoring
import com.novadarts.game.GameMode
import com.novadarts.game.MatchState
import com.novadarts.ui.DartBoardCanvas
import com.novadarts.ui.Eyebrow
import com.novadarts.ui.GlassPanel
import com.novadarts.ui.InfoTag
import com.novadarts.ui.NovaPalette
import com.novadarts.ui.PrimaryAction
import com.novadarts.ui.SecondaryAction

@Composable
fun MatchScreen(
    state: MatchState,
    accent: Color,
    onExit: () -> Unit,
    onThrow: (AimPoint) -> Unit,
    onNovaTurn: () -> Unit,
) {
    var aim by remember(state.id) { mutableStateOf<AimPoint?>(null) }
    val preview = aim?.let { DartScoring.resolve(it.x, it.y) }
    val target = if (state.mode == GameMode.AROUND_CLOCK) state.routeTarget else null

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp)
            .padding(top = 13.dp, bottom = 20.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                "×",
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable(onClick = onExit)
                    .padding(horizontal = 10.dp, vertical = 7.dp),
                color = NovaPalette.soft,
                fontSize = 22.sp,
                lineHeight = 22.sp,
                fontWeight = FontWeight.Light,
            )
            Column(modifier = Modifier.weight(1f)) {
                Eyebrow("NOVA / LOCAL MATCH", color = NovaPalette.mint)
                Text(state.mode.title, color = NovaPalette.text, fontSize = 14.sp, fontWeight = FontWeight.Black, letterSpacing = 0.8.sp)
            }
            InfoTag("OFFLINE", tint = NovaPalette.mint)
        }

        Spacer(Modifier.height(12.dp))
        when (state.mode) {
            GameMode.CLASSIC_501 -> Scoreboard(state)
            GameMode.AROUND_CLOCK -> RouteStatus(state)
            GameMode.BULL_RUSH -> BullRushStatus(state)
        }
        Spacer(Modifier.height(13.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Eyebrow(
                    when (state.mode) {
                        GameMode.CLASSIC_501 -> if (state.turnOver) "ZUG BEENDET" else "DEIN ZIEL"
                        GameMode.AROUND_CLOCK -> "NÄCHSTER TREFFER"
                        GameMode.BULL_RUSH -> "FREIE WAHL"
                    },
                    color = NovaPalette.muted,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    when (state.mode) {
                        GameMode.CLASSIC_501 -> if (state.turnOver) "Wurfserie komplett" else "Feld wählen · Pfeil werfen"
                        GameMode.AROUND_CLOCK -> if (state.routeTarget == 25) "BULL" else "SEGMENT ${state.routeTarget}"
                        GameMode.BULL_RUSH -> "JEDER PUNKT ZÄHLT"
                    },
                    color = NovaPalette.text,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
            when (state.mode) {
                GameMode.CLASSIC_501 -> InfoTag("DOUBLE OUT", tint = NovaPalette.lime)
                GameMode.AROUND_CLOCK -> InfoTag(
                    if (state.routeTarget == 25) "BULL / 21" else "${state.routeTarget.toString().padStart(2, '0')} / 20",
                    tint = NovaPalette.mint,
                )
                GameMode.BULL_RUSH -> InfoTag("30 DARTS", tint = NovaPalette.coral)
            }
        }

        Spacer(Modifier.height(5.dp))
        DartBoardCanvas(
            aim = aim,
            highlightedTarget = target,
            accent = accent,
            onAim = { aim = it },
        )

        Row(
            modifier = Modifier.fillMaxWidth().height(35.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (preview != null && aim != null) {
                InfoTag(
                    "${preview.label}  ·  ${preview.points} PTS",
                    tint = when {
                        preview.points == 50 -> NovaPalette.lime
                        preview.points >= 40 -> NovaPalette.mint
                        preview.isMiss -> NovaPalette.coral
                        else -> NovaPalette.soft
                    },
                )
                Spacer(Modifier.size(9.dp))
                Text("ZIEL BESTÄTIGEN", color = NovaPalette.muted, fontSize = 9.sp, letterSpacing = 0.8.sp)
            } else {
                Text(
                    when {
                        state.turnOver && state.mode == GameMode.CLASSIC_501 -> state.status
                        state.mode == GameMode.CLASSIC_501 && state.botTurns > 0 && state.turnHits.isEmpty() -> state.status
                        else -> "BOARD ANTIPPEN, UM DEN PUNKT ZU SETZEN"
                    },
                    color = if (state.wasBust) NovaPalette.coral else NovaPalette.muted,
                    fontSize = 9.sp,
                    letterSpacing = 0.6.sp,
                    fontWeight = if (state.wasBust) FontWeight.Bold else FontWeight.Medium,
                )
            }
        }

        Spacer(Modifier.height(4.dp))
        ThrowHistory(state.turnHits, accent)
        Spacer(Modifier.height(11.dp))

        if (state.finished) {
            ResultPanel(state)
            Spacer(Modifier.height(12.dp))
            PrimaryAction(label = "ZUR ZENTRALE", leading = "↗", onClick = onExit)
        } else {
            val classicHandoff = state.mode == GameMode.CLASSIC_501 && state.turnOver
            val canThrow = aim != null && !classicHandoff
            PrimaryAction(
                label = when {
                    classicHandoff -> "NOVA IST DRAN — WEITER"
                    aim != null -> "PFEIL WERFEN"
                    else -> "ZIEL AUF DEM BOARD WÄHLEN"
                },
                leading = when {
                    classicHandoff -> "◉"
                    aim != null -> "↗"
                    else -> "⌖"
                },
                enabled = classicHandoff || canThrow,
                onClick = {
                    when {
                        classicHandoff -> {
                            aim = null
                            onNovaTurn()
                        }
                        aim != null -> {
                            val selectedAim = aim ?: AimPoint(0f, 0f)
                            aim = null
                            onThrow(selectedAim)
                        }
                    }
                },
            )
            Spacer(Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Eyebrow("LOADOUT / SKILL ONLY", modifier = Modifier.weight(1f))
                Text("${state.totalThrows} WÜRFE", color = NovaPalette.muted, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
            }
        }
    }
}

@Composable
private fun Scoreboard(state: MatchState) {
    Row(horizontalArrangement = Arrangement.spacedBy(9.dp), modifier = Modifier.fillMaxWidth()) {
        ScoreTile("DU / LOCAL", state.playerScore, true, NovaPalette.lime, Modifier.weight(1f))
        ScoreTile("NOVA / CPU", state.botScore, false, NovaPalette.mint, Modifier.weight(1f))
    }
}

@Composable
private fun ScoreTile(name: String, score: Int, active: Boolean, accent: Color, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .height(78.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(if (active) Color(0xFF1A211C) else NovaPalette.panel)
            .border(1.dp, if (active) accent.copy(alpha = 0.42f) else NovaPalette.line, RoundedCornerShape(18.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(6.dp).clip(CircleShape).background(accent))
            Spacer(Modifier.size(7.dp))
            Eyebrow(name, color = if (active) accent else NovaPalette.muted)
        }
        Text(score.toString(), color = NovaPalette.text, fontSize = 29.sp, lineHeight = 30.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace)
    }
}

@Composable
private fun RouteStatus(state: MatchState) {
    val complete = if (state.finished) 21 else if (state.routeTarget == 25) 20 else state.routeTarget - 1
    GlassPanel(shape = RoundedCornerShape(18.dp)) {
        Row(modifier = Modifier.padding(horizontal = 15.dp, vertical = 13.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Eyebrow("ROUTE PROGRESS", color = NovaPalette.mint)
                Spacer(Modifier.height(4.dp))
                Text("${complete.toString().padStart(2, '0')} / 21", color = NovaPalette.text, fontSize = 25.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace)
            }
            Column(horizontalAlignment = Alignment.End) {
                Eyebrow("WURF", color = NovaPalette.muted)
                Spacer(Modifier.height(5.dp))
                Text("${4 - state.dartsLeft} / 3", color = NovaPalette.lime, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun BullRushStatus(state: MatchState) {
    Row(horizontalArrangement = Arrangement.spacedBy(9.dp), modifier = Modifier.fillMaxWidth()) {
        RushTile("SCORE", state.rushScore.toString(), NovaPalette.lime, Modifier.weight(1f))
        RushTile("PFEILE ÜBRIG", state.rushRemaining.toString(), NovaPalette.coral, Modifier.weight(1f))
    }
}

@Composable
private fun RushTile(label: String, value: String, accent: Color, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .height(69.dp)
            .clip(RoundedCornerShape(17.dp))
            .background(NovaPalette.panel)
            .border(1.dp, NovaPalette.line, RoundedCornerShape(17.dp))
            .padding(horizontal = 14.dp, vertical = 9.dp),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Eyebrow(label)
        Text(value, color = accent, fontSize = 23.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace)
    }
}

@Composable
private fun ThrowHistory(hits: List<DartHit>, accent: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(7.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Eyebrow("DARTS", color = NovaPalette.muted)
        Spacer(Modifier.size(2.dp))
        repeat(3) { index ->
            val hit = hits.getOrNull(index)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(31.dp)
                    .clip(RoundedCornerShape(9.dp))
                    .background(if (hit != null) accent.copy(alpha = 0.1f) else NovaPalette.panel)
                    .border(1.dp, if (hit != null) accent.copy(alpha = 0.25f) else NovaPalette.line, RoundedCornerShape(9.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    hit?.let { "${it.label} · ${it.points}" } ?: "—",
                    color = if (hit != null) NovaPalette.text else NovaPalette.muted,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                )
            }
        }
    }
}

@Composable
private fun ResultPanel(state: MatchState) {
    val accent = when {
        state.mode == GameMode.CLASSIC_501 && !state.won -> NovaPalette.coral
        state.mode == GameMode.BULL_RUSH -> NovaPalette.coral
        else -> NovaPalette.lime
    }
    GlassPanel(shape = RoundedCornerShape(21.dp)) {
        Column(modifier = Modifier.fillMaxWidth().padding(17.dp)) {
            Eyebrow(
                when (state.mode) {
                    GameMode.CLASSIC_501 -> if (state.won) "CHECKOUT / CLEAN FINISH" else "MATCH COMPLETE / NOVA WINS"
                    GameMode.AROUND_CLOCK -> "ROUTE COMPLETE / BULL HIT"
                    GameMode.BULL_RUSH -> "SESSION COMPLETE / SCORE LOCKED"
                },
                color = accent,
            )
            Spacer(Modifier.height(7.dp))
            Text(
                when (state.mode) {
                    GameMode.CLASSIC_501 -> if (state.won) "LEG GEWONNEN." else "NOVA HAT GEWONNEN."
                    GameMode.AROUND_CLOCK -> "ROUTE GESCHAFFT."
                    GameMode.BULL_RUSH -> "${state.rushScore} PUNKTE."
                },
                color = NovaPalette.text,
                fontSize = 20.sp,
                lineHeight = 23.sp,
                fontWeight = FontWeight.Black,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                "+${sessionReward(state)} NOVA-MÜNZEN  ·  ${state.totalThrows} WÜRFE  ·  ${state.bullseyes} BULLSEYES",
                color = NovaPalette.muted,
                fontSize = 9.sp,
                letterSpacing = 0.3.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

private fun sessionReward(state: MatchState): Int = when (state.mode) {
    GameMode.CLASSIC_501 -> if (state.won) 90 else 45
    GameMode.AROUND_CLOCK -> 70
    GameMode.BULL_RUSH -> 25 + (state.rushScore / 10).coerceAtMost(100)
}
