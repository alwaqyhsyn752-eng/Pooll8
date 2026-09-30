package com.example.aimassist.capture

import android.content.Intent
import android.graphics.Bitmap

interface ScreenCapture {
    val isRunning: Boolean
    fun start(resultCode: Int, data: Intent, targetWidth: Int, targetHeight: Int)
    fun setFrameListener(listener: (Bitmap) -> Unit)
    fun stop()
    fun release()
}
