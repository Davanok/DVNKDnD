package com.davanok.dvnkdnd.domain.repositories.local

import okio.Path

interface FilesRepository {
    suspend fun write(bytes: ByteArray, path: Path): Result<Unit>
    suspend fun read(path: Path): Result<ByteArray>
    suspend fun delete(path: Path): Result<Unit>
    suspend fun move(from: Path, to: Path): Result<Unit>

    fun getFilename(dir: Path, extension: String, temp: Boolean = false): Path
}