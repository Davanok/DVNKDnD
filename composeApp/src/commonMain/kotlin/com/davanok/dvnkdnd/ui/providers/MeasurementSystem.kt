package com.davanok.dvnkdnd.ui.providers

import androidx.compose.runtime.staticCompositionLocalOf
import com.davanok.dvnkdnd.domain.enums.configs.MeasurementSystem

data class MeasurementSystemConfig(
    val weight: MeasurementSystem,
    val length: MeasurementSystem
)

val LocalMeasurementSystem = staticCompositionLocalOf {
    MeasurementSystemConfig(
        weight = MeasurementSystem.IMPERIAL,
        length = MeasurementSystem.IMPERIAL
    )
}