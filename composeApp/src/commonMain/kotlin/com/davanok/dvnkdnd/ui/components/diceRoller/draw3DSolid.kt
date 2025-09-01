package com.davanok.dvnkdnd.ui.components.diceRoller

import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.util.fastAny
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.util.fastForEachIndexed
import androidx.compose.ui.util.fastMap
import io.github.aakira.napier.Napier
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

private const val EPS = 1e-6f
private const val PROJ_EPS = 1e-4f

@Immutable
data class Rotation(
    val x: Float,
    val y: Float,
    val z: Float
) {
    companion object {
        val Zero = Rotation(0f, 0f, 0f)
    }
}

data class Vec3(val x: Float, val y: Float, val z: Float) {
    operator fun minus(o: Vec3) = Vec3(x - o.x, y - o.y, z - o.z)
    operator fun plus(o: Vec3) = Vec3(x + o.x, y + o.y, z + o.z)
    operator fun times(s: Float) = Vec3(x * s, y * s, z * s)
    operator fun div(s: Float) = Vec3(x / s, y / s, z / s)

    fun length() = sqrt((x * x + y * y + z * z).toDouble()).toFloat()
    fun normalized(): Vec3 {
        val len = length()
        return if (len <= EPS) this else this / len
    }

    fun cross(other: Vec3) = Vec3(
        y * other.z - z * other.y,
        z * other.x - x * other.z,
        x * other.y - y * other.x
    )

    fun dot(other: Vec3) = x * other.x + y * other.y + z * other.z
}

/**
 * Build combined rotation matrix for X, Y, Z rotations (order: X, then Y, then Z)
 * and apply to vertex v. This avoids repeated sin/cos for each vertex.
 */
private fun rotateVertexUsingMatrix(v: Vec3, ax: Float, ay: Float, az: Float): Vec3 {
    // precompute sines/cosines
    val cx = cos(ax); val sx = sin(ax)
    val cy = cos(ay); val sy = sin(ay)
    val cz = cos(az); val sz = sin(az)

    // Rotation matrices (Rx, Ry, Rz), combined R = Rz * Ry * Rx
    // Multiply matrices explicitly to get combined rotation matrix elements.
    // R = Rz * Ry * Rx
    val m00 = cz * cy
    val m01 = cz * (sy * sx) - sz * cx
    val m02 = cz * (sy * cx) + sz * sx

    val m10 = sz * cy
    val m11 = sz * (sy * sx) + cz * cx
    val m12 = sz * (sy * cx) - cz * sx

    val m20 = -sy
    val m21 = cy * sx
    val m22 = cy * cx

    val x = v.x * m00 + v.y * m01 + v.z * m02
    val y = v.x * m10 + v.y * m11 + v.z * m12
    val z = v.x * m20 + v.y * m21 + v.z * m22
    return Vec3(x, y, z)
}

/**
 * Basic perspective projection used by original code.
 * cameraZ is distance from origin to camera along -Z axis (camera at z = -cameraZ).
 */
private fun projectVertex(v: Vec3, scale: Float, cameraZ: Float = 4f): Offset {
    val denom = (v.z + cameraZ).coerceAtLeast(PROJ_EPS)
    val k = scale / denom
    return Offset(v.x * k, v.y * k)
}

data class FaceDraw(
    val idx: Int,
    val poly2D: List<Offset>,
    val normal: Vec3,
    val depth: Float,
    val centroid: Vec3,
    val centroid2D: Offset
)

/**
 * Draw a simple 3D solid given vertices and faces.
 *
 * - vertsIn: list of 3D vertices (model space)
 * - faces: list of faces, each face is list of indices into vertsIn
 * - center: screen-space center offset to translate projected coords to
 * - overallScale: projection focal/scale factor
 * - rotation: rotations in radians around X, Y, Z (applied in that order)
 * - baseColor / strokeColor / strokeWidthPx: visual params
 * - lightDir: world-space light direction
 * - cameraZ: distance along -Z where camera is placed (default 6f)
 * - cullBackfaces: if true, skip faces whose normal faces away from camera
 *
 * Returns FaceDraw of the face closest to the camera (by centroid.z) or null.
 */
fun DrawScope.draw3DSolid(
    verts: List<Vec3>,
    faces: List<List<Int>>,
    center: Offset,
    scale: Float,
    rotation: Rotation,
    baseColor: Color,
    strokeColor: Color = Color.Black,
    strokeWidthPx: Float = 2f,
    lightDir: Vec3,
    cameraZ: Float = 4f
): FaceDraw? {
    if (verts.isEmpty() || faces.isEmpty()) return null

    // rotate vertices (use matrix)
    val rotated = verts.fastMap { rotateVertexUsingMatrix(it, rotation.x, rotation.y, rotation.z) }

    // project (2D offsets), keep list parallel to rotated
    val projected = rotated.fastMap { projectVertex(it, scale, cameraZ) }

    // normalize light direction once
    val lightN = lightDir.normalized()

    // Build face data
    val faceDraws = ArrayList<FaceDraw>(faces.size)
    faces.fastForEachIndexed { fi, faceIdxs ->
        if (faceIdxs.size < 3) return@fastForEachIndexed

        // guard indices
        if (faceIdxs.fastAny { it < 0 || it >= rotated.size }) return@fastForEachIndexed

        val poly3 = faceIdxs.fastMap { rotated[it] }
        // centroid in 3D
        val centroid = poly3.reduce { acc, v -> acc + v } / poly3.size.toFloat()

        // compute normal from first two edges
        val edgeA = poly3[1] - poly3[0]
        val edgeB = poly3[2] - poly3[0]
        var n = edgeA.cross(edgeB)
        n = if (n.length() <= EPS) Vec3(0f, 0f, 1f) else n.normalized()
        n *= -1f

        // build 2D polygon in screen space
        val poly2 = faceIdxs.fastMap { projected[it] + center }

        if (poly2.size < 3) return@fastForEachIndexed

        val depth = centroid.z
        val centroid2D = projectVertex(centroid, scale, cameraZ)
        faceDraws.add(FaceDraw(fi, poly2, n, depth, centroid, centroid2D))
    }

    if (faceDraws.isEmpty()) return null

    // Sort by depth: furthest first (so nearer faces are drawn last) and draw
    faceDraws.sortedByDescending { it.depth }.fastForEach { fd ->
        val ndotl = (fd.normal.dot(lightN)).coerceIn(-1f, 1f)
        val brightness = 0.2f + 0.8f * ((ndotl + 1f) / 2f) // keep >0.2 to avoid totally black
        val faceColor = Color(
            (baseColor.red * brightness).coerceIn(0f, 1f),
            (baseColor.green * brightness).coerceIn(0f, 1f),
            (baseColor.blue * brightness).coerceIn(0f, 1f),
            baseColor.alpha
        )

        val path = Path().apply {
            val p0 = fd.poly2D.first()
            moveTo(p0.x, p0.y)
            (1 until fd.poly2D.size).forEach {
                val p = fd.poly2D[it]
                lineTo(p.x, p.y)
            }
            close()
        }

        // draw fill and stroke
        drawPath(path, faceColor)
        drawPath(path, strokeColor, style = Stroke(width = strokeWidthPx))
    }
    // return face with minimum depth (closest to camera)
    return faceDraws.minByOrNull { it.depth }
}
