package com.example.aimassist.physics

import com.example.aimassist.domain.model.Ball
import com.example.aimassist.domain.model.Table
import com.example.aimassist.domain.model.Trajectory

interface TrajectoryCalculator {
    fun compute(cue: Ball, target: Ball, table: Table): Trajectory
}
