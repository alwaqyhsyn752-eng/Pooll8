package com.example.aimassist.physics

import com.example.aimassist.domain.model.Point2D
import kotlin.math.sqrt

data class Vector2D(val x: Float, val y: Float) {
    operator fun plus(o: Vector2D) = Vector2D(x + o.x, y + o.y)
    operator fun minus(o: Vector2D) = Vector2D(x - o.x, y - o.y)
    operator fun times(s: Float) = Vector2D(x * s, y * s)
    fun dot(o: Vector2D) = x * o.x + y * o.y
    fun magnitude() = sqrt(x * x + y * y)
    fun normalized(): Vector2D {
        val m = magnitude()
        return if (m < 1e-6f) Vector2D(0f, 0f) else Vector2D(x / m, y / m)
    }
    fun reflect(n: Vector2D): Vector2D {
        val nn = n.normalized()
        return this - nn * (2f * this.dot(nn))
    }
    fun toPoint() = Point2D(x, y)
    companion object {
        fun fromPoints(a: Point2D, b: Point2D) = Vector2D(b.x - a.x, b.y - a.y)
        val ZERO = Vector2D(0f, 0f)
    }
}
