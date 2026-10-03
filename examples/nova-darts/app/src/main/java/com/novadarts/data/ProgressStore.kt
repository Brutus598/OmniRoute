package com.novadarts.data

import android.content.Context
import android.content.SharedPreferences
import com.novadarts.game.GameMode
import com.novadarts.game.MatchState

/** All catalog entries are original text/vector designs. Costs are earned in offline play. */
enum class GearSlot(val title: String, val shortTitle: String) {
    BARREL("KÖRPER", "01"),
    SHAFT("SCHAFT", "02"),
    FLIGHT("FLIGHT", "03"),
    TIP("SPITZE", "04"),
    ACCESSORY("ZUBEHÖR", "+"),
}

data class GearItem(
    val id: String,
    val slot: GearSlot,
    val name: String,
    val detail: String,
    val cost: Int,
    val precision: Int,
    val stability: Int,
    val grip: Int,
    val accentArgb: Long,
)

data class SetupRatings(val precision: Int, val stability: Int, val grip: Int)

data class PlayerProgress(
    val coins: Int = 240,
    val matches: Int = 0,
    val wins: Int = 0,
    val totalDarts: Int = 0,
    val bullseyes: Int = 0,
    val scoredPoints: Int = 0,
    val routesCompleted: Int = 0,
    val bullRushRecord: Int = 0,
    val best501Darts: Int = 0,
    val ownedGear: Set<String> = emptySet(),
    val equipped: Map<GearSlot, String> = emptyMap(),
)

object GearCatalog {
    val items = listOf(
        GearItem("barrel_basalt", GearSlot.BARREL, "BASALT 90%", "24 g · Ringgrip · Tungsten", 0, 11, 10, 9, 0xFFD7FF59),
        GearItem("barrel_hex", GearSlot.BARREL, "HEX / MILLED", "22 g · Präzisionsfräsung", 140, 14, 9, 11, 0xFF69E2CB),
        GearItem("barrel_prism", GearSlot.BARREL, "PRISM TITAN", "23 g · Doppelter Grip", 220, 12, 14, 12, 0xFF9C8CFF),
        GearItem("barrel_nova", GearSlot.BARREL, "NOVA CNC PRO", "24 g · Tour-Level · Black", 340, 16, 14, 15, 0xFFFF8B67),

        GearItem("shaft_micro", GearSlot.SHAFT, "MICRO 35", "35 mm · Short · Nylon", 0, 8, 10, 8, 0xFFD7FF59),
        GearItem("shaft_aero", GearSlot.SHAFT, "AERO ALLOY", "32 mm · Aluminium", 90, 9, 13, 10, 0xFF69E2CB),
        GearItem("shaft_carbon", GearSlot.SHAFT, "CARBON FLEX", "38 mm · Carbonfaser", 160, 12, 15, 9, 0xFF9C8CFF),
        GearItem("shaft_vortex", GearSlot.SHAFT, "VORTEX SHORT", "28 mm · Flight-Lock", 210, 11, 17, 12, 0xFFFF8B67),

        GearItem("flight_vector", GearSlot.FLIGHT, "VECTOR 100", "Standard · 100 Micron", 0, 9, 11, 8, 0xFFD7FF59),
        GearItem("flight_slim", GearSlot.FLIGHT, "SLIM / NO. 6", "Schmal · schneller Release", 70, 12, 8, 10, 0xFF69E2CB),
        GearItem("flight_vortex", GearSlot.FLIGHT, "VORTEX 3D", "Geprägt · Extra-Stabilität", 140, 10, 16, 11, 0xFF9C8CFF),
        GearItem("flight_ghost", GearSlot.FLIGHT, "GHOST HOLO", "Holo-Finish · 75 Micron", 230, 15, 12, 12, 0xFFFF8B67),

        GearItem("tip_steel", GearSlot.TIP, "STEEL 26", "26 mm · Smooth point", 0, 9, 10, 8, 0xFFD7FF59),
        GearItem("tip_black", GearSlot.TIP, "BLACK GRIP", "30 mm · Nano-Grip", 80, 11, 12, 12, 0xFF69E2CB),
        GearItem("tip_titan", GearSlot.TIP, "TITAN PRO 32", "32 mm · Titan-Legierung", 170, 15, 13, 10, 0xFF9C8CFF),
        GearItem("tip_micro", GearSlot.TIP, "MICRO CONE", "24 mm · Slim entry", 220, 13, 11, 15, 0xFFFF8B67),

        GearItem("extra_case", GearSlot.ACCESSORY, "NOVA CASE", "Hardcase · 3-Dart Dock", 0, 8, 9, 10, 0xFFD7FF59),
        GearItem("extra_oche", GearSlot.ACCESSORY, "OCHEN MATTE", "Throwline · Soft-touch", 110, 10, 13, 12, 0xFF69E2CB),
        GearItem("extra_led", GearSlot.ACCESSORY, "HALO RING", "Board-Licht · Mint glow", 190, 12, 14, 11, 0xFF9C8CFF),
        GearItem("extra_wrist", GearSlot.ACCESSORY, "GRIP WRAP", "Wristband · Dry-touch", 250, 13, 11, 16, 0xFFFF8B67),
    )

    val initialOwned: Set<String> = GearSlot.entries.map { defaultFor(it).id }.toSet()

    fun forSlot(slot: GearSlot): List<GearItem> = items.filter { it.slot == slot }

    fun item(id: String?): GearItem? = items.firstOrNull { it.id == id }

    fun defaultFor(slot: GearSlot): GearItem = items.first { it.slot == slot }

    fun equippedItems(progress: PlayerProgress): List<GearItem> = GearSlot.entries.map { slot ->
        item(progress.equipped[slot]) ?: defaultFor(slot)
    }

    fun ratings(progress: PlayerProgress): SetupRatings {
        val setup = equippedItems(progress)
        return SetupRatings(
            precision = (38 + setup.sumOf { it.precision } / 2).coerceIn(1, 99),
            stability = (35 + setup.sumOf { it.stability } / 2).coerceIn(1, 99),
            grip = (36 + setup.sumOf { it.grip } / 2).coerceIn(1, 99),
        )
    }
}

/** Small SharedPreferences store: no cloud backup service, analytics or network sync is used. */
class ProgressStore(context: Context) {
    private val preferences: SharedPreferences =
        context.applicationContext.getSharedPreferences("nova_darts_local", Context.MODE_PRIVATE)

    fun load(): PlayerProgress {
        val seeded = preferences.getBoolean(KEY_SEEDED, false)
        val owned = (preferences.getStringSet(KEY_OWNED, null)?.toSet() ?: GearCatalog.initialOwned)
            .plus(GearCatalog.initialOwned)
        val equipped = GearSlot.entries.associateWith { slot ->
            val stored = preferences.getString("gear_${slot.name}", null)
            val valid = GearCatalog.item(stored)?.takeIf { it.slot == slot && it.id in owned }
            (valid ?: GearCatalog.defaultFor(slot)).id
        }
        return PlayerProgress(
            coins = preferences.getInt(KEY_COINS, if (seeded) 0 else 240),
            matches = preferences.getInt(KEY_MATCHES, 0),
            wins = preferences.getInt(KEY_WINS, 0),
            totalDarts = preferences.getInt(KEY_THROWS, 0),
            bullseyes = preferences.getInt(KEY_BULLS, 0),
            scoredPoints = preferences.getInt(KEY_POINTS, 0),
            routesCompleted = preferences.getInt(KEY_ROUTES, 0),
            bullRushRecord = preferences.getInt(KEY_BULL_RUSH, 0),
            best501Darts = preferences.getInt(KEY_BEST_501, 0),
            ownedGear = owned,
            equipped = equipped,
        ).also { if (!seeded) save(it, seeded = true) }
    }

    fun equipOrUnlock(progress: PlayerProgress, itemId: String): PlayerProgress {
        val item = GearCatalog.item(itemId) ?: return progress
        val alreadyOwned = item.id in progress.ownedGear
        if (!alreadyOwned && progress.coins < item.cost) return progress

        val updated = progress.copy(
            coins = if (alreadyOwned) progress.coins else progress.coins - item.cost,
            ownedGear = progress.ownedGear + item.id,
            equipped = progress.equipped + (item.slot to item.id),
        )
        save(updated)
        return updated
    }

    fun completeSession(progress: PlayerProgress, session: MatchState): PlayerProgress {
        val reward = when (session.mode) {
            GameMode.CLASSIC_501 -> if (session.won) 90 else 45
            GameMode.AROUND_CLOCK -> 70
            GameMode.BULL_RUSH -> 25 + (session.rushScore / 10).coerceAtMost(100)
        }
        val next = progress.copy(
            coins = progress.coins + reward,
            matches = progress.matches + 1,
            wins = progress.wins + if (session.mode == GameMode.CLASSIC_501 && session.won) 1 else 0,
            totalDarts = progress.totalDarts + session.totalThrows,
            bullseyes = progress.bullseyes + session.bullseyes,
            scoredPoints = progress.scoredPoints + session.totalPoints,
            routesCompleted = progress.routesCompleted +
                if (session.mode == GameMode.AROUND_CLOCK && session.won) 1 else 0,
            bullRushRecord = if (session.mode == GameMode.BULL_RUSH) {
                maxOf(progress.bullRushRecord, session.rushScore)
            } else {
                progress.bullRushRecord
            },
            best501Darts = if (session.mode == GameMode.CLASSIC_501 && session.won) {
                if (progress.best501Darts == 0) session.totalThrows
                else minOf(progress.best501Darts, session.totalThrows)
            } else {
                progress.best501Darts
            },
        )
        save(next)
        return next
    }

    private fun save(progress: PlayerProgress, seeded: Boolean = true) {
        preferences.edit()
            .putBoolean(KEY_SEEDED, seeded)
            .putInt(KEY_COINS, progress.coins)
            .putInt(KEY_MATCHES, progress.matches)
            .putInt(KEY_WINS, progress.wins)
            .putInt(KEY_THROWS, progress.totalDarts)
            .putInt(KEY_BULLS, progress.bullseyes)
            .putInt(KEY_POINTS, progress.scoredPoints)
            .putInt(KEY_ROUTES, progress.routesCompleted)
            .putInt(KEY_BULL_RUSH, progress.bullRushRecord)
            .putInt(KEY_BEST_501, progress.best501Darts)
            .putStringSet(KEY_OWNED, progress.ownedGear.toSet())
            .also { editor ->
                progress.equipped.forEach { (slot, id) -> editor.putString("gear_${slot.name}", id) }
            }
            .apply()
    }

    private companion object {
        const val KEY_SEEDED = "seeded"
        const val KEY_COINS = "coins"
        const val KEY_MATCHES = "matches"
        const val KEY_WINS = "wins"
        const val KEY_THROWS = "throws"
        const val KEY_BULLS = "bulls"
        const val KEY_POINTS = "points"
        const val KEY_ROUTES = "routes"
        const val KEY_BULL_RUSH = "bull_rush_record"
        const val KEY_BEST_501 = "best_501_darts"
        const val KEY_OWNED = "owned_gear"
    }
}
