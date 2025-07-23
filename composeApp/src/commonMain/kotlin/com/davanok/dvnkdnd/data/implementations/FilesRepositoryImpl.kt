package com.davanok.dvnkdnd.data.implementations

import com.davanok.dvnkdnd.data.repositories.FilesRepository
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath
import okio.SYSTEM
import kotlin.uuid.Uuid

class FilesRepositoryImpl(
    private val defaultDir: Path,
    private val cacheDir: Path
): FilesRepository {
    private val fs: FileSystem = FileSystem.SYSTEM

    override suspend fun write(bytes: ByteArray, path: Path) = runCatching {
        val parent = path.parent
        if (parent != null && !fs.exists(parent)) fs.createDirectories(parent)
        fs.write(path) {
            write(bytes)
        }
        Unit
    }
    override suspend fun read(path: Path): Result<ByteArray> = runCatching {
        fs.read(path) {
            readByteArray()
        }
    }
    override suspend fun delete(path: Path) = runCatching {
        fs.delete(path)
    }

    override suspend fun move(from: Path, to: Path) = runCatching {
        fs.atomicMove(from, to)
    }

    override fun getFilename(dir: Path, extension: String, temp: Boolean): Path {
        val root = if (temp) cacheDir else defaultDir
        val result = root / dir / (Uuid.random().toHexString() + "." + extension).toPath()
        return result
    }
}