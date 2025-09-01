package com.davanok.dvnkdnd.ui.components.diceRoller

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.translate
import com.davanok.dvnkdnd.data.model.dndEnums.Dices
import kotlin.math.min
import kotlin.math.PI

private const val DEFAULT_SCALE_FRACTION = 3f
private const val DEFAULT_CAMERA_Z = 8f
private const val DEFAULT_PADDING_FRACTION = 0.05f
private const val DEFAULT_STROKE_PX = 0.5f
private val DEFAULT_LIGHT_DIR_RAW = Vec3(0f, 0f, -1f)

private fun Float.degToRad(): Float = times(PI.toFloat() / 180f)

private fun computeBoundingRadius(verts: List<Vec3>): Float =
    verts.maxOfOrNull { it.length() } ?: 1e-3f

private fun DrawScope.internalDrawDice(
    vertsFaces: Pair<List<Vec3>, List<List<Int>>>,
    baseColor: Color,
    rotation: Rotation = Rotation.Zero,
    closestFaceContent: (DrawScope.(faceNormal: Vec3) -> Unit)? = null
) {
    val minSide = min(size.width, size.height)
    val reserved = minSide * DEFAULT_PADDING_FRACTION
    val desiredProjectedRadiusPx = (minSide - reserved) * DEFAULT_SCALE_FRACTION

    val (verts, faces) = vertsFaces
    val boundingRadius = computeBoundingRadius(verts).coerceAtLeast(1e-6f)
    val overallScale = desiredProjectedRadiusPx / boundingRadius

    val rotationRad = Rotation(
        rotation.x.degToRad(),
        rotation.y.degToRad(),
        rotation.z.degToRad()
    )

    val lightDir = DEFAULT_LIGHT_DIR_RAW.normalized()

    val faceClosest = draw3DSolid(
        verts = verts,
        faces = faces,
        center = center,
        scale = overallScale,
        rotation = rotationRad,
        baseColor = baseColor,
        strokeColor = Color.Black,
        strokeWidthPx = DEFAULT_STROKE_PX,
        lightDir = lightDir,
        cameraZ = DEFAULT_CAMERA_Z
    )

    if (closestFaceContent != null && faceClosest != null) {
        val offset = center + faceClosest.centroid2D
        translate(offset.x, offset.y) {
            closestFaceContent(faceClosest.normal)
        }
    }
}

fun DrawScope.drawDice(
    dice: Dices,
    rotation: Rotation = dice.previewRotation(),
    color: Color = Color.White,
    closestFaceContent: (DrawScope.(faceNormal: Vec3) -> Unit)? = null
) {
    val vertsFaces = when (dice) {
        Dices.D4 -> makeTetrahedron()
        Dices.D6 -> makeCube()
        Dices.D8 -> makeOctahedron()
        Dices.D10, Dices.D100 -> makeD10()
        Dices.D12 -> makeDodecahedronFromIcosahedron()
        Dices.D20 -> makeIcosahedron()
        Dices.OTHER -> return
    }
    internalDrawDice(
        vertsFaces = vertsFaces,
        baseColor = color,
        rotation = rotation,
        closestFaceContent = closestFaceContent
    )
}

@Suppress("unused")
fun DrawScope.drawD4(
    rotation: Rotation = Rotation.Zero,
    color: Color = Color.White,
    closestFaceContent: (DrawScope.(faceNormal: Vec3) -> Unit)? = null
) = internalDrawDice(
    vertsFaces = makeTetrahedron(),
    baseColor = color,
    rotation = rotation,
    closestFaceContent = closestFaceContent
)

@Suppress("unused")
fun DrawScope.drawD6(
    rotation: Rotation = Rotation.Zero,
    color: Color = Color.White,
    closestFaceContent: (DrawScope.(faceNormal: Vec3) -> Unit)? = null
) = internalDrawDice(
    vertsFaces = makeCube(),
    baseColor = color,
    rotation = rotation,
    closestFaceContent = closestFaceContent
)

@Suppress("unused")
fun DrawScope.drawD8(
    rotation: Rotation = Rotation.Zero,
    color: Color = Color.White,
    closestFaceContent: (DrawScope.(faceNormal: Vec3) -> Unit)? = null
) = internalDrawDice(
    vertsFaces = makeOctahedron(),
    baseColor = color,
    rotation = rotation,
    closestFaceContent = closestFaceContent
)

@Suppress("unused")
fun DrawScope.drawD10(
    rotation: Rotation = Rotation.Zero,
    color: Color = Color.White,
    closestFaceContent: (DrawScope.(faceNormal: Vec3) -> Unit)? = null
) = internalDrawDice(
    vertsFaces = makeD10(),
    baseColor = color,
    rotation = rotation,
    closestFaceContent = closestFaceContent
)

@Suppress("unused")
fun DrawScope.drawD12(
    rotation: Rotation = Rotation.Zero,
    color: Color = Color.White,
    closestFaceContent: (DrawScope.(faceNormal: Vec3) -> Unit)? = null
) = internalDrawDice(
    vertsFaces = makeDodecahedronFromIcosahedron(),
    baseColor = color,
    rotation = rotation,
    closestFaceContent = closestFaceContent
)

@Suppress("unused")
fun DrawScope.drawD20(
    rotation: Rotation = Rotation.Zero,
    color: Color = Color.White,
    closestFaceContent: (DrawScope.(faceNormal: Vec3) -> Unit)? = null
) = internalDrawDice(
    vertsFaces = makeIcosahedron(),
    baseColor = color,
    rotation = rotation,
    closestFaceContent = closestFaceContent
)

fun DrawScope.drawD100(
    rotation: Rotation = Rotation.Zero,
    color: Color = Color.White,
    closestFaceContent: (DrawScope.(faceNormal: Vec3) -> Unit)? = null
) = drawD10(rotation = rotation, color = color, closestFaceContent = closestFaceContent)

@Suppress("unused")
fun DrawScope.drawOther() {}

fun Dices.previewRotation() = when (this) {
    Dices.D4 -> Rotation(124f, 51f, 83f)
    Dices.D6 -> Rotation(0f, 0f, 0f)
    Dices.D8 -> Rotation(53f, 34f, 34f)
    Dices.D10, Dices.D100 -> Rotation(255f, 161f, 356f)
    Dices.D12 -> Rotation(30f, 0f, 0f)
    Dices.D20 -> Rotation(110f, 0f, 0f)
    Dices.OTHER -> Rotation.Zero
}
