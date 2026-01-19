package com.davanok.dvnkdnd.ui.components.diceRoller

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D

data class Rotation(val x: Float, val y: Float, val z: Float) {
    companion object {
        val Zero = Rotation(0f, 0f, 0f)
    }
}

data class RotAnims(
    val x: Animatable<Float, AnimationVector1D>,
    val y: Animatable<Float, AnimationVector1D>,
    val z: Animatable<Float, AnimationVector1D>
)

fun Rotation.toRotAnims() = RotAnims(
    x = Animatable(x),
    y = Animatable(y),
    z = Animatable(z)
)

fun RotAnims.toRotation() = Rotation(x = x.value, y = y.value, z = z.value)
