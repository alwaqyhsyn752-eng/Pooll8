package com.example.aimassist.overlay

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import com.example.aimassist.domain.model.Trajectory

class OverlayView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    private val renderer = TrajectoryRenderer()

    var trajectory: Trajectory? = null
        set(value) { field = value; invalidate() }

    init {
        setWillNotDraw(false)
        setLayerType(LAYER_TYPE_HARDWARE, null)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        trajectory?.let { renderer.draw(canvas, it) }
    }
}
