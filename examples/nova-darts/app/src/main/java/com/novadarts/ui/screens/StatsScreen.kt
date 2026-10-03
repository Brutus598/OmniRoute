package com.novadarts.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.novadarts.data.GearCatalog
import com.novadarts.data.PlayerProgress
import com.novadarts.ui.Eyebrow
import com.novadarts.ui.GlassPanel
import com.novadarts.ui.MetricTile
import com.novadarts.ui.NovaPalette
import com.novadarts.ui.RatingBar
import com.novadarts.ui.SectionTitle

@Composable
fun StatsScreen(progress: PlayerProgress) {
    val level = (1 + progress.matches / 5).coerceAtMost(20)
    val matchesToNext = if (level >= 20) 0 else 5 - progress.matches % 5
    val avgThreeDart = if (progress.totalDarts == 0) 0 else progress.scoredPoints * 3 / progress.totalDarts
    val winRate = if (progress.matches == 0) 0 else progress.wins * 100 / progress.matches
    val ratings = GearCatalog.ratings(progress)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(top = 18.dp, bottom = 26.dp),
    ) {
        Eyebrow("NOVA / PERFORMANCE", color = NovaPalette.mint)
        Spacer(Modifier.height(8.dp))
        Text("DEIN GAME.\nDEINE DATEN.", color = NovaPalette.text, fontSize = 31.sp, lineHeight = 34.sp, fontWeight = FontWeight.Black)
        Spacer(Modifier.height(8.dp))
        Text("Alle Werte bleiben lokal auf deinem Handy.", color = NovaPalette.muted, fontSize = 12.sp)
        Spacer(Modifier.height(22.dp))

        GlassPanel(shape = RoundedCornerShape(22.dp)) {
            Column(modifier = Modifier.padding(17.dp)) {
                Row {
                    Column(modifier = Modifier.weight(1f)) {
                        Eyebrow("PLAYER LEVEL", color = NovaPalette.mint)
                        Spacer(Modifier.height(5.dp))
                        Text("${level.toString().padStart(2, '0')}", color = NovaPalette.text, fontSize = 36.sp, lineHeight = 39.sp, fontWeight = FontWeight.Black)
                    }
                    Column {
                        Eyebrow("BULL RUSH BEST", color = NovaPalette.muted)
                        Spacer(Modifier.height(8.dp))
                        Text("${progress.bullRushRecord}", color = NovaPalette.lime, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(Modifier.height(10.dp))
                BoxProgress(levelProgress = if (level >= 20) 1f else (5 - matchesToNext) / 5f)
                Spacer(Modifier.height(7.dp))
                Text(
                    if (level >= 20) "MAX LEVEL ERREICHT" else "$matchesToNext MATCHES BIS LEVEL ${level + 1}",
                    color = NovaPalette.muted,
                    fontSize = 9.sp,
                    letterSpacing = 1f,
                    fontWeight = FontWeight.Bold,
                )
            }
        }

        Spacer(Modifier.height(23.dp))
        SectionTitle("MATCH ARCHIVE")
        Spacer(Modifier.height(11.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            MetricTile("MATCHES", progress.matches.toString(), "lokal gespielt", modifier = Modifier.weight(1f))
            MetricTile("SIEGE", progress.wins.toString(), "$winRate% win rate", accent = NovaPalette.mint, modifier = Modifier.weight(1f))
        }
        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            MetricTile("3-DART AVG", avgThreeDart.toString(), "Punkte pro Aufnahme", accent = NovaPalette.violet, modifier = Modifier.weight(1f))
            MetricTile("BULLSEYES", progress.bullseyes.toString(), "50-Punkte-Treffer", accent = NovaPalette.coral, modifier = Modifier.weight(1f))
        }
        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            MetricTile(
                "BEST 501 LEG",
                if (progress.best501Darts == 0) "—" else "${progress.best501Darts}",
                "Darts bis Checkout",
                accent = NovaPalette.lime,
                modifier = Modifier.weight(1f),
            )
            MetricTile("ROUTEN", progress.routesCompleted.toString(), "Around the Clock", accent = NovaPalette.mint, modifier = Modifier.weight(1f))
        }

        Spacer(Modifier.height(23.dp))
        SectionTitle("LOADOUT / READOUT")
        Spacer(Modifier.height(11.dp))
        GlassPanel(shape = RoundedCornerShape(20.dp)) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(15.dp)) {
                RatingBar("PRECISION", ratings.precision, NovaPalette.lime)
                RatingBar("STABILITY", ratings.stability, NovaPalette.mint)
                RatingBar("GRIP", ratings.grip, NovaPalette.violet)
            }
        }
        Spacer(Modifier.height(12.dp))
        Text(
            "Loadout-Werte sind kosmetisch. Die Trefferwertung bleibt fair und hängt nur von deinem Ziel ab.",
            color = NovaPalette.muted,
            fontSize = 10.sp,
            lineHeight = 15.sp,
        )
    }
}

@Composable
private fun BoxProgress(levelProgress: Float) {
    androidx.compose.foundation.layout.Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(6.dp)
            .background(NovaPalette.line, RoundedCornerShape(5.dp)),
    ) {
        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .fillMaxWidth(levelProgress.coerceIn(0f, 1f))
                .height(6.dp)
                .background(NovaPalette.lime, RoundedCornerShape(5.dp)),
        )
    }
}
