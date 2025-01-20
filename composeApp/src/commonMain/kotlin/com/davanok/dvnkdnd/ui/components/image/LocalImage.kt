package com.davanok.dvnkdnd.ui.components.image

import io.github.vinceglb.filekit.core.PlatformFile


suspend fun PlatformFile.toByteArray(): ByteArray {
    return  if (supportsStreams()) {
        val size = getSize()
        if (size != null && size > 0L) {
            val buffer = ByteArray(size.toInt())
            val tmpBuffer = ByteArray(1000)
            var totalBytesRead = 0
            getStream().use {
                while (it.hasBytesAvailable()) {
                    val numRead = it.readInto(tmpBuffer, 1000)
                    tmpBuffer.copyInto(
                        buffer,
                        destinationOffset = totalBytesRead,
                        endIndex = numRead,
                    )
                    totalBytesRead += numRead
                }
            }
            buffer
        } else {
            readBytes()
        }
    } else {
        readBytes()
    }
}