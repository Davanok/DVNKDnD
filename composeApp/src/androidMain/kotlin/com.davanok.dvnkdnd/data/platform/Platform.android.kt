package com.davanok.dvnkdnd.data.platform

import android.os.Build

object AndroidPlatform: Platform {
    override val name: String = "Android"
    override val version: String = Build.VERSION.RELEASE
    override val model: String = "${Build.MANUFACTURER} ${Build.MODEL}"
    override val extra: Map<String, String> = emptyMap()
}

actual fun Platform.Companion.currentPlatform(): Platform = AndroidPlatform