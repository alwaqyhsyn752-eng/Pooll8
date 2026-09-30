package com.example.aimassist.domain.repository

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val lineColor: Flow<Int>
    val lineWidth: Flow<Float>
    val showTargetLine: Flow<Boolean>
    val showCueDeflection: Flow<Boolean>
    val frameSkip: Flow<Int>
    suspend fun setLineColor(color: Int)
    suspend fun setLineWidth(width: Float)
    suspend fun setShowTargetLine(enabled: Boolean)
    suspend fun setShowCueDeflection(enabled: Boolean)
    suspend fun setFrameSkip(count: Int)
}
