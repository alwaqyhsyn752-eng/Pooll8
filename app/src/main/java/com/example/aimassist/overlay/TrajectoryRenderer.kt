package com.example.aimassist.overlay

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.Path
import com.example.aimassist.domain.model.LineSegment
import com.example.aimassist.domain.model.Trajectory

class TrajectoryRenderer {

    private val cuePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE; strokeWidth = 5f; color = Color.WHITE
        pathEffect = DashPathEffect(floatArrayOf(20f, 12f), 0f)
    }
    private val targetPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE; strokeWidth = 5f; color = Color.YELLOW
        pathEffect = DashPathEffect(floatArrayOf(20f, 12f), 0f)
    }
    private val deflectPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE; strokeWidth = 3f; color = Color.CYAN
        pathEffect = DashPathEffect(floatArrayOf(12f, 10f), 0f)
    }
    private val ghostPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE; strokeWidth = 3f; color = Color.WHITE; alpha = 150
    }
    private val path = Path()

    fun draw(canvas: Canvas, t: Trajectory) {
        drawSegment(canvas, t.cueLine, cuePaint)
        canvas.drawCircle(t.ghostBall.x, t.ghostBall.y, 15f, ghostPaint)
        t.targetLine?.let { drawSegment(canvas, it, targetPaint) }
        t.cueDeflectionLine?.let { drawSegment(canvas, it, deflectPaint) }
    }

    private fun drawSegment(canvas: Canvas, s: LineSegment, p: Paint) {
        path.reset()
        path.moveTo(s.start.x, s.start.y)
        path.lineTo(s.end.x, s.end.y)
        canvas.drawPath(path, p)
    }
}
