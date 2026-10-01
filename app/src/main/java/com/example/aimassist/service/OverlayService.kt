package com.example.aimassist.service

import android.content.Intent
import android.graphics.Bitmap
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.example.aimassist.capture.MediaProjectionCapture
import com.example.aimassist.cv.OpenCVBallDetector
import com.example.aimassist.cv.TableMapper
import com.example.aimassist.domain.model.Ball
import com.example.aimassist.domain.model.BallColor
import com.example.aimassist.overlay.OverlayWindowManager
import com.example.aimassist.physics.TrajectoryCalculator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class OverlayService : LifecycleService() {

    companion object {
        private const val TAG = "OverlayService"
        const val EXTRA_RESULT_CODE = "resultCode"
        const val EXTRA_DATA = "data"
        const val TARGET_WIDTH = 720
        const val TARGET_HEIGHT = 1280
        const val FRAME_SKIP = 2
    }

    @Inject lateinit var capture: MediaProjectionCapture
    @Inject lateinit var detector: OpenCVBallDetector
    @Inject lateinit var tableMapper: TableMapper
    @Inject lateinit var physics: TrajectoryCalculator
    @Inject lateinit var overlayManager: OverlayWindowManager
    @Inject lateinit var notifHelper: NotificationHelper

    private val frameChannel = MutableSharedFlow<Bitmap>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    private var processingJob: Job? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        try {
            notifHelper.ensureChannel()
            startForeground(NotificationHelper.NOTIF_ID, notifHelper.buildNotification())
        } catch (t: Throwable) {
            Log.e(TAG, "startForeground failed", t)
            stopSelf()
            return START_NOT_STICKY
        }

        intent?.let {
            val code = it.getIntExtra(EXTRA_RESULT_CODE, -1)
            @Suppress("DEPRECATION")
            val data = it.getParcelableExtra<Intent>(EXTRA_DATA)
            if (code == -1 || data == null) {
                Log.e(TAG, "Missing projection data")
                stopSelf()
                return START_NOT_STICKY
            }
            try {
                startPipeline(code, data)
            } catch (t: Throwable) {
                Log.e(TAG, "Pipeline failed", t)
                stopSelf()
            }
        }
        return START_STICKY
    }

    private fun startPipeline(resultCode: Int, data: Intent) {
        if (processingJob?.isActive == true) return
        overlayManager.attach()
        capture.start(resultCode, data, TARGET_WIDTH, TARGET_HEIGHT)
        capture.setFrameListener { frameChannel.tryEmit(it) }

        processingJob = lifecycleScope.launch(Dispatchers.Default) {
            var counter = 0
            frameChannel.collect { bitmap ->
                counter++
                if (counter % FRAME_SKIP != 0) return@collect
                runCatching {
                    val detection = detector.detect(bitmap)
                    val table = tableMapper.map(bitmap, detection.balls) ?: return@runCatching
                    val cue = detection.balls.firstOrNull { it.color == BallColor.CUE } ?: return@runCatching
                    val target = pickTarget(detection.balls, cue) ?: return@runCatching
                    val traj = physics.compute(cue, target, table)
                    withContext(Dispatchers.Main) {
                        overlayManager.attach().trajectory = traj
                    }
                }.onFailure { Log.w(TAG, "Frame failed", it) }
            }
        }
    }

    private fun pickTarget(balls: List<Ball>, cue: Ball): Ball? =
        balls.filter { it.id != cue.id }.minByOrNull { it.center.distanceTo(cue.center) }

    override fun onDestroy() {
        try { processingJob?.cancel() } catch (_: Throwable) {}
        try { capture.stop() } catch (_: Throwable) {}
        try { overlayManager.detach() } catch (_: Throwable) {}
        try { detector.release() } catch (_: Throwable) {}
        super.onDestroy()
    }

    override fun onBind(intent: Intent): IBinder? {
        super.onBind(intent)
        return null
    }
}
