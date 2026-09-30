package com.example.aimassist.domain.model

data class Trajectory(
    val cueLine: LineSegment,
    val ghostBall: Point2D,
    val targetLine: LineSegment?,
    val cueDeflectionLine: LineSegment?
)

data class LineSegment(val start: Point2D, val end: Point2D)
