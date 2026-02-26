package com.davanok.dvnkdnd.ui.components.text

import androidx.compose.runtime.Composable
import com.davanok.dvnkdnd.core.MeasurementSystemConverters
import com.davanok.dvnkdnd.data.platform.toString
import com.davanok.dvnkdnd.domain.enums.configs.MeasurementSystem
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.centimeter_short
import dvnkdnd.composeapp.generated.resources.foot_short
import dvnkdnd.composeapp.generated.resources.gram_short
import dvnkdnd.composeapp.generated.resources.inch_short
import dvnkdnd.composeapp.generated.resources.kilogram_short
import dvnkdnd.composeapp.generated.resources.meter_short
import dvnkdnd.composeapp.generated.resources.ounce_short
import dvnkdnd.composeapp.generated.resources.pound_short
import org.jetbrains.compose.resources.stringResource

object MeasurementConverter {
    @Composable
    fun convertLength(
        inches: Int,
        system: MeasurementSystem
    ) = convertLength(
        inches.toDouble(),
        system
    )
    @Composable
    fun convertLength(
        inches: Double,
        system: MeasurementSystem
    ): String = when (system) {
        MeasurementSystem.METRIC -> {
            val cm = MeasurementSystemConverters.Length.inchToCentimeter(inches)
            if (cm >= 100) {
                val meters = cm / 100.0
                stringResource(Res.string.meter_short, meters.toString(2))
            } else {
                stringResource(Res.string.centimeter_short, cm)
            }
        }

        MeasurementSystem.IMPERIAL -> {
            val feet = MeasurementSystemConverters.Length.inchToFoot(inches).toInt()
            val inches = inches - MeasurementSystemConverters.Length.footToInch(feet.toDouble())

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

    @Composable
    fun convertWeight(
        ounces: Int,
        system: MeasurementSystem
    ) = convertWeight(
        ounces.toDouble(),
        system
    )
    @Composable
    fun convertWeight(
        ounces: Double,
        system: MeasurementSystem
    ): String = when (system) {
        MeasurementSystem.METRIC -> {
            val g = MeasurementSystemConverters.Weight.ounceToGram(ounces)
            if (g >= 1000) {
                val kilograms = g / 1000.0
                stringResource(Res.string.kilogram_short, kilograms.toString(2))
            } else {
                stringResource(Res.string.gram_short, g)
            }
        }

        MeasurementSystem.IMPERIAL -> {
            val pound = MeasurementSystemConverters.Weight.ounceToPound(ounces).toInt()
            val ounce = ounces - MeasurementSystemConverters.Weight.fountToOunce(pound.toDouble())

            when {
                pound > 0 && ounce >= 0.5 -> {
                    stringResource(Res.string.pound_short, pound) +
                            ' ' +
                            stringResource(Res.string.ounce_short, ounce.toString(2))
                }
                pound > 0 -> stringResource(Res.string.pound_short, pound)
                else -> stringResource(Res.string.ounce_short, ounce.toString(2))
            }
        }
    }
}