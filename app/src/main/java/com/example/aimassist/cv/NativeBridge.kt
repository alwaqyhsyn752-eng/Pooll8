package com.example.aimassist.cv

import android.graphics.Bitmap
import com.example.aimassist.domain.model.Ball
import com.example.aimassist.domain.model.BallColor
import com.example.aimassist.domain.model.Point2D
import java.nio.ByteBuffer
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NativeBridge @Inject constructor() {

    companion object {
        init { System.loadLibrary("native-lib") }
    }

    private external fun nativeDetectBalls(
        buffer: ByteBuffer, width: Int, height: Int,
        rowStride: Int, minRadius: Int, maxRadius: Int
    ): FloatArray

    fun detect(bitmap: Bitmap): List<Ball> {
        val width = bitmap.width
        val height = bitmap.height
        val buffer = ByteBuffer.allocateDirect(bitmap.byteCount)
        bitmap.copyPixelsToBuffer(buffer)
        buffer.rewind()

        val raw = nativeDetectBalls(buffer, width, height, width * 4, 8, 40)

        val out = ArrayList<Ball>(raw.size / 4)
        var i = 0; var id = 0
        while (i + 3 < raw.size) {
            val r = raw[i + 2]
            if (r > 0f) {
                out.add(
                    Ball(
                        id = id++,
                        center = Point2D(raw[i], raw[i + 1]),
                        radius = r,
                        color = BallColor.entries.getOrElse(raw[i + 3].toInt()) { BallColor.UNKNOWN }
                    )
                )
            }
            i += 4
        }
        return out
    }
}
