package com.novadarts.game

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.cos
import kotlin.math.sin

class DartGameTest {
    @Test
    fun boardScoringUsesStandardBullTripleAndDoubleZones() {
        assertEquals(50, DartScoring.resolve(0f, 0f).points)
        assertEquals(25, DartScoring.resolve(0.07f, 0f).points)
        assertEquals(60, DartScoring.resolve(0f, -0.55f).points)
        assertEquals(40, DartScoring.resolve(0f, -0.96f).points)
        assertEquals(6, DartScoring.resolve(0.75f, 0f).points)
        assertEquals(0, DartScoring.resolve(1.1f, 0f).points)
    }

    @Test
    fun classicCheckoutRequiresADouble() {
        val match = DartGame.newMatch(GameMode.CLASSIC_501, id = 1L).copy(
            playerScore = 40,
            turnStartScore = 40,
        )

        val after = DartGame.throwAt(match, AimPoint(0f, -0.96f))

        assertEquals(0, after.playerScore)
        assertTrue(after.finished)
        assertTrue(after.won)
    }

    @Test
    fun classicBustResetsTheWholeVisitAndHandsTurnToCpu() {
        val match = DartGame.newMatch(GameMode.CLASSIC_501, id = 2L).copy(
            playerScore = 10,
            turnStartScore = 10,
        )

        val after = DartGame.throwAt(match, AimPoint(0f, -0.55f))

        assertEquals(10, after.playerScore)
        assertEquals(0, after.turnPoints)
        assertTrue(after.turnOver)
        assertTrue(after.wasBust)
        assertFalse(after.finished)
    }

    @Test
    fun aroundClockAdvancesOnlyAfterTheCurrentSegmentIsHit() {
        val start = DartGame.newMatch(GameMode.AROUND_CLOCK, id = 3L)
        val angle = Math.toRadians(-72.0)
        val hitOne = DartGame.throwAt(
            start,
            AimPoint((cos(angle) * 0.7).toFloat(), (sin(angle) * 0.7).toFloat()),
        )
        val miss = DartGame.throwAt(start, AimPoint(0f, 0f))

        assertEquals(2, hitOne.routeTarget)
        assertEquals(3, hitOne.dartsLeft)
        assertEquals(1, miss.routeTarget)
        assertEquals(2, miss.dartsLeft)
    }

    @Test
    fun bullRushEndsAfterExactlyThirtyDarts() {
        var match = DartGame.newMatch(GameMode.BULL_RUSH, id = 4L)
        repeat(30) { match = DartGame.throwAt(match, AimPoint(0f, 0f)) }

        assertTrue(match.finished)
        assertTrue(match.won)
        assertEquals(0, match.rushRemaining)
        assertEquals(30, match.totalThrows)
        assertEquals(1500, match.rushScore)
    }
}
