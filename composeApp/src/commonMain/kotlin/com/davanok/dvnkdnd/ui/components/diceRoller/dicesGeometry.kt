package com.davanok.dvnkdnd.ui.components.diceRoller

import androidx.compose.ui.util.fastMap
import androidx.compose.ui.util.fastMapIndexedNotNull
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

private const val EPS = 1e-6f

/**
 * Regular tetrahedron centered at origin.
 */
fun makeTetrahedron(): Pair<List<Vec3>, List<List<Int>>> {
    val s = 1f
    val verts = listOf(
        Vec3(s, s, s),
        Vec3(s, -s, -s),
        Vec3(-s, s, -s),
        Vec3(-s, -s, s)
    )
    val faces = listOf(
        listOf(0, 2, 1),
        listOf(0, 1, 3),
        listOf(0, 3, 2),
        listOf(1, 2, 3)
    )
    return verts to faces
}

/**
 * Cube (hexahedron) vertices and quad faces.
 */
fun makeCube(): Pair<List<Vec3>, List<List<Int>>> {
    val s = 1f
    val verts = listOf(
        Vec3(-s, -s, -s), Vec3(s, -s, -s),
        Vec3(s, s, -s), Vec3(-s, s, -s),
        Vec3(-s, -s, s), Vec3(s, -s, s),
        Vec3(s, s, s), Vec3(-s, s, s)
    )
    val faces = listOf(
        listOf(0, 1, 2, 3), listOf(4, 7, 6, 5),
        listOf(0, 4, 5, 1), listOf(1, 5, 6, 2),
        listOf(2, 6, 7, 3), listOf(3, 7, 4, 0)
    )
    return verts to faces
}

/**
 * Octahedron (dual of cube).
 */
fun makeOctahedron(): Pair<List<Vec3>, List<List<Int>>> {
    val verts = listOf(
        Vec3(1f, 0f, 0f), Vec3(-1f, 0f, 0f),
        Vec3(0f, 1f, 0f), Vec3(0f, -1f, 0f),
        Vec3(0f, 0f, 1f), Vec3(0f, 0f, -1f)
    )
    val faces = listOf(
        listOf(0, 4, 2), listOf(2, 4, 1), listOf(1, 4, 3), listOf(3, 4, 0),
        listOf(2, 5, 0), listOf(1, 5, 2), listOf(3, 5, 1), listOf(0, 5, 3)
    )
    return verts to faces
}

/**
 * Icosahedron construction based on the golden ratio.
 */
fun makeIcosahedron(): Pair<List<Vec3>, List<List<Int>>> {
    val phi = (1.0 + sqrt(5.0)) / 2.0
    val t = phi.toFloat()
    val raw = listOf(
        Vec3(-1f, t, 0f), Vec3(1f, t, 0f), Vec3(-1f, -t, 0f), Vec3(1f, -t, 0f),
        Vec3(0f, -1f, t), Vec3(0f, 1f, t), Vec3(0f, -1f, -t), Vec3(0f, 1f, -t),
        Vec3(t, 0f, -1f), Vec3(t, 0f, 1f), Vec3(-t, 0f, -1f), Vec3(-t, 0f, 1f)
    )
    val verts = raw.fastMap { it.normalized() }

    val faces = listOf(
        listOf(0, 5, 11), listOf(0, 1, 5), listOf(0, 7, 1), listOf(0, 10, 7), listOf(0, 11, 10),
        listOf(1, 9, 5), listOf(5, 4, 11), listOf(11, 2, 10), listOf(10, 6, 7), listOf(7, 8, 1),
        listOf(3, 4, 9), listOf(3, 2, 4), listOf(3, 6, 2), listOf(3, 8, 6), listOf(3, 9, 8),
        listOf(4, 5, 9), listOf(2, 11, 4), listOf(6, 10, 2), listOf(8, 7, 6), listOf(9, 1, 8)
    )

    return verts to faces
}

/**
 * Build the dual polyhedron from vertices and faces.
 * Returns (dualVertices, dualFaces).
 * Dual vertices are centroids of original faces (projected to unit-like sphere).
 */
fun makeDualFromPoly(vertices: List<Vec3>, faces: List<List<Int>>): Pair<List<Vec3>, List<List<Int>>> {
    // 1) compute centroids of faces
    val centroidsRaw = faces.fastMap { face ->
        val inv = 1f / face.size
        var sx = 0f; var sy = 0f; var sz = 0f
        for (vi in face) {
            val p = vertices[vi]
            sx += p.x; sy += p.y; sz += p.z
        }
        Vec3(sx * inv, sy * inv, sz * inv)
    }

    // 2) scale centroids to have comparable magnitudes (avoid very small/large scales)
    val avgDist = centroidsRaw.fastMap { it.length() }.average().toFloat().coerceAtLeast(EPS)
    val centroids = centroidsRaw.fastMap { (it * (1f / avgDist)).normalized() }

    // 3) for each original vertex, collect incident faces and order them around the vertex
    val dualFaces = mutableListOf<List<Int>>()
    for (vi in vertices.indices) {
        val origin = vertices[vi].normalized()
        val incident = faces.fastMapIndexedNotNull { fi, f -> if (f.contains(vi)) fi else null }
        if (incident.size < 3) {
            // degeneracy: keep whatever incident faces exist
            dualFaces += incident
            continue
        }

        // build local tangent basis (U, V)
        val meanCentroid = incident.fold(Vec3(0f, 0f, 0f)) { acc, fi -> acc + centroids[fi] } * (1f / incident.size)
        var U = meanCentroid - origin * meanCentroid.dot(origin)
        if (U.length() <= EPS) {
            var arb = Vec3(1f, 0f, 0f)
            if (abs(origin.dot(arb)) > 0.9f) arb = Vec3(0f, 1f, 0f)
            U = arb.cross(origin)
        }
        U = U.normalized()

        var V = origin.cross(U)
        if (V.length() <= EPS) {
            var arb = Vec3(0f, 1f, 0f)
            if (abs(origin.dot(arb)) > 0.9f) arb = Vec3(0f, 0f, 1f)
            U = arb.cross(origin).normalized()
            V = origin.cross(U).normalized()
        } else {
            V = V.normalized()
        }

        // sort incident faces by angle around the vertex
        val ordered = incident.sortedBy { fi ->
            val c = centroids[fi]
            val proj = c - origin * c.dot(origin)
            val x = proj.dot(U).toDouble()
            val y = proj.dot(V).toDouble()
            atan2(y, x)
        }

        // remove consecutive duplicates if any and keep cycle order
        val orderedUnique = ordered.fold(mutableListOf<Int>()) { acc, idx ->
            if (acc.isEmpty() || acc.last() != idx) acc += idx
            acc
        }

        dualFaces += orderedUnique
    }

    val dualVerts = centroids.fastMap { it.normalized() }

    // Ensure outward-pointing face normals for dual faces
    val corrected = dualFaces.fastMap { face ->
        if (face.size < 3) return@fastMap face
        val p0 = dualVerts[face[0]]
        val p1 = dualVerts[face[1]]
        val p2 = dualVerts[face[2]]
        val n = (p1 - p0).cross(p2 - p0).normalized()
        val faceCenter = face.fold(Vec3(0f, 0f, 0f)) { acc, vi -> acc + dualVerts[vi] } * (1f / face.size)
        if (n.dot(faceCenter) < 0f) face.reversed() else face
    }

    return dualVerts to corrected
}

/**
 * Build a dodecahedron as the dual of the icosahedron.
 */
fun makeDodecahedronFromIcosahedron(): Pair<List<Vec3>, List<List<Int>>> {
    val (icosV, icosF) = makeIcosahedron()
    val (verts, faces) = makeDualFromPoly(icosV, icosF)
    return verts to faces.fastMap { it.reversed() }
}

/**
 * Construct a pentagonal trapezohedron-like D10 approximation.
 * Returns vertices and quad faces (10 faces total).
 */
fun makeD10(): Pair<List<Vec3>, List<List<Int>>> {
    val n = 5
    val radius = 1f
    val poleZ = 1.5f
    val ringZ = 0.2f
    val angleShift = (PI.toFloat() / n)

    val topRing = (0 until n).map { i ->
        val angle = 2f * PI.toFloat() * i / n
        Vec3(cos(angle) * radius, sin(angle) * radius, ringZ)
    }

    val bottomRing = (0 until n).map { i ->
        val angle = 2f * PI.toFloat() * i / n + angleShift
        Vec3(cos(angle) * radius, sin(angle) * radius, -ringZ)
    }

    val topPole = Vec3(0f, 0f, poleZ)
    val bottomPole = Vec3(0f, 0f, -poleZ)

    val verts = topRing + bottomRing + listOf(topPole, bottomPole)

    val topOffset = 0
    val bottomOffset = n
    val topPoleIndex = 2 * n
    val bottomPoleIndex = 2 * n + 1

    val faces = mutableListOf<List<Int>>()
    for (i in 0 until n) {
        val ni = (i + 1) % n
        faces += listOf(topPoleIndex, topOffset + ni, bottomOffset + i, topOffset + i)
        faces += listOf(bottomPoleIndex, bottomOffset + i, topOffset + ni, bottomOffset + ni)
    }

    return verts to faces
}
