package com.example.aimassist.capture

import android.graphics.Bitmap
import java.util.concurrent.ConcurrentLinkedQueue
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FrameBufferPool @Inject constructor() {
    private val pool = ConcurrentLinkedQueue<Bitmap>()
    private val maxSize = 4

    fun acquire(width: Int, height: Int): Bitmap {
        val recycled = pool.poll()
        return if (recycled != null && recycled.width == width && recycled.height == height) recycled
        else { recycled?.recycle(); Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888) }
    }

    fun release(bitmap: Bitmap) {
        if (pool.size < maxSize && !bitmap.isRecycled) pool.offer(bitmap) else bitmap.recycle()
    }

    fun clear() { while (pool.isNotEmpty()) pool.poll()?.recycle() }
}
