package com.example.aimassist.cv

import android.graphics.Bitmap
import com.example.aimassist.domain.model.Ball
import com.example.aimassist.domain.model.Table

interface BallDetector {
    suspend fun detect(bitmap: Bitmap): DetectionResult
    fun release()
}

data class DetectionResult(
    val balls: List<Ball>,
    val table: Table? = null,
    val debugOverlay: Bitmap? = null
)
