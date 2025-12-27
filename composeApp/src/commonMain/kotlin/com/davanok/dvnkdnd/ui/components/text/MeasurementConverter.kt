package com.davanok.dvnkdnd.ui.components.text

import androidx.compose.runtime.Composable
import com.davanok.dvnkdnd.data.platform.toString
import com.davanok.dvnkdnd.domain.enums.configs.MeasurementSystem
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.centimeter_short
import dvnkdnd.composeapp.generated.resources.foot_short
import dvnkdnd.composeapp.generated.resources.inch_short
import dvnkdnd.composeapp.generated.resources.meter_short
import org.jetbrains.compose.resources.stringResource

object MeasurementConverter {
    @Composable
    fun convertLength(
        cm: Int,
        system: MeasurementSystem
    ): String = when (system) {
        MeasurementSystem.METRIC ->
            if (cm >= 100) {
                val meters = cm / 100.0
                stringResource(Res.string.meter_short, meters.toString(2))
            } else {
                stringResource(Res.string.centimeter_short, cm)
            }

        MeasurementSystem.IMPERIAL -> {
            val totalInches = cm * 0.393700787
            val feet = (totalInches / 12).toInt()
            val inches = totalInches - feet * 12

            when {
                feet > 0 && inches >= 0.5 -> {
                    stringResource(Res.string.foot_short, feet) +
                            ' ' +
                            stringResource(Res.string.inch_short, inches.toString(2))
                }
                feet > 0 -> stringResource(Res.string.foot_short, feet)
                else -> stringResource(Res.string.inch_short, inches.toString(2))
            }
        }
    }
}