package com.davanok.dvnkdnd.data.repositories

import okio.Path
import okio.Path.Companion.toPath

interface FilesRepository {
    suspend fun write(bytes: ByteArray, path: Path): Result<Unit>
    suspend fun read(path: Path): Result<ByteArray>
    suspend fun delete(path: Path): Result<Unit>
    suspend fun move(from: Path, to: Path): Result<Unit>

    fun getFilename(dir: Path, extension: String, temp: Boolean = false): Path

    companion object Paths {
        val images = "images".toPath()
        val characterImages = images / "characters"
    }
}