package com.novadarts.game

import kotlin.math.atan2
import kotlin.math.floor
import kotlin.math.sqrt
import kotlin.random.Random

/** Game modes are deliberately self-contained: no account, network or remote match state. */
enum class GameMode(
    val title: String,
    val subtitle: String,
    val eyebrow: String,
) {
    CLASSIC_501("501 DUELL", "Double-out gegen die NOVA-KI", "KLASSIKER"),
    AROUND_CLOCK("AROUND THE CLOCK", "Von 1 bis 20 — dann Bull", "ROUTE"),
    BULL_RUSH("BULL RUSH", "30 Pfeile. Hol den Highscore.", "ARCADE"),
}

data class AimPoint(val x: Float, val y: Float)

data class DartHit(
    val points: Int,
    val label: String,
    val segment: Int? = null,
    val multiplier: Int = 0,
    val isDouble: Boolean = false,
    val isBull: Boolean = false,
    val isMiss: Boolean = false,
)

data class MatchState(
    val id: Long,
    val mode: GameMode,
    val playerScore: Int = 501,
    val botScore: Int = 501,
    val turnStartScore: Int = 501,
    val turnPoints: Int = 0,
    val dartsLeft: Int = 3,
    val turnHits: List<DartHit> = emptyList(),
    val routeTarget: Int = 1,
    val rushRemaining: Int = 30,
    val rushScore: Int = 0,
    val totalThrows: Int = 0,
    val totalPoints: Int = 0,
    val bullseyes: Int = 0,
    val botTurns: Int = 0,
    val turnOver: Boolean = false,
    val finished: Boolean = false,
    val won: Boolean = false,
    val wasBust: Boolean = false,
    val status: String = "Tippe ein Feld auf dem Board",
)

object DartScoring {
    val sectors = intArrayOf(20, 1, 18, 4, 13, 6, 10, 15, 2, 17, 3, 19, 7, 16, 8, 11, 14, 9, 12, 5)

    /** x/y are measured from the board centre, with the board's outer double ring at radius 1. */
    fun resolve(x: Float, y: Float): DartHit {
        val radius = sqrt(x * x + y * y)
        if (radius > 1f) return DartHit(0, "MISS", isMiss = true)
        if (radius <= 0.038f) return DartHit(50, "BULL", 25, 2, isDouble = true, isBull = true)
        if (radius <= 0.095f) return DartHit(25, "25", 25, 1, isBull = true)

        val angle = Math.toDegrees(atan2(y.toDouble(), x.toDouble())).toFloat()
        val normalized = ((angle + 99f) % 360f + 360f) % 360f
        val index = floor(normalized / 18f).toInt().coerceIn(0, sectors.lastIndex)
        val segment = sectors[index]

        return when {
            radius in 0.50f..0.58f -> DartHit(segment * 3, "T$segment", segment, 3)
            radius >= 0.92f -> DartHit(segment * 2, "D$segment", segment, 2, isDouble = true)
            else -> DartHit(segment, "S$segment", segment, 1)
        }
    }
}

object DartGame {
    fun newMatch(mode: GameMode, id: Long = System.currentTimeMillis()): MatchState = when (mode) {
        GameMode.CLASSIC_501 -> MatchState(id = id, mode = mode)
        GameMode.AROUND_CLOCK -> MatchState(
            id = id,
            mode = mode,
            playerScore = 0,
            botScore = 0,
            turnStartScore = 0,
            status = "Triff Segment 1",
        )
        GameMode.BULL_RUSH -> MatchState(
            id = id,
            mode = mode,
            playerScore = 0,
            botScore = 0,
            turnStartScore = 0,
            status = "30 Pfeile — ziele auf den Bull",
        )
    }

    fun throwAt(state: MatchState, point: AimPoint): MatchState {
        if (state.finished || state.turnOver) return state
        val hit = DartScoring.resolve(point.x, point.y)
        return when (state.mode) {
            GameMode.CLASSIC_501 -> throw501(state, hit)
            GameMode.AROUND_CLOCK -> throwAroundClock(state, hit)
            GameMode.BULL_RUSH -> throwBullRush(state, hit)
        }
    }

    private fun throw501(state: MatchState, hit: DartHit): MatchState {
        val projected = state.playerScore - hit.points
        val bust = projected < 0 || projected == 1 || (projected == 0 && !hit.isDouble)
        val hits = (state.turnHits + hit).takeLast(3)
        val bulls = state.bullseyes + if (hit.isBull && hit.points == 50) 1 else 0

        if (bust) {
            return state.copy(
                playerScore = state.turnStartScore,
                turnPoints = 0,
                dartsLeft = 0,
                turnHits = hits,
                totalThrows = state.totalThrows + 1,
                totalPoints = (state.totalPoints - state.turnPoints).coerceAtLeast(0),
                bullseyes = bulls,
                turnOver = true,
                wasBust = true,
                status = "BUST // Zug zurückgesetzt",
            )
        }

        val nextDarts = state.dartsLeft - 1
        val checkout = projected == 0
        return state.copy(
            playerScore = projected,
            turnPoints = state.turnPoints + hit.points,
            dartsLeft = if (checkout) 0 else nextDarts,
            turnHits = hits,
            totalThrows = state.totalThrows + 1,
            totalPoints = state.totalPoints + hit.points,
            bullseyes = bulls,
            turnOver = !checkout && nextDarts <= 0,
            finished = checkout,
            won = checkout,
            wasBust = false,
            status = when {
                checkout -> "CHECKOUT // LEG GEWONNEN"
                nextDarts <= 0 -> "ZUG VOLL // NOVA IST DRAN"
                else -> "${hit.label} // NOCH ${projected}"
            },
        )
    }

    private fun throwAroundClock(state: MatchState, hit: DartHit): MatchState {
        val targetHit = if (state.routeTarget == 25) hit.isBull else hit.segment == state.routeTarget
        val nextDarts = state.dartsLeft - 1
        val target = state.routeTarget
        val routeFinished = targetHit && target == 25
        val nextTarget = when {
            !targetHit -> target
            target == 20 -> 25
            target == 25 -> 25
            else -> target + 1
        }
        val clearedTurn = targetHit || nextDarts <= 0
        val bulls = state.bullseyes + if (hit.isBull && hit.points == 50) 1 else 0

        return state.copy(
            routeTarget = nextTarget,
            dartsLeft = if (clearedTurn) 3 else nextDarts,
            turnHits = if (clearedTurn) emptyList() else (state.turnHits + hit).takeLast(3),
            totalThrows = state.totalThrows + 1,
            totalPoints = state.totalPoints + hit.points,
            bullseyes = bulls,
            finished = routeFinished,
            won = routeFinished,
            status = when {
                routeFinished -> "BULL // ROUTE KOMPLETT"
                targetHit && target == 20 -> "20 GETROFFEN // JETZT BULL"
                targetHit -> "SEGMENT $target // SAUBER"
                nextDarts <= 0 -> "NEUER VERSUCH // ZIEL BLEIBT $target"
                else -> "ZIEL: ${if (target == 25) "BULL" else target}"
            },
        )
    }

    private fun throwBullRush(state: MatchState, hit: DartHit): MatchState {
        val remaining = state.rushRemaining - 1
        val finished = remaining <= 0
        val bulls = state.bullseyes + if (hit.isBull && hit.points == 50) 1 else 0
        val hits = (state.turnHits + hit).takeLast(3)
        return state.copy(
            rushRemaining = remaining.coerceAtLeast(0),
            rushScore = state.rushScore + hit.points,
            turnHits = hits,
            totalThrows = state.totalThrows + 1,
            totalPoints = state.totalPoints + hit.points,
            bullseyes = bulls,
            finished = finished,
            won = finished,
            status = if (finished) "ZEIT // SCORE GESICHERT" else "${hit.label} // ${remaining} PFEILE ÜBRIG",
        )
    }

    /** Resolves the complete offline CPU turn after the player's three darts or a bust. */
    fun novaTurn(state: MatchState, random: Random = Random(state.id + state.botTurns.toLong())): MatchState {
        if (state.mode != GameMode.CLASSIC_501 || !state.turnOver || state.finished) return state

        val turnStart = state.botScore
        var remaining = turnStart
        var turnPoints = 0
        var bust = false
        var checkout = false

        for (dart in 0 until 3) {
            val hit = novaHit(remaining, random)
            val projected = remaining - hit.points
            if (projected < 0 || projected == 1 || (projected == 0 && !hit.isDouble)) {
                bust = true
                break
            }
            remaining = projected
            turnPoints += hit.points
            if (remaining == 0) {
                checkout = true
                break
            }
        }

        val score = if (bust) turnStart else remaining
        return state.copy(
            botScore = score,
            turnStartScore = state.playerScore,
            turnPoints = 0,
            dartsLeft = 3,
            turnHits = emptyList(),
            botTurns = state.botTurns + 1,
            turnOver = false,
            finished = checkout,
            won = false,
            wasBust = false,
            status = when {
                checkout -> "NOVA CHECKOUT // PARTIE VERLOREN"
                bust -> "NOVA // BUST — DU BIST DRAN"
                else -> "NOVA // $turnPoints PUNKTE — DU BIST DRAN"
            },
        )
    }

    private fun novaHit(remaining: Int, random: Random): DartHit {
        if (remaining == 50 && random.nextInt(100) < 28) {
            return DartHit(50, "BULL", 25, 2, isDouble = true, isBull = true)
        }
        if (remaining in 2..40 && remaining % 2 == 0 && random.nextInt(100) < 26) {
            val segment = remaining / 2
            if (segment in 1..20) return DartHit(remaining, "D$segment", segment, 2, isDouble = true)
        }

        return when (random.nextInt(100)) {
            in 0..31 -> DartHit(60, "T20", 20, 3)
            in 32..51 -> DartHit(40, "D20", 20, 2, isDouble = true)
            in 52..79 -> DartHit(20, "S20", 20, 1)
            in 80..88 -> DartHit(5, "S5", 5, 1)
            in 89..95 -> DartHit(1, "S1", 1, 1)
            else -> DartHit(0, "MISS", isMiss = true)
        }
    }
}
