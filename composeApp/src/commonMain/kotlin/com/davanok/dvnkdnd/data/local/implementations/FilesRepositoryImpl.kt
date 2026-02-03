package com.davanok.dvnkdnd.data.local.implementations

import com.davanok.dvnkdnd.core.utils.runLogging
import com.davanok.dvnkdnd.domain.DataDirectories
import com.davanok.dvnkdnd.domain.repositories.local.FilesRepository
import dev.zacsweers.metro.Inject
import okio.FileSystem
import okio.IOException
import okio.Path
import okio.Path.Companion.toPath
import okio.SYSTEM
import kotlin.uuid.Uuid

@Inject
class FilesRepositoryImpl(
    private val directories: DataDirectories
): FilesRepository {
    private val fs: FileSystem = FileSystem.SYSTEM

    override suspend fun write(bytes: ByteArray, path: Path) =
        runLogging("write file") {
            val parent = path.parent
            if (parent != null && !fs.exists(parent)) fs.createDirectories(parent)
            fs.write(path) { write(bytes) }
            Unit
        }
    override suspend fun read(path: Path): Result<ByteArray> =
        runLogging("read file") {
            fs.read(path) { readByteArray() }
        }
    override suspend fun delete(path: Path) =
        runLogging("delete file") {
            fs.delete(path)
        }

    override suspend fun move(from: Path, to: Path) =
        runLogging("move file") {
            val parent = to.parent
            if (parent != null && !fs.exists(parent)) fs.createDirectories(parent)
            try {
                fs.atomicMove(from, to)
            } catch (_: IOException) {
                fs.copy(from, to)
                fs.delete(from)
            }
        }

    override fun getFilename(dir: Path, extension: String, temp: Boolean): Path {
        val root = if (temp) directories.cacheDirectory else directories.dataDirectory
        val result = root / dir / (Uuid.random().toHexString() + "." + extension).toPath()
        return result
    }
}