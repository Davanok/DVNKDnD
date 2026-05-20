package com.davanok.dvnkdnd.data.local.implementations

import co.touchlab.kermit.Logger
import com.davanok.dvnkdnd.core.utils.runLogging
import com.davanok.dvnkdnd.domain.DataDirectories
import com.davanok.dvnkdnd.domain.repositories.local.FilesRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import okio.FileSystem
import okio.IOException
import okio.Path
import okio.Path.Companion.toPath
import okio.SYSTEM
import kotlin.uuid.Uuid

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(scope = AppScope::class)
class FilesRepositoryImpl(
    private val directories: DataDirectories,
    logger: Logger
): FilesRepository {
    private val logger = logger.withTag(TAG)
    private val fs: FileSystem = FileSystem.SYSTEM

    override suspend fun write(bytes: ByteArray, path: Path) =
        logger.runLogging("write file") {
            val parent = path.parent
            if (parent != null && !fs.exists(parent)) fs.createDirectories(parent)
            fs.write(path) { write(bytes) }
            Unit
        }
    override suspend fun read(path: Path): Result<ByteArray> =
        logger.runLogging("read file") {
            fs.read(path) { readByteArray() }
        }
    override suspend fun delete(path: Path) =
        logger.runLogging("delete file") {
            fs.delete(path)
        }

    override suspend fun move(from: Path, to: Path) =
        logger.runLogging("move file") {
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
    
    companion object {
        private const val TAG = "FilesRepository"
    }
}