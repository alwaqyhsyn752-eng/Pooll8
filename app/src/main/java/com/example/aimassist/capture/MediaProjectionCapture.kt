package com.example.aimassist.capture

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MediaProjectionCapture @Inject constructor(
    @ApplicationContext private val context: Context
) : ScreenCapture {

    companion object {
        private const val TAG = "MediaProjectionCapture"
        private const val MAX_IMAGES = 2
    }

    private var mediaProjection: MediaProjection? = null
    private var virtualDisplay: VirtualDisplay? = null
    private var imageReader: ImageReader? = null
    private var handlerThread: HandlerThread? = null
    private var handler: Handler? = null

    @Volatile private var _isRunning = false
    override val isRunning: Boolean get() = _isRunning

    @SuppressLint("WrongConstant")
    override fun start(resultCode: Int, data: Intent, targetWidth: Int, targetHeight: Int) {
        if (_isRunning) return
        val mgr = context.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        mediaProjection = mgr.getMediaProjection(resultCode, data) ?: error("MediaProjection null")

        handlerThread = HandlerThread("CaptureThread").also { it.start() }
        handler = Handler(handlerThread!!.looper)

        imageReader = ImageReader.newInstance(targetWidth, targetHeight, PixelFormat.RGBA_8888, MAX_IMAGES)

        virtualDisplay = mediaProjection!!.createVirtualDisplay(
            "Pooll8Capture", targetWidth, targetHeight,
            context.resources.displayMetrics.densityDpi,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
            imageReader!!.surface, null, handler
        )
        _isRunning = true
        Log.d(TAG, "Capture started: ${targetWidth}x$targetHeight")
    }

    override fun setFrameListener(listener: (Bitmap) -> Unit) {
        imageReader?.setOnImageAvailableListener({ reader ->
            val image = reader.acquireLatestImage() ?: return@setOnImageAvailableListener
            try {
                val planes = image.planes
                val buffer = planes[0].buffer
                val pixelStride = planes[0].pixelStride
                val rowStride = planes[0].rowStride
                val rowPadding = rowStride - pixelStride * image.width

                val bmp = Bitmap.createBitmap(
                    image.width + rowPadding / pixelStride, image.height, Bitmap.Config.ARGB_8888
                )
                bmp.copyPixelsFromBuffer(buffer)
                val cropped = if (rowPadding == 0) bmp
                else Bitmap.createBitmap(bmp, 0, 0, image.width, image.height)
                listener(cropped)
                if (cropped !== bmp) bmp.recycle()
                cropped.recycle()
            } finally { image.close() }
        }, handler)
    }

    override fun stop() {
        _isRunning = false
        virtualDisplay?.release(); virtualDisplay = null
        imageReader?.close(); imageReader = null
        mediaProjection?.stop(); mediaProjection = null
        handlerThread?.quitSafely(); handlerThread = null
        handler = null
    }

    override fun release() = stop()
}
