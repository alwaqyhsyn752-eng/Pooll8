package com.example.aimassist.domain.model

import kotlin.math.hypot

data class Point2D(val x: Float, val y: Float) {
    fun distanceTo(other: Point2D): Float = hypot(x - other.x, y - other.y)
    operator fun plus(other: Point2D) = Point2D(x + other.x, y + other.y)
    operator fun minus(other: Point2D) = Point2D(x - other.x, y - other.y)
    operator fun times(scalar: Float) = Point2D(x * scalar, y * scalar)
    fun magnitude(): Float = hypot(x, y)
    fun normalized(): Point2D {
        val m = magnitude()
        return if (m < 1e-6f) Point2D(0f, 0f) else Point2D(x / m, y / m)
    }
}
