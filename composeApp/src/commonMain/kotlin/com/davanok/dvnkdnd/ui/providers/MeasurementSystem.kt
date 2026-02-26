package com.davanok.dvnkdnd.ui.providers

import androidx.compose.runtime.staticCompositionLocalOf
import com.davanok.dvnkdnd.domain.enums.configs.MeasurementSystem
import io.github.aakira.napier.Napier

data class MeasurementSystemConfig(
    val weight: MeasurementSystem,
    val length: MeasurementSystem
)

val LocalMeasurementSystem = staticCompositionLocalOf {
    Napier.w { "CompositionLocal MeasurementSystemConfig not provided - using imperial" }
    MeasurementSystemConfig(
        weight = MeasurementSystem.IMPERIAL,
        length = MeasurementSystem.IMPERIAL
    )
}