package com.davanok.dvnkdnd.data.implementations

import com.davanok.dvnkdnd.data.repositories.FilesRepository
import io.github.aakira.napier.Napier
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
        Napier.d { "write: path: $path" }
        val parent = path.parent
        if (parent != null && !fs.exists(parent)) fs.createDirectories(parent)
        fs.write(path) {
            write(bytes)
        }
        Unit
    }.onFailure {
        Napier.e("Error in write", it)
    }
    override suspend fun read(path: Path): Result<ByteArray> = runCatching {
        Napier.d { "read: path: $path" }
        fs.read(path) {
            readByteArray()
        }
    }.onFailure {
        Napier.e("Error in read", it)
    }
    override suspend fun delete(path: Path) = runCatching {
        Napier.d { "delete: path: $path" }
        fs.delete(path)
    }.onFailure {
        Napier.e("Error in delete", it)
    }

    override suspend fun move(from: Path, to: Path) = runCatching {
        Napier.d { "move: from: $from, to: $to" }
        fs.atomicMove(from, to)
    }.onFailure {
        Napier.e("Error in move", it)
    }

    override fun getFilename(dir: Path, extension: String, temp: Boolean): Path {
        Napier.d { "getFilename: dir: $dir, extension: $extension, temp: $temp" }
        val root = if (temp) cacheDir else defaultDir
        val result = root / dir / (Uuid.random().toHexString() + "." + extension).toPath()
        return result
    }
}