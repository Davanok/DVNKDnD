package com.davanok.dvnkdnd.ui.components.diceRoller

import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.util.fastAny
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.util.fastForEachIndexed
import androidx.compose.ui.util.fastMap
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

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
    fun normalized() = div(length())

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

    // --- HSL helpers (0..1 ranges) ---
    fun rgbToHsl(r: Float, g: Float, b: Float): Triple<Float, Float, Float> {
        val max = maxOf(r, g, b)
        val min = minOf(r, g, b)
        val l = (max + min) / 2f
        if (max == min) return Triple(0f, 0f, l)
        val d = max - min
        val s = if (l > 0.5f) d / (2f - max - min) else d / (max + min)
        val h = when (max) {
            r -> ((g - b) / d + (if (g < b) 6f else 0f)) / 6f
            g -> ((b - r) / d + 2f) / 6f
            else -> ((r - g) / d + 4f) / 6f
        }.let { (it % 1f + 1f) % 1f } // ensure 0..1
        return Triple(h, s, l)
    }
    fun hue2rgb(p: Float, q: Float, t0: Float): Float {
        var t = t0
        if (t < 0f) t += 1f
        if (t > 1f) t -= 1f
        return when {
            t < 1f/6f -> p + (q - p) * 6f * t
            t < 1f/2f -> q
            t < 2f/3f -> p + (q - p) * (2f/3f - t) * 6f
            else -> p
        }
    }
    fun hslToRgb(h: Float, s: Float, l: Float): Triple<Float, Float, Float> {
        if (s == 0f) return Triple(l, l, l)
        val q = if (l < 0.5f) l * (1f + s) else l + s - l * s
        val p = 2f * l - q
        val r = hue2rgb(p, q, h + 1f/3f)
        val g = hue2rgb(p, q, h)
        val b = hue2rgb(p, q, h - 1f/3f)
        return Triple(r, g, b)
    }

    val rotated = verts.fastMap { rotateVertexUsingMatrix(it, rotation.x, rotation.y, rotation.z) }
    val projected = rotated.fastMap { projectVertex(it, scale, cameraZ) }
    val lightN = lightDir.normalized()

    val faceDraws = ArrayList<FaceDraw>(faces.size)
    faces.fastForEachIndexed { fi, faceIdxs ->
        if (faceIdxs.size < 3) return@fastForEachIndexed
        if (faceIdxs.fastAny { it < 0 || it >= rotated.size }) return@fastForEachIndexed

        val poly3 = faceIdxs.fastMap { rotated[it] }
        val centroid = poly3.reduce { acc, v -> acc + v } / poly3.size.toFloat()

        val edgeA = poly3[1] - poly3[0]
        val edgeB = poly3[2] - poly3[0]
        val n = edgeB.cross(edgeA).normalized()

        val poly2 = faceIdxs.fastMap { projected[it] + center }
        if (poly2.size < 3) return@fastForEachIndexed

        val depth = centroid.z
        val centroid2D = projectVertex(centroid, scale, cameraZ)

        faceDraws.add(FaceDraw(fi, poly2, n, depth, centroid, centroid2D))
    }

    if (faceDraws.isEmpty()) return null

    // 0 = opaque, 1 = fully transparent (glass)
    val glassMixGlobal = (1f - baseColor.alpha).coerceIn(0f, 1f)

    faceDraws.sortedByDescending { it.depth }.fastForEach { fd ->
        val ndotl = fd.normal.dot(lightN).coerceIn(-1f, 1f)

        val path = Path().apply {
            val p0 = fd.poly2D.first()
            moveTo(p0.x, p0.y)
            (1 until fd.poly2D.size).forEach {
                val p = fd.poly2D[it]
                lineTo(p.x, p.y)
            }
            close()
        }

        if (glassMixGlobal < 0.01f) {
            // OPAQUE baseline
            val brightness = 0.45f + 0.55f * ((ndotl + 1f) / 2f)
            val faceColor = Color(
                (baseColor.red * brightness).coerceIn(0f, 1f),
                (baseColor.green * brightness).coerceIn(0f, 1f),
                (baseColor.blue * brightness).coerceIn(0f, 1f),
                baseColor.alpha
            )
            drawPath(path, faceColor)
            drawPath(path, strokeColor, style = Stroke(width = strokeWidthPx))
        } else {
            val camPos = Vec3(0f, 0f, cameraZ)
            val viewDir = (camPos - fd.centroid).normalized()
            val ndotv = fd.normal.dot(viewDir).coerceIn(-1f, 1f)

            val fresnel = ((1f - ndotv).coerceIn(0f, 1f))
            val fresnelPow = fresnel * fresnel
            val rimBase = (0.12f + 0.88f * fresnelPow).coerceIn(0f, 1f)
            val rimFactor = (rimBase * glassMixGlobal).coerceIn(0f, 1f)

            val halfVec = (lightN + viewDir).normalized()
            val specAngle = fd.normal.dot(halfVec).coerceIn(0f, 1f)
            val shininess = 64f
            val specularBase = specAngle.pow(shininess) * 1.6f
            val specular = (specularBase * glassMixGlobal).coerceIn(0f, 1f)

            // --- Brighten without washing to white: use HSL lift (preserve hue & saturation) ---
            val r0 = baseColor.red
            val g0 = baseColor.green
            val b0 = baseColor.blue
            val (h0, s0, l0) = rgbToHsl(r0, g0, b0)

            // how much to raise lightness: depends on transparency and lighting (ndotl)
            // aggressive lift for very transparent, modest for mid values
            val liftFromGlass = (0.45f * glassMixGlobal).coerceIn(0f, 0.9f) // max ~0.45 of remaining range
            val liftFromLight = (0.15f * ((ndotl + 1f) / 2f)) // lit faces slightly brighter
            val newL = (l0 + (1f - l0) * (liftFromGlass + liftFromLight)).coerceIn(0f, 0.95f)

            // slightly boost saturation for very transparent to keep color vivid
            val newS = (s0 + (1f - s0) * (0.18f * glassMixGlobal)).coerceIn(0f, 1f)

            val (br, bg, bb) = hslToRgb(h0, newS, newL)
            val litTint = Color(br.coerceIn(0f,1f), bg.coerceIn(0f,1f), bb.coerceIn(0f,1f), baseColor.alpha)

            // alpha control for edge/center (stronger so not invisible)
            val glassAlphaBase = (0.14f + 0.48f * (1f - ndotl)).coerceIn(0.08f, 0.8f)
            val glassAlpha = (glassAlphaBase * glassMixGlobal).coerceIn(0f, 1f)
            val edgeAlpha = (glassAlpha * (0.85f + 0.6f * glassMixGlobal)).coerceIn(0f, 1f)
            val centerAlpha = (glassAlpha * (0.5f + 0.45f * glassMixGlobal)).coerceIn(0f, 1f)

            val glassEdgeColor = litTint.copy(alpha = edgeAlpha)
            val glassCenterColor = litTint.copy(alpha = centerAlpha)

            // polygon bounds for radial sizing
            var minX = Float.POSITIVE_INFINITY
            var minY = Float.POSITIVE_INFINITY
            var maxX = Float.NEGATIVE_INFINITY
            var maxY = Float.NEGATIVE_INFINITY
            fd.poly2D.fastForEach { p ->
                if (p.x < minX) minX = p.x
                if (p.y < minY) minY = p.y
                if (p.x > maxX) maxX = p.x
                if (p.y > maxY) maxY = p.y
            }
            val boxW = (maxX - minX).coerceAtLeast(1f)
            val boxH = (maxY - minY).coerceAtLeast(1f)
            val radialRadius = (kotlin.math.hypot(boxW.toDouble(), boxH.toDouble()).toFloat() * 0.65f).coerceAtLeast(8f)

            val lightShift = Offset(-lightN.x, -lightN.y) * (radialRadius * 0.25f)
            val fillCenter = fd.centroid2D + center + lightShift

            val fillBrush = Brush.radialGradient(
                colors = listOf(
                    glassCenterColor,
                    glassEdgeColor
                ),
                center = fillCenter,
                radius = radialRadius
            )

            // draw main glass fill
            drawPath(path = path, brush = fillBrush)

            // gentle white boost (much weaker than before) to avoid total wash-out
            val brightenAlpha = (0.02f + 0.20f * glassMixGlobal).coerceIn(0f, 0.5f)
            if (brightenAlpha > 0.001f) {
                val brightBrush = Brush.radialGradient(
                    colors = listOf(
                        Color.White.copy(alpha = brightenAlpha * 0.7f),
                        Color.Transparent
                    ),
                    center = fillCenter,
                    radius = radialRadius * 0.9f
                )
                drawPath(path = path, brush = brightBrush)
            }

            // specular highlight
            val highlightOffset = Offset(-lightN.x, -lightN.y) * (radialRadius * 0.32f)
            val reflectCenter = fillCenter + highlightOffset
            val specIntensity = (specular * 0.9f + rimFactor * 0.15f).coerceIn(0f, 1f)
            if (specIntensity > 0.01f) {
                val specBrush = Brush.radialGradient(
                    colors = listOf(
                        Color.White.copy(alpha = (0.55f * specIntensity).coerceIn(0f,1f)),
                        Color.White.copy(alpha = 0f)
                    ),
                    center = reflectCenter,
                    radius = radialRadius * 0.55f
                )
                drawPath(path = path, brush = specBrush)
            }

            // rim overlay
            val rimAlpha = (0.12f * rimFactor * (0.7f + 0.6f * glassMixGlobal)).coerceIn(0f, 0.6f)
            if (rimAlpha > 0.003f) {
                val rimBrush = Brush.radialGradient(
                    colors = listOf(
                        Color.White.copy(alpha = rimAlpha),
                        Color.Transparent
                    ),
                    center = fillCenter,
                    radius = radialRadius * 1.2f
                )
                drawPath(path = path, brush = rimBrush)
            }

            // stroke softened for transparent pieces
            val outerStrokeAlpha = (0.5f * (1f - glassMixGlobal * 0.9f)).coerceIn(0f, 1f)
            drawPath(path = path, color = strokeColor.copy(alpha = outerStrokeAlpha), style = Stroke(width = strokeWidthPx))

            val innerEdgeAlpha = (0.22f * rimFactor).coerceIn(0f, 0.5f)
            if (innerEdgeAlpha > 0.01f) {
                drawPath(path = path, color = Color.White.copy(alpha = innerEdgeAlpha), style = Stroke(width = strokeWidthPx / 2f))
            }
        }
    }

    return faceDraws.minByOrNull { it.depth }
}
