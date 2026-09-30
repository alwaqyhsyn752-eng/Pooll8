package com.example.aimassist.domain.model

data class Ball(
    val id: Int,
    val center: Point2D,
    val radius: Float,
    val color: BallColor
) { val isCue: Boolean get() = color == BallColor.CUE }

enum class BallColor { CUE, EIGHT, SOLID, STRIPE, UNKNOWN }
