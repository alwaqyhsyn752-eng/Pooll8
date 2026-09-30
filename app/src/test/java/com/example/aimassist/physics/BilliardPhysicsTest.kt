package com.example.aimassist.physics

import com.example.aimassist.domain.model.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class BilliardPhysicsTest {
    private lateinit var physics: BilliardPhysics

    @Before fun setup() { physics = BilliardPhysics(CollisionDetector()) }

    @Test fun `straight shot produces ghost in front of target`() {
        val cue = Ball(0, Point2D(100f, 500f), 15f, BallColor.CUE)
        val target = Ball(1, Point2D(500f, 500f), 15f, BallColor.SOLID)
        val table = Table(Rect2D(0f, 0f, 1000f, 1000f), emptyList())

        val traj = physics.compute(cue, target, table)
        assertEquals(30f, traj.ghostBall.distanceTo(target.center), 1f)
        assertEquals(500f, traj.ghostBall.y, 1f)
    }

    @Test fun `no collision when target behind cue`() {
        val cue = Ball(0, Point2D(500f, 500f), 15f, BallColor.CUE)
        val target = Ball(1, Point2D(100f, 500f), 15f, BallColor.SOLID)
        assertNull(CollisionDetector().findBallCollision(cue, target, Vector2D(1f, 0f)))
    }
}
