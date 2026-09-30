package com.example.aimassist.domain.model

data class Table(
    val bounds: Rect2D,
    val pockets: List<Pocket>,
    val cushionWidth: Float = 0f
)

data class Rect2D(val left: Float, val top: Float, val right: Float, val bottom: Float) {
    val width: Float get() = right - left
    val height: Float get() = bottom - top
    val center: Point2D get() = Point2D((left + right) / 2f, (top + bottom) / 2f)
}

data class Pocket(val center: Point2D, val radius: Float)
