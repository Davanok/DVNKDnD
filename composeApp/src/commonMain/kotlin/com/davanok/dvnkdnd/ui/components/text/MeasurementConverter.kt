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
            val totalInches = MeasurementSystemConverters.Length.centimeterToInch(cm)
            val feet = MeasurementSystemConverters.Length.inchToFoot(totalInches).toInt()
            val inches = totalInches - MeasurementSystemConverters.Length.footToInch(feet.toFloat())

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
        g: Int,
        system: MeasurementSystem
    ): String = when (system) {
        MeasurementSystem.METRIC ->
            if (g >= 1000) {
                val kilograms = g / 1000.0
                stringResource(Res.string.kilogram_short, kilograms.toString(2))
            } else {
                stringResource(Res.string.gram_short, g)
            }

        MeasurementSystem.IMPERIAL -> {
            val totalOunce = MeasurementSystemConverters.Weight.gramToOunce(g)
            val fount = MeasurementSystemConverters.Weight.ounceToFount(totalOunce).toInt()
            val ounce = totalOunce - MeasurementSystemConverters.Weight.fountToOunce(fount.toFloat())

            when {
                fount > 0 && ounce >= 0.5 -> {
                    stringResource(Res.string.foot_short, fount) +
                            ' ' +
                            stringResource(Res.string.inch_short, ounce.toString(2))
                }
                fount > 0 -> stringResource(Res.string.foot_short, fount)
                else -> stringResource(Res.string.inch_short, ounce.toString(2))
            }
        }
    }
}