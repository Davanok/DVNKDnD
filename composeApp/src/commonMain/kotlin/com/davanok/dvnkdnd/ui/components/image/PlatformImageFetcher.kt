package com.davanok.dvnkdnd.ui.components.image

import coil3.ImageLoader
import coil3.fetch.FetchResult
import coil3.fetch.Fetcher
import coil3.key.Keyer
import coil3.request.Options
import io.github.vinceglb.filekit.core.PlatformFile

class PlatformImageFetcher(
    private val platformFile: PlatformFile,
    private val options: Options,
    private val imageLoader: ImageLoader,
): Fetcher {
    override suspend fun fetch(): FetchResult? {
        val bytes = platformFile.toByteArray()
        val output = imageLoader.components.newFetcher(bytes, options, imageLoader)
        val (fetcher) = checkNotNull(output) { "no supported fetcher" }
        return fetcher.fetch()
    }

    class Factory : Fetcher.Factory<PlatformFile> {
        override fun create(
            data: PlatformFile,
            options: Options,
            imageLoader: ImageLoader
        ): Fetcher? {
            return PlatformImageFetcher(data, options, imageLoader)
        }
    }
}

class PlatformImageKeyer: Keyer<PlatformFile> {
    override fun key(
        data: PlatformFile,
        options: Options
    ): String? {
        return data.path
    }
}