package com.example.aimassist.cv

import android.graphics.Bitmap
import com.example.aimassist.domain.model.Pocket
import com.example.aimassist.domain.model.Point2D
import com.example.aimassist.domain.model.Rect2D
import com.example.aimassist.domain.model.Table
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TableMapper @Inject constructor() {

    fun map(bitmap: Bitmap, detectedBalls: List<com.example.aimassist.domain.model.Ball>): Table? {
        val w = bitmap.width.toFloat()
        val h = bitmap.height.toFloat()
        val marginX = w * 0.05f
        val marginY = h * 0.15f
        val bounds = Rect2D(marginX, marginY, w - marginX, h - marginY)
        val pr = w * 0.025f
        val pockets = listOf(
            Pocket(Point2D(bounds.left, bounds.top), pr),
            Pocket(Point2D(bounds.center.x, bounds.top), pr),
            Pocket(Point2D(bounds.right, bounds.top), pr),
            Pocket(Point2D(bounds.left, bounds.bottom), pr),
            Pocket(Point2D(bounds.center.x, bounds.bottom), pr),
            Pocket(Point2D(bounds.right, bounds.bottom), pr)
        )
        return Table(bounds, pockets)
    }
}
