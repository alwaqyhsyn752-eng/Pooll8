package com.example.aimassist.physics

import com.example.aimassist.domain.model.Ball
import com.example.aimassist.domain.model.Point2D
import com.example.aimassist.domain.model.Rect2D
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.sqrt

@Singleton
class CollisionDetector @Inject constructor() {

    fun findBallCollision(cue: Ball, target: Ball, dir: Vector2D): Point2D? {
        val d = dir.normalized()
        val f = Vector2D.fromPoints(target.center, cue.center)
        val r = cue.radius + target.radius
        val a = d.dot(d)
        val b = 2f * f.dot(d)
        val c = f.dot(f) - r * r
        val disc = b * b - 4f * a * c
        if (disc < 0f) return null
        val sq = sqrt(disc)
        val t1 = (-b - sq) / (2f * a)
        val t2 = (-b + sq) / (2f * a)
        val t = when { t1 > 0f -> t1; t2 > 0f -> t2; else -> return null }
        return Point2D(cue.center.x + d.x * t, cue.center.y + d.y * t)
    }

    fun findCushionCollision(cue: Ball, dir: Vector2D, bounds: Rect2D): Point2D? {
        val d = dir.normalized()
        var bestT = Float.MAX_VALUE
        var hit: Point2D? = null
        if (d.x < 0f) { val t = (bounds.left + cue.radius - cue.center.x) / d.x
            if (t > 0f && t < bestT) { bestT = t; hit = Point2D(cue.center.x + d.x * t, cue.center.y + d.y * t) } }
        if (d.x > 0f) { val t = (bounds.right - cue.radius - cue.center.x) / d.x
            if (t > 0f && t < bestT) { bestT = t; hit = Point2D(cue.center.x + d.x * t, cue.center.y + d.y * t) } }
        if (d.y < 0f) { val t = (bounds.top + cue.radius - cue.center.y) / d.y
            if (t > 0f && t < bestT) { bestT = t; hit = Point2D(cue.center.x + d.x * t, cue.center.y + d.y * t) } }
        if (d.y > 0f) { val t = (bounds.bottom - cue.radius - cue.center.y) / d.y
            if (t > 0f && t < bestT) { bestT = t; hit = Point2D(cue.center.x + d.x * t, cue.center.y + d.y * t) } }
        return hit
    }
}
