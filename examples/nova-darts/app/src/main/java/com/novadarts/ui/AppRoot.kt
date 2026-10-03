package com.novadarts.ui

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.hapticfeedback.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import com.novadarts.data.PlayerProgress
import com.novadarts.data.ProgressStore
import com.novadarts.game.AimPoint
import com.novadarts.game.DartGame
import com.novadarts.game.GameMode
import com.novadarts.game.MatchState
import com.novadarts.ui.screens.GearScreen
import com.novadarts.ui.screens.HomeScreen
import com.novadarts.ui.screens.MatchScreen
import com.novadarts.ui.screens.PlayScreen
import com.novadarts.ui.screens.StatsScreen

private enum class HubTab(val label: String, val glyph: String) {
    HOME("HOME", "⌂"),
    PLAY("SPIELEN", "◉"),
    GEAR("GEAR LAB", "✣"),
    STATS("STATS", "▤"),
}

@Composable
fun NovaDartsApp() {
    val context = LocalContext.current
    val store = remember { ProgressStore(context) }
    val haptics = LocalHapticFeedback.current
    var progress by remember { mutableStateOf(store.load()) }
    var selectedTab by remember { mutableStateOf(HubTab.HOME) }
    var match by remember { mutableStateOf<MatchState?>(null) }
    var rewardedSessionId by remember { mutableStateOf<Long?>(null) }

    fun launch(mode: GameMode) {
        rewardedSessionId = null
        match = DartGame.newMatch(mode)
    }

    fun acceptSession(updated: MatchState) {
        match = updated
        if (updated.finished && rewardedSessionId != updated.id) {
            progress = store.completeSession(progress, updated)
            rewardedSessionId = updated.id
        }
    }

    BackHandler(enabled = match != null) {
        match = null
        selectedTab = HubTab.HOME
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NovaPalette.ink)
            .windowInsetsPadding(WindowInsets.safeDrawing),
    ) {
        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            val active = match
            if (active != null) {
                MatchScreen(
                    state = active,
                    accent = loadoutAccent(progress),
                    onExit = {
                        match = null
                        selectedTab = HubTab.HOME
                    },
                    onThrow = { aim: AimPoint ->
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        acceptSession(DartGame.throwAt(active, aim))
                    },
                    onNovaTurn = {
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        acceptSession(DartGame.novaTurn(active))
                    },
                )
            } else {
                when (selectedTab) {
                    HubTab.HOME -> HomeScreen(
                        progress = progress,
                        onStart = ::launch,
                        onOpenPlay = { selectedTab = HubTab.PLAY },
                        onOpenGear = { selectedTab = HubTab.GEAR },
                    )
                    HubTab.PLAY -> PlayScreen(onStart = ::launch)
                    HubTab.GEAR -> GearScreen(
                        progress = progress,
                        onEquipOrUnlock = { itemId -> progress = store.equipOrUnlock(progress, itemId) },
                    )
                    HubTab.STATS -> StatsScreen(progress)
                }
            }
        }
        if (match == null) {
            HubNavigation(selected = selectedTab, onSelect = { selectedTab = it })
        }
    }
}

private fun loadoutAccent(progress: PlayerProgress) =
    com.novadarts.data.GearCatalog.item(progress.equipped[com.novadarts.data.GearSlot.FLIGHT])
        ?.accentArgb
        ?.let { androidx.compose.ui.graphics.Color(it) }
        ?: NovaPalette.lime

@Composable
private fun HubNavigation(selected: HubTab, onSelect: (HubTab) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(NovaPalette.panel)
            .border(width = 1.dp, color = NovaPalette.line)
            .padding(horizontal = 9.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        HubTab.entries.forEach { tab ->
            val active = tab == selected
            Column(
                modifier = Modifier
                    .weight(1f)
                    .height(55.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (active) NovaPalette.lime.copy(alpha = 0.08f) else NovaPalette.panel)
                    .clickable(onClick = { onSelect(tab) })
                    .padding(vertical = 5.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(tab.glyph, color = if (active) NovaPalette.lime else NovaPalette.muted, fontSize = 18.sp, lineHeight = 19.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(2.dp))
                Text(
                    tab.label,
                    color = if (active) NovaPalette.lime else NovaPalette.muted,
                    fontSize = 8.sp,
                    letterSpacing = 0.75.sp,
                    fontWeight = if (active) FontWeight.Black else FontWeight.Medium,
                )
            }
        }
    }
}
