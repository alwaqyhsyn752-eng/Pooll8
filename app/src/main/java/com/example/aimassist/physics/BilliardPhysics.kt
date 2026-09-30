package com.example.aimassist.physics

import com.example.aimassist.domain.model.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BilliardPhysics @Inject constructor(
    private val collisionDetector: CollisionDetector
) : TrajectoryCalculator {

    override fun compute(cue: Ball, target: Ball, table: Table): Trajectory {
        val direction = Vector2D.fromPoints(cue.center, target.center).normalized()
        val ghost = collisionDetector.findBallCollision(cue, target, direction)
            ?: target.center.minus(direction.toPoint().times(cue.radius + target.radius))

        val cueLine = LineSegment(cue.center, ghost)
        val targetDir = Vector2D.fromPoints(ghost, target.center).normalized()
        val targetEnd = Point2D(target.center.x + targetDir.x * 300f, target.center.y + targetDir.y * 300f)
        val targetLine = LineSegment(target.center, targetEnd)

        val tangent = Vector2D(-targetDir.y, targetDir.x).normalized()
        val deflectEnd = Point2D(ghost.x + tangent.x * 150f, ghost.y + tangent.y * 150f)
        val cueDeflection = LineSegment(ghost, deflectEnd)

        return Trajectory(cueLine, ghost, targetLine, cueDeflection)
    }
}
