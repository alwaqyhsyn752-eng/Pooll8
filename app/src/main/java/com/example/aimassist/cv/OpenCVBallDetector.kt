package com.example.aimassist.cv

import android.graphics.Bitmap
import com.example.aimassist.capture.FrameBufferPool
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OpenCVBallDetector @Inject constructor(
    private val nativeBridge: NativeBridge,
    private val bufferPool: FrameBufferPool
) : BallDetector {

    override suspend fun detect(bitmap: Bitmap): DetectionResult =
        withContext(Dispatchers.Default) {
            DetectionResult(balls = nativeBridge.detect(bitmap))
        }

    override fun release() = bufferPool.clear()
}
