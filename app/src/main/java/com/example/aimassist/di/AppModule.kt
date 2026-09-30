package com.example.aimassist.di

import com.example.aimassist.capture.MediaProjectionCapture
import com.example.aimassist.capture.ScreenCapture
import com.example.aimassist.cv.BallDetector
import com.example.aimassist.cv.OpenCVBallDetector
import com.example.aimassist.domain.repository.SettingsRepository
import com.example.aimassist.physics.BilliardPhysics
import com.example.aimassist.physics.TrajectoryCalculator
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.flowOf
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideSettingsRepository(): SettingsRepository = object : SettingsRepository {
        override val lineColor = flowOf(0xFFFFFFFF.toInt())
        override val lineWidth = flowOf(5f)
        override val showTargetLine = flowOf(true)
        override val showCueDeflection = flowOf(true)
        override val frameSkip = flowOf(2)
        override suspend fun setLineColor(color: Int) {}
        override suspend fun setLineWidth(width: Float) {}
        override suspend fun setShowTargetLine(enabled: Boolean) {}
        override suspend fun setShowCueDeflection(enabled: Boolean) {}
        override suspend fun setFrameSkip(count: Int) {}
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class BindingModule {
    @Binds abstract fun bindScreenCapture(impl: MediaProjectionCapture): ScreenCapture
    @Binds abstract fun bindBallDetector(impl: OpenCVBallDetector): BallDetector
    @Binds abstract fun bindTrajectoryCalculator(impl: BilliardPhysics): TrajectoryCalculator
}
