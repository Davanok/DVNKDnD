package com.davanok.dvnkdnd.core

object MeasurementSystemConverters {
    object Length {
        fun inchToCentimeter(inch: Double): Double = inch * 2.54
        fun centimeterToInch(cm: Double): Double = cm / 2.54

        fun inchToFoot(inch: Double): Double = inch / 12
        fun footToInch(foot: Double): Double = foot * 12
    }
    object Weight {
        fun ounceToGram(oz: Double): Double = oz * 28.3459
        fun gramToOunce(g: Double): Double = g / 28.3459

        fun ounceToPound(oz: Double): Double = oz / 16
        fun fountToOunce(ft: Double): Double = ft * 16
    }
}