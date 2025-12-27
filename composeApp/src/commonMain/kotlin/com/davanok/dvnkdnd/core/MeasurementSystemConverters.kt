package com.davanok.dvnkdnd.core

object MeasurementSystemConverters {
    object Length {
        fun centimeterToInch(cm: Int): Float = cm * 0.393701f

        fun inchToFoot(inch: Float): Float = inch * 0.0833333f
        fun footToInch(foot: Float): Float = foot * 12
    }
    object Weight {
        fun gramToOunce(g: Int): Float = g * 0.035274f

        fun ounceToFount(oz: Float): Float = oz * 0.0625f
        fun fountToOunce(ft: Float): Float = ft * 16
    }
}