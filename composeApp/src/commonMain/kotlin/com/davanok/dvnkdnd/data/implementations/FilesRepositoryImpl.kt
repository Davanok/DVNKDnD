package com.davanok.dvnkdnd.data.implementations

import com.davanok.dvnkdnd.data.repositories.FilesRepository
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath
import okio.SYSTEM
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class FilesRepositoryImpl(
    private val defaultDir: Path,
    private val cacheDir: Path
): FilesRepository {
    private val fs: FileSystem = FileSystem.SYSTEM

    override suspend fun write(bytes: ByteArray, path: Path) {
        val parent = path.parent
        if (parent != null && !fs.exists(parent)) fs.createDirectories(parent)
        fs.write(path) {
            write(bytes)
        }
    }
    override suspend fun read(path: Path): ByteArray {
        fs.read(path) {
            return readByteArray()
        }
    }
    override suspend fun delete(path: Path) {
        fs.delete(path)
    }

    override suspend fun move(from: Path, to: Path) {
        fs.atomicMove(from, to)
    }

    @OptIn(ExperimentalUuidApi::class)
    override fun getFilename(dir: Path, extension: String, temp: Boolean): Path {
        val root = if (temp) cacheDir else defaultDir
        var result = root / dir / (Uuid.random().toHexString() + "." + extension).toPath()
        return result
    }
}