package com.davanok.dvnkdnd.data.repositories

import okio.Path
import okio.Path.Companion.toPath

interface FilesRepository {
    suspend fun write(bytes: ByteArray, path: Path)
    suspend fun read(path: Path): ByteArray
    suspend fun delete(path: Path)
    suspend fun move(from: Path, to: Path)

    fun getFilename(dir: Path, extension: String, temp: Boolean = false): Path

    companion object Paths {
        val images = "images".toPath()
        val characterImages = images / "characters"
    }
}